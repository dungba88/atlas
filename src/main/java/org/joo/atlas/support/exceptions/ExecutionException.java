package org.joo.atlas.support.exceptions;

import lombok.Getter;

@Getter
public class ExecutionException extends RuntimeException {

    private static final long serialVersionUID = 3143931441208815L;

    public ExecutionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
