package org.joo.atlas.models.impl;

import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskTopo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultTaskTopo implements TaskTopo {

    private Task task;
    
    private int taskGroup;

    private String[] dependedTasks;
    
    @Override
    public String getTaskId() {
        return task.getId();
    }

    @Override
    public String[] getDependantTasks() {
        return task.getDependants();
    }
}