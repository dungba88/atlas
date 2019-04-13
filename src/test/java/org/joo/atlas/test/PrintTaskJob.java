package org.joo.atlas.test;

import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskTopo;
import org.joo.promise4j.Promise;

import lombok.Getter;

@Getter
public class PrintTaskJob implements Job {

    private TaskTopo taskTopo;

    public PrintTaskJob(TaskTopo taskTopo) {
        this.taskTopo = taskTopo;
    }

    @Override
    public Promise<TaskResult, Exception> run(ExecutionContext context) {
        System.out.println("Running task [" + context.getBatchId() + " - " + taskTopo.getTaskId() + "]");
        try {
            Thread.sleep((long) (Math.random() * (long) context.getArgs()[0]));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
//        System.out.println("Finish task [" + context.getBatchId() + " - " + taskTopo.getTaskId() + "]");
        return Promise.of(null);
    }
}
