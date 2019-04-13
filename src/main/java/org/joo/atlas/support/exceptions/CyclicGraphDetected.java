package org.joo.atlas.support.exceptions;

public class CyclicGraphDetected extends RuntimeException {

    private static final long serialVersionUID = 6441702383775423420L;

    public CyclicGraphDetected(String id) {
        super("Cyclic graph detected while trying to visit node " + id);
    }
}
