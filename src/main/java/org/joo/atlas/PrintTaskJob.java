package org.joo.atlas;

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
    public Promise<TaskResult, Exception> run(Object[] args) {
        System.out.println("Running task [" + taskTopo.getTaskId() + "]");
        try {
            Thread.sleep((long) args[0]);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Finish task [" + taskTopo.getTaskId() + "]");
        return Promise.of(null);
    }
}
