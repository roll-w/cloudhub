package org.huel.cloudhub.client.configuration.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.ErrorCodeFinderChain;

import java.io.IOException;

/**
 * @author RollW
 */
public class ErrorCodeDeserializer extends StdDeserializer<ErrorCode> {
    private final ErrorCodeFinderChain errorCodeFinderChain;

    public ErrorCodeDeserializer(ErrorCodeFinderChain codeFinderChain) {
        super(ErrorCode.class);
        this.errorCodeFinderChain = codeFinderChain;
    }

    @Override
    public ErrorCode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String code = p.getValueAsString();
        return errorCodeFinderChain.findErrorCode(code);
    }
}
