package org.huel.cloudhub.client.service.object;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author RollW
 */
public final class ObjectMetadataHeaders {
    public static final String OBJECT_LENGTH = "x-cos-object-length";
    public static final String CONTENT_TYPE = "Content-Type";

    private static final Set<String> UNMODIFIABLE_HEADERS = new HashSet<>();
    static {
        UNMODIFIABLE_HEADERS.add(OBJECT_LENGTH);
    }

    public static Set<String> getUnmodifiableHeaders() {
        return Collections.unmodifiableSet(UNMODIFIABLE_HEADERS);
    }

    private ObjectMetadataHeaders() {
    }
}
