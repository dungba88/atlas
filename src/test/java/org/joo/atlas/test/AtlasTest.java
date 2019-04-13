package org.joo.atlas.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.tasks.impl.DAGTaskSorter;
import org.joo.atlas.tasks.impl.DefaultTaskMapper;
import org.joo.atlas.tasks.impl.DefaultTaskSubmitter;
import org.joo.atlas.tasks.impl.HashedTaskRouter;
import org.joo.atlas.tasks.impl.PooledTaskRunner;
import org.junit.Assert;
import org.junit.Test;

public class AtlasTest {

    @Test
    public void testGraph() throws InterruptedException {
        var taskRouter = new HashedTaskRouter(2);
        var taskMapper = new DefaultTaskMapper().with("test-task", PrintTaskJob::new);
        var submitter = new DefaultTaskSubmitter(new DAGTaskSorter(), new PooledTaskRunner(16, taskRouter), taskMapper);
        var batch = Batch.of("test", //
                Task.of("4", "task4", "test-task", new String[] { "6" }, 100L),
                Task.of("2", "task2", "test-task", new String[] { "3" }, 100L),
                Task.of("3", "task3", "test-task", new String[] { "4", "5" }, 100L),
                Task.of("1", "task1", "test-task", new String[] { "2", "3" }, 100L),
                Task.of("5", "task5", "test-task", new String[] { "6" }, 300L), //
                Task.of("7", "task7", "test-task", new String[] { "2" }, 100L), //
                Task.of("6", "task6", "test-task", new String[0], 100L), //
                Task.of("8", "task8", "test-task", new String[0], 500L));
        var exRef = new AtomicReference<Throwable>();
        var latch = new CountDownLatch(1);
        submitter.submitTasks(batch) //
                 .always((s, r, e) -> {
                     if (e != null)
                         exRef.set(e);
                     latch.countDown();
                 });
        latch.await();
        Assert.assertNull(exRef.get());
        submitter.stop();
    }
}
