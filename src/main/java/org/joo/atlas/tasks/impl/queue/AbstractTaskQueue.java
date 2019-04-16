package org.joo.atlas.tasks.impl.queue;

import java.util.Arrays;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.BatchExecution;
import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.impl.DefaultBatchExecution;
import org.joo.atlas.models.impl.DefaultExecutionContext;
import org.joo.atlas.models.impl.results.DefaultTaskResult;
import org.joo.atlas.models.impl.results.FailedTaskResult;
import org.joo.atlas.tasks.TaskNotifier;
import org.joo.atlas.tasks.TaskQueue;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.atlas.tasks.TaskStorage;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import io.gridgo.framework.impl.NonameComponentLifecycle;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractTaskQueue extends NonameComponentLifecycle implements TaskQueue, TaskNotifier {

    private TaskStorage storage;

    private TaskRouter router;

    public AbstractTaskQueue(TaskRouter router, TaskStorage storage) {
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
    public Promise<Object, Exception> notifyBatchStart(String batchId) {
        return storage.fetchBatchExecution(batchId).then(batchExecution -> {
            runJobs(batchExecution, batchId, batchExecution.getBatch().getBatch());
            return Promise.of(null);
        });
    }

    @Override
    public Promise<TaskResult, Exception> notifyJobComplete(String batchId, String taskId, TaskResult result) {
        if (result == null)
            result = new DefaultTaskResult(taskId, null);
        var theResult = result;
        return storage.fetchBatchExecution(batchId).then(batchExecution -> {
            var job = batchExecution.mapTask(taskId);
            batchExecution.completeJob(job, theResult);
            if (!theResult.isSuccessful()) {
                return Promise.ofCause(new RuntimeException(theResult.getCause()));
            }
            runChildJobs(batchId, job, batchExecution);
            return Promise.of(theResult);
        });
    }

    protected void runJobs(BatchExecution batchExecution, String batchId, Job[] jobs) {
        for (var job : jobs) {
            if (!batchExecution.canRun(job))
                continue;
            var context = new DefaultExecutionContext(batchId, job.getTaskTopo().getTask().getTaskData());
            runJob(job, context);
        }
    }

    protected void runJob(Job job, ExecutionContext context) {
        var batchId = context.getBatchId();
        var taskId = job.getTaskTopo().getTaskId();
        doRunJob(job, context).then(result -> router.routeJob(this, batchId, job, result)) //
                              .fail(ex -> router.routeJob(this, batchId, job, new FailedTaskResult(taskId, ex)));
    }

    protected void runChildJobs(String batchId, Job job, BatchExecution batchExecution) {
        var jobs = Arrays.stream(job.getTaskTopo().getDependantTasks()) //
                         .map(batchExecution::mapTask) //
                         .toArray(size -> new Job[size]);
        runJobs(batchExecution, batchId, jobs);
    }

    @Override
    protected void onStart() {
        router.start();
    }

    @Override
    protected void onStop() {
        router.stop();
    }

    protected Promise<TaskResult, Exception> doRunJob(Job job, ExecutionContext context) {
        return Promise.of(null);
    }
}
