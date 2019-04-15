package org.joo.atlas.tasks;

import org.joo.atlas.models.TaskResult;
import org.joo.promise4j.Promise;

public interface TaskNotifier {

    Promise<TaskResult, Throwable> notifyJobComplete(String batchId, String taskId, TaskResult result);

    Promise<Object, Throwable>  notifyBatchStart(String batchId);
}
