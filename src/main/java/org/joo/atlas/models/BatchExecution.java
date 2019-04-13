package org.joo.atlas.models;

import java.util.Map;

import org.joo.promise4j.Deferred;

public interface BatchExecution {

    Deferred<TaskResult, Throwable> getDeferred();

    Batch<Job> getBatch();
    
    Map<String, TaskResult> getCompletedJobs();
    
    Job mapTask(String taskId);

    void completeJob(Job job, TaskResult result);

    boolean canRun(Job job);

    boolean isCompleted();
}
