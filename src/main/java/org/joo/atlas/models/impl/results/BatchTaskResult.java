package org.joo.atlas.models.impl.results;

import java.util.Optional;
import java.util.stream.Collectors;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskResultStatus;
import org.joo.atlas.support.exceptions.BatchException;

import io.gridgo.bean.BArray;
import lombok.Getter;

@Getter
public class BatchTaskResult implements TaskResult {

    private String id;

    private boolean successful;

    private Throwable cause;

    private BArray result;

    private TaskResultStatus status;

    public BatchTaskResult(String id, BArray results) {
        this.id = id;
        this.result = results;
        checkForFailure();
    }

    private void checkForFailure() {
        var failures = result.stream() //
                             .map(e -> e.asReference().<TaskResult>getReference())
                             .filter(e -> !e.isSuccessful()) //
                             .collect(Collectors.toMap(e -> e.getId(), e -> Optional.ofNullable(e.getCause())));
        successful = failures.isEmpty();
        if (!successful) {
            cause = new BatchException(failures);
            status = TaskResultStatus.FAILED;
        } else {
            status = TaskResultStatus.FINISHED;
        }
    }
}
