package org.joo.atlas.models.impl;

import org.joo.atlas.models.Batch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultBatch<T> implements Batch<T> {

    private String id;
    
    private T[] batch;
}