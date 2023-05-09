package org.huel.cloudhub.client.disk.common;

import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.WebCommonErrorCode;

/**
 * @author RollW
 */
public class ParameterFailedException extends BusinessRuntimeException {
    private static final String DEFAULT_TEMPLATE = "Parameter '{}' validate failed.";

    public ParameterFailedException() {
        super(WebCommonErrorCode.ERROR_PARAM_FAILED);
    }

    public ParameterFailedException(String message, Object... args) {
        super(WebCommonErrorCode.ERROR_PARAM_FAILED, message, args);
    }

    public ParameterFailedException(String parameterName) {
        this(DEFAULT_TEMPLATE, parameterName);
    }


}
