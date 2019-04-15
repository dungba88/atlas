package org.joo.atlas.models;

import java.io.Serializable;

public interface TaskTopo extends Serializable {

    String getTaskId();

    int getTaskGroup();

    Task getTask();

    String[] getDependantTasks();

    String[] getDependedTasks();
}
