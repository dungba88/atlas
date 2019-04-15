package org.joo.atlas.tasks;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskResult;
import org.joo.promise4j.Promise;

public interface TaskSubmitter extends Component {

    Promise<TaskResult, Throwable> submitTasks(Batch<Task> batch);
}
