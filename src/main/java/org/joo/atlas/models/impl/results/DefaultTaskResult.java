package org.joo.atlas.models.impl.results;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.models.TaskResultStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultTaskResult implements TaskResult {

    private String id;

    private Object result;

    public DefaultTaskResult() {

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

    @Override
    public Throwable getCause() {
        return null;
    }
}
