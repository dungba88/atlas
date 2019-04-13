package org.joo.atlas.support.exceptions;

public class CyclicGraphDetectedException extends RuntimeException {

    private static final long serialVersionUID = 6441702383775423420L;

    public CyclicGraphDetectedException(String id) {
        super("Cyclic graph detected while trying to visit node " + id);
    }
}
