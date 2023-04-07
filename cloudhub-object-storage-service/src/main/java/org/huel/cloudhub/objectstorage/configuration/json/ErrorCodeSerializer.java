package org.huel.cloudhub.objectstorage.configuration.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.huel.cloudhub.web.ErrorCode;

import java.io.IOException;

/**
 * @author RollW
 */
public class ErrorCodeSerializer extends StdSerializer<ErrorCode> {
    public ErrorCodeSerializer() {
        super(ErrorCode.class);
    }

    @Override
    public void serialize(ErrorCode value, JsonGenerator gen,
                          SerializerProvider provider) throws IOException {
        gen.writeString(value.getCode());
    }
}
