package org.joo.atlas.models.impl;

import org.joo.atlas.models.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultTask implements Task {

    private String id;

    private String name;
    
    private String type;
    
    private String[] dependants;
    
    private Object[] taskArguments;
}