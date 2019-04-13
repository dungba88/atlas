package org.joo.atlas.models.impl.results;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskResultStatus;

import lombok.Getter;

@Getter
public class CanceledTaskResult implements TaskResult {

    private String id;

    public CanceledTaskResult(String id) {
        this.id = id;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public TaskResultStatus getStatus() {
        return TaskResultStatus.CANCELED;
    }

    @Override
    public Throwable getCause() {
        return null;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public String toString() {
        return "Task canceled";
    }
}
