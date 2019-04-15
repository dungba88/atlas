package org.joo.atlas.tasks.impl.routers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.tasks.TaskNotifier;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.atlas.tasks.impl.AbstractComponent;
import org.joo.promise4j.Promise;

public class HashedTaskRouter extends AbstractComponent implements TaskRouter {

    private static final long serialVersionUID = 3542951723310626472L;

    private ExecutorService[] routers;

    public HashedTaskRouter(int routerThreads) {
        this.routers = new ExecutorService[routerThreads];
        for (var i = 0; i < routerThreads; i++) {
            routers[i] = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public Promise<Object, Throwable> routeJob(TaskNotifier notifier, String routingKey, Job job, TaskResult result,
            Throwable cause) {
        var router = findRouter(routingKey);
        if (cause == null) {
            router.submit(() -> notifier.notifyJobComplete(routingKey, job, result));
        } else {
            router.submit(() -> notifier.notifyJobFailure(routingKey, job, cause, null));
        }
        return Promise.of(null);
    }

    @Override
    protected void onStop() {
        for (var router : routers) {
            router.shutdownNow();
        }
    }

    @Override
    public Promise<Object, Throwable> routeBatch(TaskNotifier notifier, String batchId) {
        var router = findRouter(batchId);
        router.submit(() -> notifier.notifyBatchStart(batchId));
        return Promise.of(null);
    }

    protected ExecutorService findRouter(String routingKey) {
        var hash = routingKey.hashCode();
        return this.routers[hash % this.routers.length];
    }
}
