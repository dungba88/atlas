package org.joo.atlas.test.jobs;

import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskTopo;
import org.joo.promise4j.Promise;

import lombok.Getter;

@Getter
public class FailTaskJob implements Job {

    private static final long serialVersionUID = -4403082105580281768L;

    private TaskTopo taskTopo;

    public FailTaskJob(TaskTopo taskTopo) {
        this.taskTopo = taskTopo;
    }

    @Override
    public Promise<TaskResult, Exception> run(ExecutionContext context) {
        if (Math.random() > 0.5) {
            return Promise.ofCause(new RuntimeException("just failed"));
        }
        return Promise.of(null);
    }
}
