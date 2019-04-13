package org.joo.atlas.models.impl;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskResultStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultTaskResult implements TaskResult {

    private String id;

    private Object result;

    private Throwable cause;

    public DefaultTaskResult(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }

    @Override
    public TaskResultStatus getStatus() {
        return TaskResultStatus.FINISHED;
    }

    @Override
    public String toString() {
        return "Task completed sucessfully";
    }
}
