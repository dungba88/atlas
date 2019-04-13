package org.joo.atlas.tasks.impl;

import java.util.Map;

import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.joo.atlas.models.BatchExecution;
import org.joo.atlas.models.impl.DefaultBatchExecution;
import org.joo.atlas.tasks.TaskStorage;
import org.joo.promise4j.Promise;

public class MemBasedTaskStorage implements TaskStorage {

    private Map<String, BatchExecution> executionMap = new NonBlockingHashMap<>();

    @Override
    public Promise<BatchExecution, Exception> fetchBatchExecution(String batchId) {
        return Promise.of(executionMap.get(batchId));
    }

    @Override
    public Promise<Object, Exception> storeBatchExecution(String id, DefaultBatchExecution batchExecution) {
        executionMap.put(id, batchExecution);
        return Promise.of(null);
    }
}
