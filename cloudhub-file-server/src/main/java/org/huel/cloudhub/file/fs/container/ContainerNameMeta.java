package org.huel.cloudhub.file.fs.container;

import java.util.regex.Pattern;

/**
 * Meta parsed from container name.
 *
 * @author RollW
 */
public class ContainerNameMeta {
    private final String id;
    private final long serial;
    private final String name;

    public ContainerNameMeta(String id, long serial) {
        this.id = id;
        this.serial = serial;
        this.name = toName();
    }

    private String toName() {
        return "%s_%010d".formatted(id, serial);
    }

    public String getId() {
        return id;
    }

    public long getSerial() {
        return serial;
    }

    public String getName() {
        return name;
    }

    public static ContainerNameMeta parse(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        if (metas.length != 2) {
            throw new IllegalArgumentException("Not a valid container file name");
        }
        return new ContainerNameMeta(metas[0],
                Long.parseLong(metas[1]));
    }

    public ContainerNameMeta forkSerial(long serial) {
        return new ContainerNameMeta(id, serial);
    }
}
