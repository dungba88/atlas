package org.joo.atlas.tasks.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.BatchExecution;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.impl.DefaultBatchExecution;
import org.joo.atlas.models.impl.DefaultTaskResult;
import org.joo.atlas.models.impl.FailedTaskResult;
import org.joo.atlas.tasks.TaskRunner;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

public class PooledTaskRunner implements TaskRunner {

    private Map<String, BatchExecution> executionMap = new ConcurrentHashMap<>();

    private ExecutorService[] notifiers;

    private ExecutorService pool;

    public PooledTaskRunner(int workerThreads, int notifierThreads) {
        this.pool = new ForkJoinPool(workerThreads);
        this.notifiers = new ExecutorService[notifierThreads];
        for (var i = 0; i < notifierThreads; i++) {
            notifiers[i] = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public Promise<TaskResult, Throwable> runTasks(Batch<Job> batch) {
        var deferred = new CompletableDeferredObject<TaskResult, Throwable>();
        var batchExecution = new DefaultBatchExecution(deferred, batch);
        executionMap.put(batch.getId(), batchExecution);
        runBatch(batchExecution);
        return deferred.promise();
    }

    private void runBatch(BatchExecution batchExecution) {
        var batch = batchExecution.getBatch();
        var batchId = batch.getId();
        var jobs = batch.getBatch();
        runJobs(batchExecution, batchId, jobs);
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
            job.run(job.getTaskTopo().getTask().getTaskArguments()) //
               .map(result -> notifyJob(batchId, job, result, null))//
               .fail(ex -> notifyJob(batchId, job, null, ex));
        } catch (Exception ex) {
            notifyJobFailure(batchId, job, ex, null);
        }
    }

    protected Future<Promise<TaskResult, Throwable>> notifyJob(String batchId, Job job, TaskResult result,
            Throwable cause) {
        var hash = batchId.hashCode();
        var notifier = this.notifiers[hash % this.notifiers.length];
        if (cause == null)
            return notifier.submit(() -> notifyJobComplete(batchId, job, result));
        return notifier.submit(() -> notifyJobFailure(batchId, job, cause, null));
    }

    protected Promise<TaskResult, Throwable> notifyJobFailure(String batchId, Job job, Throwable ex,
            TaskResult result) {
        var batchExecution = executionMap.get(batchId);
        batchExecution.completeJob(job, new FailedTaskResult(job.getTaskTopo().getTaskId(), ex, result));
        return Promise.ofCause(ex);
    }

    protected Promise<TaskResult, Throwable> notifyJobComplete(String batchId, Job job, TaskResult result) {
        if (result == null)
            result = new DefaultTaskResult(job.getTaskTopo().getTaskId(), null);
        if (!result.isSuccessful()) {
            return notifyJobFailure(batchId, job, result.getCause(), result);
        }
        var batchExecution = executionMap.get(batchId);
        batchExecution.completeJob(job, result);

        var jobs = Arrays.stream(job.getTaskTopo().getDependantTasks()) //
                         .map(batchExecution::mapTask) //
                         .toArray(size -> new Job[size]);

        runJobs(batchExecution, batchId, jobs);

        return Promise.of(result);
    }

    @Override
    public void stop() {
        for (var notifier : notifiers) {
            notifier.shutdownNow();
        }
        pool.shutdownNow();
    }
}
