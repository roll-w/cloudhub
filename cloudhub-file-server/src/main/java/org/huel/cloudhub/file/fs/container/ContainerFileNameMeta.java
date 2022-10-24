package org.huel.cloudhub.file.fs.container;

import java.util.regex.Pattern;

/**
 * @author RollW
 */
public record ContainerFileNameMeta(
        String id,
        long serial,
        long version) {

    public String toName() {
        return "%s_%010d_%010d".formatted(id, serial, version);
    }

    public static ContainerFileNameMeta parse(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        if (metas.length != 3) {
            throw new IllegalArgumentException("Not a valid container file name");
        }
        return new ContainerFileNameMeta(metas[0],
                Long.parseLong(metas[1]), Long.parseLong(metas[2]));
    }

    public ContainerFileNameMeta forkVersion(long version) {
        return new ContainerFileNameMeta(id, serial, version);
    }

    public ContainerFileNameMeta forkSerial(long serial) {
        return new ContainerFileNameMeta(id, serial, version);
    }
}
