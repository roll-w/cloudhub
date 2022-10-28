package org.huel.cloudhub.file.fs.container;

import java.util.regex.Pattern;

/**
 * Replica Container File Name Meta Info.
 *
 * @author RollW
 */
public class ReplicaContainerNameMeta {
    private final String id;
    private final String sourceServerId;
    private final long serial;
    private final String name;

    public ReplicaContainerNameMeta(String sourceServerId,
                                    String id, long serial) {
        this.id = id;
        this.serial = serial;
        this.sourceServerId = sourceServerId;
        this.name = toName();
    }

    private String toName() {
        return "%s_%s_%010d".formatted(sourceServerId, id, serial);
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

    public String getSourceServerId() {
        return sourceServerId;
    }

    public static ReplicaContainerNameMeta parse(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        if (metas.length != 3) {
            throw new IllegalArgumentException("Not a valid container file name");
        }
        return new ReplicaContainerNameMeta(metas[0], metas[1],
                Long.parseLong(metas[2]));
    }
}
