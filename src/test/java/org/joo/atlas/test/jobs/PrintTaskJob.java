package org.joo.atlas.test.jobs;

import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskTopo;
import org.joo.atlas.models.impl.results.DefaultTaskResult;
import org.joo.promise4j.Promise;

import io.gridgo.bean.BObject;
import lombok.Getter;

@Getter
public class PrintTaskJob implements Job {

    private static final long serialVersionUID = 6875856662528294631L;

    private TaskTopo taskTopo;

    public PrintTaskJob(TaskTopo taskTopo) {
        this.taskTopo = taskTopo;
    }

    @Override
    public Promise<TaskResult, Exception> run(ExecutionContext context) {
        System.out.println("Running task [" + context.getBatchId() + " - " + taskTopo.getTaskId() + "]");
        try {
            Thread.sleep((long) (Math.random() * (long) context.getContextData().get("sleepTimeMs")));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Finish task [" + context.getBatchId() + " - " + taskTopo.getTaskId() + "]");
        return Promise.of(new DefaultTaskResult(taskTopo.getTaskId(), BObject.of("test", "xyz")));
    }
}
