package org.joo.atlas.models;

public interface TaskTopo {

    String getTaskId();

    int getTaskGroup();

    Task getTask();

    String[] getDependantTasks();

    String[] getDependedTasks();
}
