package org.joo.atlas.models;

public interface TaskResult {

    String getId();

    boolean isSuccessful();
    
    Throwable getCause();
    
    Object getResult();
}
