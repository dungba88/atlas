package org.joo.atlas.tasks.impl.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.atlas.tasks.TaskStorage;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

public class PooledTaskRunner extends AbstractTaskQueue {

    private ExecutorService pool;

    public PooledTaskRunner(int workerThreads, TaskRouter router, TaskStorage storage) {
        super(router, storage);
        this.pool = new ForkJoinPool(workerThreads);
    }

    @Override
    protected Promise<TaskResult, Exception> doRunJob(Job job, ExecutionContext context) {
        var deferred = new CompletableDeferredObject<TaskResult, Exception>();
        pool.submit(() -> {
            job.run(context).forward(deferred);
        });
        return deferred.promise();
    }

    @Override
    public void onStop() {
        super.onStop();
        pool.shutdownNow();
    }

    @Override
    protected void onStart() {
        // Nothing to do here
    }
}
