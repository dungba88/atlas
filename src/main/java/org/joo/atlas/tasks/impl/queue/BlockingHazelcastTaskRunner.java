package org.joo.atlas.tasks.impl.queue;

import java.util.concurrent.CompletableFuture;

import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.support.exceptions.ExecutionException;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.atlas.tasks.TaskStorage;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.CompletableDeferredObject;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

public class BlockingHazelcastTaskRunner extends AbstractTaskQueue {

    private HazelcastInstance hzc;

    private IExecutorService pool;

    public BlockingHazelcastTaskRunner(String serviceName, TaskRouter router, TaskStorage storage) {
        super(router, storage);
        this.hzc = Hazelcast.newHazelcastInstance();
        this.pool = hzc.getExecutorService(serviceName);
    }

    @Override
    protected Promise<TaskResult, Exception> doRunJob(Job job, ExecutionContext context) {
        var hzcJob = new HazelcastCallableJob();
        hzcJob.setContext(context);
        hzcJob.setJob(job);

        var completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return pool.submit(hzcJob).get();
            } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                throw new ExecutionException("Exception caught while trying to execute task with Hazelcast", e);
            }
        });
        return new CompletableDeferredObject<>(completableFuture);
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
