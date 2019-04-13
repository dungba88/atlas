package org.joo.atlas.models.impl;

import org.joo.atlas.models.TaskResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultTaskResult implements TaskResult {
    
    private String id;
    
    private boolean successful;

    private Object result;
    
    private Throwable cause;
    
    public DefaultTaskResult(String id, Object result) {
        this.id = id;
        this.result = result;
        this.successful = true;
    }
}
