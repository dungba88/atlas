package org.joo.atlas.tasks.impl;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.BatchExecution;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.impl.DefaultBatchExecution;
import org.joo.atlas.models.impl.DefaultExecutionContext;
import org.joo.atlas.models.impl.DefaultTaskResult;
import org.joo.atlas.models.impl.FailedTaskResult;
import org.joo.atlas.tasks.TaskNotifier;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.atlas.tasks.TaskRunner;
import org.joo.atlas.tasks.TaskStorage;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

public class PooledTaskRunner implements TaskRunner, TaskNotifier {

    private TaskStorage storage;

    private ExecutorService pool;

    private TaskRouter router;

    public PooledTaskRunner(int workerThreads, TaskRouter router, TaskStorage storage) {
        this.pool = new ForkJoinPool(workerThreads);
        this.router = router;
        this.storage = storage;
    }

    @Override
    public Promise<TaskResult, Throwable> runTasks(Batch<Job> batch) {
        var deferred = new CompletableDeferredObject<TaskResult, Throwable>();
        var batchExecution = new DefaultBatchExecution(deferred, batch);
        return storage.storeBatchExecution(batch.getId(), batchExecution).then(r -> {
            router.routeBatch(this, batch.getId());
            return deferred.promise();
        });
    }

    @Override
    public Promise<Object, Throwable> notifyBatchStart(String batchId) {
        return storage.fetchBatchExecution(batchId).then(batchExecution -> {
            runJobs(batchExecution, batchId, batchExecution.getBatch().getBatch());
            return Promise.of(null);
        });
    }

    @Override
    public Promise<TaskResult, Throwable> notifyJobFailure(String batchId, Job job, Throwable ex, TaskResult result) {
        return storage.fetchBatchExecution(batchId).then(be -> {
            be.completeJob(job, new FailedTaskResult(job.getTaskTopo().getTaskId(), ex, result));
            return Promise.ofCause(ex);
        });
    }

    @Override
    public Promise<TaskResult, Throwable> notifyJobComplete(String batchId, Job job, TaskResult result) {
        if (result == null)
            result = new DefaultTaskResult(job.getTaskTopo().getTaskId(), null);
        if (!result.isSuccessful()) {
            return notifyJobFailure(batchId, job, result.getCause(), result);
        }
        var theResult = result;
        return storage.fetchBatchExecution(batchId).then(batchExecution -> {
            batchExecution.completeJob(job, theResult);
            runChildJobs(batchId, job, batchExecution);
            return Promise.of(theResult);
        });
    }

    protected void runJobs(BatchExecution batchExecution, String batchId, Job[] jobs) {
        for (var job : jobs) {
            if (!batchExecution.canRun(job))
                continue;
            pool.submit(() -> {
                runJob(batchId, job);
            });
        }
    }

    protected void runJob(String batchId, Job job) {
        try {
            var context = new DefaultExecutionContext(batchId, job.getTaskTopo().getTask().getTaskArguments());
            job.run(context) //
               .then(result -> router.routeJob(this, batchId, job, result, null))//
               .fail(ex -> router.routeJob(this, batchId, job, null, ex));
        } catch (Exception ex) {
            router.routeJob(this, batchId, job, null, ex);
        }
    }

    protected void runChildJobs(String batchId, Job job, BatchExecution batchExecution) {
        var jobs = Arrays.stream(job.getTaskTopo().getDependantTasks()) //
                         .map(batchExecution::mapTask) //
                         .toArray(size -> new Job[size]);
        runJobs(batchExecution, batchId, jobs);
    }

    @Override
    public void stop() {
        router.stop();
        pool.shutdownNow();
    }
}
