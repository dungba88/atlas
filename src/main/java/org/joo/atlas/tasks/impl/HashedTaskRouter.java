package org.joo.atlas.tasks.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.tasks.TaskNotifier;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.promise4j.Promise;

public class HashedTaskRouter implements TaskRouter {

    private ExecutorService[] routers;

    public HashedTaskRouter(int routerThreads) {
        this.routers = new ExecutorService[routerThreads];
        for (var i = 0; i < routerThreads; i++) {
            routers[i] = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public Promise<Object, Throwable> notifyJob(TaskNotifier notifier, String routingKey, Job job, TaskResult result,
            Throwable cause) {
        var hash = routingKey.hashCode();
        var router = this.routers[hash % this.routers.length];
        if (cause == null) {
            router.submit(() -> notifier.notifyJobComplete(routingKey, job, result));
        } else {
            router.submit(() -> notifier.notifyJobFailure(routingKey, job, cause, null));
        }
        return Promise.of(null);
    }

    @Override
    public void stop() {
        for (var router : routers) {
            router.shutdownNow();
        }
    }
}
