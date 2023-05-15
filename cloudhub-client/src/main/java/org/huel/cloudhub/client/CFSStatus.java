package org.huel.cloudhub.client;

/**
 * @author RollW
 */
public enum CFSStatus {
    SUCCESS,
    CANCELED,
    DATA_LOSS,
    IO_ERROR,
    UNAVAILABLE,
    NOT_FOUND,
    UNKNOWN,
    ;

    public boolean success() {
        return this == SUCCESS;
    }
}
