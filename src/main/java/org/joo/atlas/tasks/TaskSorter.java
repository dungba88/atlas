package org.joo.atlas.tasks;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskTopo;
import org.joo.promise4j.Promise;

public interface TaskSorter {

    Promise<Batch<TaskTopo>, Exception> sortTasks(Batch<Task> batch);
}
