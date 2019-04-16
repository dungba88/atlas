package org.joo.atlas.models.impl.results;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskResultStatus;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class FailedTaskResult implements TaskResult {

    private String id;

    private Throwable cause;

    public FailedTaskResult(String id, @NonNull Throwable cause) {
        this.id = id;
        this.cause = cause;
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
    public Object getResult() {
        return null;
    }

    @Override
    public String toString() {
        return "Task failed with exception [" + cause + "]";
    }
}
