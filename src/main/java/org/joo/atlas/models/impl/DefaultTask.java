package org.joo.atlas.models.impl;

import java.util.Map;

import org.joo.atlas.models.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultTask implements Task {

    private static final long serialVersionUID = 7251495568683098285L;

    private String id;

    private String name;
    
    private String type;
    
    private String[] dependants;
    
    private Map<String, Object> taskData;
}