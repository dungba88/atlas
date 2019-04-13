package org.joo.atlas.models;

public interface TaskResult {

    String getId();

    TaskResultStatus getStatus();

    boolean isSuccessful();

    Throwable getCause();

    Object getResult();
}
