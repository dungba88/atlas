package org.joo.atlas.support.exceptions;

import java.util.Map;

import lombok.Getter;

@Getter
public class BatchException extends RuntimeException {

    private static final long serialVersionUID = 6602331667374032369L;

    private Map<String, Throwable> failures;

    public BatchException(Map<String, Throwable> failures) {
        super("Multiple exceptions caught on batch execution " + failures);
        this.failures = failures;
    }
}
