package org.joo.atlas.tasks;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.promise4j.Promise;

public interface TaskRunner {

    Promise<TaskResult, Throwable> runTasks(Batch<Job> batch);

    void stop();
}
