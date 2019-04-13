package org.joo.atlas.models.impl.results;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskResultStatus;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class FailedTaskResult implements TaskResult {

    private String id;

    private Throwable cause;

    private TaskResult result;

    public FailedTaskResult(String id, @NonNull Throwable cause, TaskResult result) {
        this.id = id;
        this.cause = cause;
        this.result = result;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public TaskResultStatus getStatus() {
        return TaskResultStatus.FAILED;
    }

    @Override
    public String toString() {
        return "Task failed with exception [" + cause + "]";
    }
}
