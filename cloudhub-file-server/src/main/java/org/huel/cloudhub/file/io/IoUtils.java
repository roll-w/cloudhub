package org.huel.cloudhub.file.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author RollW
 */
public class IoUtils {
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ignore) {
            // ignore
        }
    }

    private IoUtils() {
    }
}
