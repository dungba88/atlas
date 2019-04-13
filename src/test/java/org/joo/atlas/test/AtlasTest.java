package org.joo.atlas.test;

import java.util.ArrayList;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.support.exceptions.CyclicGraphDetectedException;
import org.joo.atlas.tasks.impl.DefaultTaskMapper;
import org.joo.atlas.tasks.impl.DefaultTaskSubmitter;
import org.joo.atlas.tasks.impl.HashedTaskRouter;
import org.joo.atlas.tasks.impl.PooledTaskRunner;
import org.joo.promise4j.Promise;
import org.joo.promise4j.PromiseException;
import org.junit.Assert;
import org.junit.Test;

public class AtlasTest {

    @Test
    public void testCircularDependency() throws PromiseException, InterruptedException {
        var taskRouter = new HashedTaskRouter(2);
        var taskMapper = new DefaultTaskMapper().with("test-task", PrintTaskJob::new);
        var submitter = new DefaultTaskSubmitter(new PooledTaskRunner(16, taskRouter), taskMapper);

        var batch = createBatchWithCircularDependency();

        try {
            submitter.submitTasks(batch).get();
            Assert.fail("must fail with cyclic graph detected exception");
        } catch (CyclicGraphDetectedException ex) {
        }

        submitter.stop();
    }

    private Batch<Task> createBatchWithCircularDependency() {
        var batch = Batch.of("circular-batch", //
                Task.of("4", "task4", "test-task", new String[] { "6" }, 100L),
                Task.of("2", "task2", "test-task", new String[] { "3" }, 100L),
                Task.of("3", "task3", "test-task", new String[] { "4", "5" }, 100L),
                Task.of("1", "task1", "test-task", new String[] { "2", "3" }, 100L),
                Task.of("5", "task5", "test-task", new String[] { "6" }, 300L), //
                Task.of("7", "task7", "test-task", new String[] { "2" }, 100L), //
                Task.of("6", "task6", "test-task", new String[] { "1" }, 100L), //
                Task.of("8", "task8", "test-task", new String[0], 500L));
        return batch;
    }

    @Test
    public void testGraph() throws InterruptedException, PromiseException {
        var taskRouter = new HashedTaskRouter(2);
        var taskMapper = new DefaultTaskMapper().with("test-task", PrintTaskJob::new);
        var submitter = new DefaultTaskSubmitter(new PooledTaskRunner(16, taskRouter), taskMapper);

        var promises = new ArrayList<Promise<TaskResult, Throwable>>();
        for (var i = 0; i < 5; i++) {
            var batch = createBatch("test" + i);
            var promise = submitter.submitTasks(batch);
            promises.add(promise);
        }

        Promise.all(promises).get();

        submitter.stop();
    }

    protected Batch<Task> createBatch(String batchId) {
        var batch = Batch.of(batchId, //
                Task.of("4", "task4", "test-task", new String[] { "6" }, 100L),
                Task.of("2", "task2", "test-task", new String[] { "3" }, 100L),
                Task.of("3", "task3", "test-task", new String[] { "4", "5" }, 100L),
                Task.of("1", "task1", "test-task", new String[] { "2", "3" }, 100L),
                Task.of("5", "task5", "test-task", new String[] { "6" }, 300L), //
                Task.of("7", "task7", "test-task", new String[] { "2" }, 100L), //
                Task.of("6", "task6", "test-task", new String[0], 100L), //
                Task.of("8", "task8", "test-task", new String[0], 500L));
        return batch;
    }
}
