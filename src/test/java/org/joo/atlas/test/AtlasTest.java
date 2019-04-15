package org.joo.atlas.test;

import java.util.ArrayList;
import java.util.Collections;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.impl.results.BatchTaskResult;
import org.joo.atlas.support.exceptions.CyclicGraphDetectedException;
import org.joo.atlas.tasks.impl.DefaultTaskMapper;
import org.joo.atlas.tasks.impl.DefaultTaskSubmitter;
import org.joo.atlas.tasks.impl.queue.BlockingHazelcastTaskRunner;
import org.joo.atlas.tasks.impl.queue.PooledTaskRunner;
import org.joo.atlas.tasks.impl.routers.HashedTaskRouter;
import org.joo.atlas.tasks.impl.storages.MemBasedTaskStorage;
import org.joo.promise4j.Promise;
import org.joo.promise4j.PromiseException;
import org.junit.Assert;
import org.junit.Test;

public class AtlasTest {

    @Test
    public void testCircularDependency() throws PromiseException, InterruptedException {
        var taskMapper = new DefaultTaskMapper().with("test-task", PrintTaskJob::new);
        var taskStorage = new MemBasedTaskStorage();
        var taskRouter = new HashedTaskRouter(2);
        var taskRunner = new PooledTaskRunner(16, taskRouter, taskStorage);
        var submitter = new DefaultTaskSubmitter(taskRunner, taskMapper);

        submitter.start();

        var batch = createBatchWithCircularDependency();

        try {
            submitter.submitTasks(batch).get();
            Assert.fail("must fail with cyclic graph detected exception");
        } catch (PromiseException ex) {
            if (!(ex.getCause() instanceof CyclicGraphDetectedException)) {
                Assert.fail(ex.getCause().getMessage());
            }
        }

        submitter.stop();
    }

    private Batch<Task> createBatchWithCircularDependency() {
        var batch = Batch.of("circular-batch", //
                Task.of("4", "task4", "test-task", new String[] { "6" }, Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("2", "task2", "test-task", new String[] { "3" }, Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("3", "task3", "test-task", new String[] { "4", "5" },
                        Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("1", "task1", "test-task", new String[] { "2", "3" },
                        Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("5", "task5", "test-task", new String[] { "6" }, Collections.singletonMap("sleepTimeMs", 300L)), //
                Task.of("7", "task7", "test-task", new String[] { "2" }, Collections.singletonMap("sleepTimeMs", 100L)), //
                Task.of("6", "task6", "test-task", new String[] { "1" }, Collections.singletonMap("sleepTimeMs", 100L)), //
                Task.of("8", "task8", "test-task", new String[0], Collections.singletonMap("sleepTimeMs", 500L)));
        return batch;
    }

    @Test
    public void testRandomFailure() throws InterruptedException, PromiseException {
        var taskMapper = new DefaultTaskMapper().with("test-task", FailTaskJob::new);
        var taskStorage = new MemBasedTaskStorage();
        var taskRouter = new HashedTaskRouter(2);
        var taskRunner = new PooledTaskRunner(16, taskRouter, taskStorage);
        var submitter = new DefaultTaskSubmitter(taskRunner, taskMapper);

        submitter.start();

        var promises = new ArrayList<Promise<TaskResult, Throwable>>();
        for (var i = 0; i < 10; i++) {
            var batch = createBatch("test" + i);
            var promise = submitter.submitTasks(batch);
            promises.add(promise);
        }

        var results = Promise.all(promises).get();
        for (var result : results) {
            var batchResult = (BatchTaskResult) result;
            System.out.println(batchResult.getResult());
        }

        submitter.stop();
    }

    @Test
    public void testGraph() throws InterruptedException, PromiseException {
        var taskMapper = new DefaultTaskMapper().with("test-task", PrintTaskJob::new);
        var taskStorage = new MemBasedTaskStorage();
        var taskRouter = new HashedTaskRouter(2);
        var taskRunner = new PooledTaskRunner(16, taskRouter, taskStorage);
        var submitter = new DefaultTaskSubmitter(taskRunner, taskMapper);

        submitter.start();

        var promises = new ArrayList<Promise<TaskResult, Throwable>>();
        for (var i = 0; i < 5; i++) {
            var batch = createBatch("test" + i);
            var promise = submitter.submitTasks(batch);
            promises.add(promise);
        }

        Promise.all(promises).get();

        submitter.stop();
    }

    @Test
    public void testHazelcast() throws InterruptedException, PromiseException {
        var taskMapper = new DefaultTaskMapper().with("test-task", NopTaskJob::new);
        var taskStorage = new MemBasedTaskStorage();
        var taskRouter = new HashedTaskRouter(2);
        var taskRunner = new BlockingHazelcastTaskRunner("default", taskRouter, taskStorage);
        var submitter = new DefaultTaskSubmitter(taskRunner, taskMapper);

        submitter.start();

        var iterations = 100;
        var started = System.nanoTime();
        var promises = new ArrayList<Promise<TaskResult, Throwable>>();
        for (var i = 0; i < iterations; i++) {
            var batch = createBatch("test" + i);
            var promise = submitter.submitTasks(batch);
            promises.add(promise);
        }

        Promise.all(promises).get();
        var elapsed = System.nanoTime() - started;
        System.out.println(elapsed / 1e6 + "ms for " + (iterations * 8) + " tasks");

        submitter.stop();
    }

    protected Batch<Task> createBatch(String batchId) {
        var batch = Batch.of(batchId, //
                Task.of("4", "task4", "test-task", new String[] { "6" }, Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("2", "task2", "test-task", new String[] { "3" }, Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("3", "task3", "test-task", new String[] { "4", "5" },
                        Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("1", "task1", "test-task", new String[] { "2", "3" },
                        Collections.singletonMap("sleepTimeMs", 100L)),
                Task.of("5", "task5", "test-task", new String[] { "6" }, Collections.singletonMap("sleepTimeMs", 300L)), //
                Task.of("7", "task7", "test-task", new String[] { "2" }, Collections.singletonMap("sleepTimeMs", 100L)), //
                Task.of("6", "task6", "test-task", new String[0], Collections.singletonMap("sleepTimeMs", 100L)), //
                Task.of("8", "task8", "test-task", new String[0], Collections.singletonMap("sleepTimeMs", 500L)));
        return batch;
    }
}
