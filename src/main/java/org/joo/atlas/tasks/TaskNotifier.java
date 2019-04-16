package org.joo.atlas.tasks;

import org.joo.atlas.models.TaskResult;
import org.joo.promise4j.Promise;

public interface TaskNotifier {

    Promise<TaskResult, Exception> notifyJobComplete(String batchId, String taskId, TaskResult result);

    Promise<Object, Exception>  notifyBatchStart(String batchId);
}
