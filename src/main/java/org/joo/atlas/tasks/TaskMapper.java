package org.joo.atlas.tasks;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskTopo;

public interface TaskMapper {

    Job mapTask(TaskTopo taskTopo);
}
