package org.joo.atlas.support.exceptions;

import lombok.Getter;

@Getter
public class LifecycleException extends RuntimeException {

    private static final long serialVersionUID = 3143931441208815L;

    public LifecycleException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
