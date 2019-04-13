package org.joo.atlas.models.impl;

import org.joo.atlas.models.TaskResult;

import lombok.Getter;

@Getter
public class FailedTaskResult implements TaskResult {

    private String id;

    private Throwable cause;

    private TaskResult result;

    public FailedTaskResult(String id, Throwable cause, TaskResult result) {
        this.id = id;
        this.cause = cause;
        this.result = result;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }
}
