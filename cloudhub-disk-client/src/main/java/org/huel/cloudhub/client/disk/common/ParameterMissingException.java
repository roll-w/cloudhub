package org.huel.cloudhub.client.disk.common;


import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.WebCommonErrorCode;

/**
 * @author RollW
 */
public class ParameterMissingException extends BusinessRuntimeException {
    private static final String DEFAULT_TEMPLATE = "Parameter {} is missing.";

    public ParameterMissingException() {
        super(WebCommonErrorCode.ERROR_PARAM_MISSING);
    }

    public ParameterMissingException(String message, Object... args) {
        super(WebCommonErrorCode.ERROR_PARAM_MISSING, message, args);
    }

    public ParameterMissingException(String parameterName) {
        this(DEFAULT_TEMPLATE, parameterName);
    }
}
