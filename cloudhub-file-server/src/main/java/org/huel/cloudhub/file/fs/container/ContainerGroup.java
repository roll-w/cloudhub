package org.huel.cloudhub.file.fs.container;

import java.util.*;

/**
 * @author RollW
 */
public class ContainerGroup {
    private long lastSerial;

    private final Map<String, Container> containers = new HashMap<>();
    private final Map<Long, List<FreeBlockInfo>> serialFreeBlockInfos = new HashMap<>();
    private final Set<String> fileIds = new HashSet<>();

    public ContainerGroup() {

    }

    public void push(Container container) {

    }

    public long lastSerial() {
        return lastSerial;
    }
}
