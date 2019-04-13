package org.joo.atlas.tasks.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.joo.atlas.tasks.TaskRouter;
import org.joo.atlas.tasks.TaskStorage;

public class PooledTaskRunner extends AbstractTaskRunner {

    private ExecutorService pool;

    public PooledTaskRunner(int workerThreads, TaskRouter router, TaskStorage storage) {
        super(router, storage);
        this.pool = new ForkJoinPool(workerThreads);
    }

    @Override
    protected void doRunJob(Runnable runnable) {
        pool.submit(runnable);
    }

    @Override
    public void stop() {
        super.stop();
        pool.shutdownNow();
    }
}
