package org.joo.atlas.tasks;

import org.joo.atlas.models.BatchExecution;
import org.joo.atlas.models.impl.DefaultBatchExecution;
import org.joo.promise4j.Promise;

public interface TaskStorage {

    Promise<BatchExecution, Exception> fetchBatchExecution(String batchId);

    Promise<Object, Exception> storeBatchExecution(String id, DefaultBatchExecution batchExecution);
}
