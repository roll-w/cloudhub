package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * Unsupported kind exception.
 *
 * @author RollW
 */
public class UnsupportedKindException extends IllegalArgumentException {
    public UnsupportedKindException() {
        super();
    }

    public UnsupportedKindException(String s) {
        super(s);
    }

    public UnsupportedKindException(SystemResourceKind systemResourceKind) {
        super("Unsupported system resource kind: " + systemResourceKind);
    }

    public UnsupportedKindException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedKindException(Throwable cause) {
        super(cause);
    }
}
