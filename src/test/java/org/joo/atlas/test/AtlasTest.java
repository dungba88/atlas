package org.joo.atlas.test;

import java.util.concurrent.CountDownLatch;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.tasks.impl.DAGTaskSorter;
import org.joo.atlas.tasks.impl.DefaultTaskMapper;
import org.joo.atlas.tasks.impl.DefaultTaskSubmitter;
import org.joo.atlas.tasks.impl.PooledTaskRunner;

public class AtlasTest {

    public static void main(String[] args) throws InterruptedException {
        var taskMapper = new DefaultTaskMapper().with("test-task", PrintTaskJob::new);
        var submitter = new DefaultTaskSubmitter(new DAGTaskSorter(), new PooledTaskRunner(16, 2), taskMapper);
        var batch = Batch.of("test", //
                Task.of("4", "task4", "test-task", new String[] { "6" }, 100L),
                Task.of("2", "task2", "test-task", new String[] { "3" }, 100L),
                Task.of("3", "task3", "test-task", new String[] { "4", "5" }, 100L),
                Task.of("1", "task1", "test-task", new String[] { "2", "3" }, 100L),
                Task.of("5", "task5", "test-task", new String[] { "6" }, 300L), //
                Task.of("7", "task7", "test-task", new String[] { "2" }, 100L), //
                Task.of("6", "task6", "test-task", new String[0], 100L), //
                Task.of("8", "task8", "test-task", new String[0], 500L));
        var latch = new CountDownLatch(1);
        var promise = submitter.submitTasks(batch);
        promise.always((s, r, e) -> {
            if (e != null)
                e.printStackTrace();
            latch.countDown();
        });
        latch.await();
        submitter.stop();
    }
}
