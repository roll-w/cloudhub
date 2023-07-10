package org.huel.cloudhub.client.disk.domain.systembased.validate;

/**
 * @author RollW
 */
public class UnsupportedFieldException extends IllegalArgumentException {
    public UnsupportedFieldException() {
        super();
    }

    public UnsupportedFieldException(String s) {
        super(s);
    }

    public UnsupportedFieldException(FieldType fieldType) {
        super("Unsupported field type: " + fieldType);
    }

    public UnsupportedFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
