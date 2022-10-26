package org.huel.cloudhub.file.fs.container;

import java.util.regex.Pattern;

/**
 * Meta parsed from container name.
 *
 * @author RollW
 */
public record ContainerFileNameMeta(
        String id,
        long serial) {

    public String toName() {
        return "%s_%010d".formatted(id, serial);
    }

    public static ContainerFileNameMeta parse(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        if (metas.length != 2) {
            throw new IllegalArgumentException("Not a valid container file name");
        }
        return new ContainerFileNameMeta(metas[0],
                Long.parseLong(metas[1]));
    }

    public ContainerFileNameMeta forkSerial(long serial) {
        return new ContainerFileNameMeta(id, serial);
    }
}
