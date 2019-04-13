package org.joo.atlas.models.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.BatchExecution;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.impl.results.BatchTaskResult;
import org.joo.atlas.models.impl.results.CanceledTaskResult;
import org.joo.promise4j.Deferred;

import lombok.Getter;

@Getter
public class DefaultBatchExecution implements BatchExecution {

    private Deferred<TaskResult, Throwable> deferred;

    private Batch<Job> batch;

    private Map<String, TaskResult> completedJobs = new HashMap<>();

    private Map<String, Job> taskMap = new HashMap<>();

    public DefaultBatchExecution(Deferred<TaskResult, Throwable> deferred, Batch<Job> batch) {
        this.deferred = deferred;
        this.batch = batch;
        buildTaskMap();
    }

    private void buildTaskMap() {
        for (var job : batch.getBatch()) {
            taskMap.put(job.getTaskTopo().getTaskId(), job);
        }
    }

    @Override
    public boolean canRun(Job job) {
        var depended = job.getTaskTopo().getDependedTasks();
        if (depended.length == 0)
            return true;
        return Arrays.stream(depended) //
                     .allMatch(this::hasCompleted);
    }

    private boolean hasCompleted(String task) {
        return completedJobs.containsKey(task) && completedJobs.get(task).isSuccessful();
    }

    @Override
    public void completeJob(Job job, TaskResult result) {
        completedJobs.put(job.getTaskTopo().getTaskId(), result);
        // canceled children of failed task
        if (!result.isSuccessful()) {
            for (var child : job.getTaskTopo().getDependantTasks()) {
                completeJob(taskMap.get(child), new CanceledTaskResult(child));
            }
        }
        checkComplete();
    }

    protected void checkComplete() {
        if (isCompleted()) {
            deferred.resolve(new BatchTaskResult(batch.getId(), completedJobs));
        }
    }

    @Override
    public boolean isCompleted() {
        return completedJobs.size() == batch.getBatch().length;
    }

    @Override
    public Job mapTask(String taskId) {
        return taskMap.get(taskId);
    }
}
