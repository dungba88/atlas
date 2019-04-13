package org.joo.atlas.tasks.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskTopo;
import org.joo.atlas.tasks.TaskMapper;

public class DefaultTaskMapper implements TaskMapper {

    private Map<String, Function<TaskTopo, Job>> map = new HashMap<>();

    public DefaultTaskMapper with(String taskType, Function<TaskTopo, Job> job) {
        map.put(taskType, job);
        return this;
    }

    @Override
    public Job mapTask(TaskTopo taskTopo) {
        return map.get(taskTopo.getTask().getType()).apply(taskTopo);
    }
}
