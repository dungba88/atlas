package org.joo.atlas.tasks.impl;

import java.util.Arrays;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Job;
import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskTopo;
import org.joo.atlas.models.impl.DefaultBatch;
import org.joo.atlas.tasks.TaskMapper;
import org.joo.atlas.tasks.TaskQueue;
import org.joo.atlas.tasks.TaskSorter;
import org.joo.atlas.tasks.TaskSubmitter;
import org.joo.promise4j.Promise;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultTaskSubmitter extends AbstractComponent implements TaskSubmitter {

    private final TaskSorter taskSorter;

    private final TaskQueue taskRunner;

    private final TaskMapper taskMapper;

    public DefaultTaskSubmitter(TaskQueue taskRunner, TaskMapper taskMapper) {
        this(new DAGTaskSorter(), taskRunner, taskMapper);
    }

    @Override
    public Promise<TaskResult, Throwable> submitTasks(Batch<Task> batch) {
        return taskSorter.sortTasks(batch) //
                         .map(this::mapTasks) //
                         .then(taskRunner::runTasks);
    }

    private Batch<Job> mapTasks(Batch<TaskTopo> batch) {
        var jobs = Arrays.stream(batch.getBatch()) //
                         .map(taskMapper::mapTask) //
                         .toArray(size -> new Job[size]);
        return new DefaultBatch<>(batch.getId(), jobs);
    }
    
    @Override
    protected void onStart() {
        taskRunner.start();
    }

    @Override
    protected void onStop() {
        taskRunner.stop();
    }
}
