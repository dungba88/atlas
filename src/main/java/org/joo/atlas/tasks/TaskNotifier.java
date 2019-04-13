package org.joo.atlas.tasks;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.promise4j.Promise;

public interface TaskNotifier {

    Promise<TaskResult, Throwable> notifyJobComplete(String batchId, Job job, TaskResult result);

    Promise<TaskResult, Throwable> notifyJobFailure(String batchId, Job job, Throwable ex, TaskResult result);

    Promise<Object, Throwable>  notifyBatchStart(String batchId);
}
