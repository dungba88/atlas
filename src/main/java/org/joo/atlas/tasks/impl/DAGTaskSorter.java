package org.joo.atlas.tasks.impl;

import org.joo.atlas.models.Batch;
import org.joo.atlas.models.Task;
import org.joo.atlas.models.TaskTopo;
import org.joo.atlas.models.impl.DefaultBatch;
import org.joo.atlas.tasks.TaskSorter;
import org.joo.atlas.tasks.impl.algorithm.DFSTopoSorting;
import org.joo.promise4j.Promise;

public class DAGTaskSorter implements TaskSorter {

    @Override
    public Promise<Batch<TaskTopo>, Throwable> sortTasks(Batch<Task> batch) {
        try {
            var sorter = new DFSTopoSorting(batch.getBatch());
            var sortedTasks = sorter.sort().topo();
            return Promise.of(new DefaultBatch<>(batch.getId(), sortedTasks));
        } catch (Exception ex) {
            return Promise.ofCause(ex);
        }
    }
}
