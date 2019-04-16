package org.joo.atlas.test;

import java.util.ArrayList;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.tasks.impl.DefaultTaskMapper;
import org.joo.atlas.tasks.impl.DefaultTaskSubmitter;
import org.joo.atlas.tasks.impl.queue.BlockingHazelcastTaskRunner;
import org.joo.atlas.tasks.impl.routers.HashedTaskRouter;
import org.joo.atlas.tasks.impl.storages.MemBasedTaskStorage;
import org.joo.atlas.test.jobs.NopTaskJob;
import org.joo.promise4j.Promise;
import org.joo.promise4j.PromiseException;
import org.junit.Test;

public class AtlasHazelcastTest extends AtlasBaseTest {

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
}
