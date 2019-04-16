package org.joo.atlas.support.exceptions;

public class RemoteException extends RuntimeException {

    private static final long serialVersionUID = -2928130202606913742L;

    public RemoteException(String cause) {
        super(cause);
    }
}
