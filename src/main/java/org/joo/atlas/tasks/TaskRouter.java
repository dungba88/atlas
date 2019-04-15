package org.joo.atlas.tasks;

import java.io.Serializable;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.promise4j.Promise;

public interface TaskRouter extends Serializable, Component {

    Promise<Object, Throwable> routeJob(TaskNotifier notifier, String routingKey, Job job, TaskResult result,
            Throwable cause);

    Promise<Object, Throwable> routeBatch(TaskNotifier notifier, String batchId);
}
