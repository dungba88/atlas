package org.joo.atlas.models.impl;

import org.joo.atlas.models.ExecutionContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultExecutionContext implements ExecutionContext {

    private String batchId;
    
    private Object[] args;
}
