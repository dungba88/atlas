package org.joo.atlas.models;

import org.joo.atlas.models.impl.DefaultTask;

public interface Task {

    String getId();

    String getName();

    String getType();

    Object[] getTaskArguments();

    String[] getDependants();

    static Task of(String id, String name, String type) {
        return new DefaultTask(id, name, type, new String[0], new Object[0]);
    }

    static Task of(String id, String name, String type, String[] dependants) {
        return new DefaultTask(id, name, type, dependants, new Object[0]);
    }

    static Task of(String id, String name, String type, String[] dependants, Object... args) {
        return new DefaultTask(id, name, type, dependants, args);
    }
}
