package org.joo.atlas.models;

import org.joo.atlas.models.impl.DefaultBatch;

public interface Batch<T> {

    String getId();
    
    T[] getBatch();
    
    @SuppressWarnings("unchecked")
    static <T> Batch<T> of(String id, T...batch) {
        return new DefaultBatch<T>(id, batch);
    }
}
