package org.joo.atlas.models.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.support.exceptions.BatchException;

import lombok.Getter;

@Getter
public class BatchTaskResult implements TaskResult {

    private String id;

    private boolean successful;

    private Throwable cause;

    private Map<String, TaskResult> result;

    public BatchTaskResult(String id, Map<String, TaskResult> results) {
        this.id = id;
        this.result = results;
        checkForFailure();
    }

    private void checkForFailure() {
        var failures = result.entrySet().stream() //
                             .filter(e -> !e.getValue().isSuccessful())
                             .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getCause()));
        successful = failures.isEmpty();
        if (!successful) {
            cause = new BatchException(failures);
        }
    }
}
