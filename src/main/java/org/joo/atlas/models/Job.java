package org.joo.atlas.models;

import org.joo.promise4j.Promise;

public interface Job {

    TaskTopo getTaskTopo();

    Promise<TaskResult, Exception> run(Object[] args);
}
