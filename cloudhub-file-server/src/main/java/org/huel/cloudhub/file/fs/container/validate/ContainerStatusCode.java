package org.huel.cloudhub.file.fs.container.validate;

import org.huel.cloudhub.server.rpc.status.SerializedContainerStatusCode;

/**
 * When a group index lost or unreadable, still try to find container and
 * validate it. And returns one or more of the following statuses.
 *
 * @author RollW
 */
public enum ContainerStatusCode {

    /**
     * Metadata lost.
     */
    META_LOST(SerializedContainerStatusCode.META_LOST, 0, 0, 1, 4),

    /**
     * Metadata has been changed unexpectedly.
     */
    META_FAILED(SerializedContainerStatusCode.META_FAILED, 1, 0, 1, 4),

    /**
     * Container file has been lost.
     */
    CONTAINER_LOST(SerializedContainerStatusCode.CONTAINER_LOST, 2, 2, 3, 4),

    /**
     * Container file has been changed unexpectedly.
     */
    CONTAINER_FAILED(SerializedContainerStatusCode.CONTAINER_FAILED, 3, 2, 3, 4),

    /**
     * Container file is valid.
     */
    VALID(SerializedContainerStatusCode.VALID, 4, 0, 1, 2, 3, 4),
    ;

    private final SerializedContainerStatusCode serialized;
    private final int statusCode;
    private final int[] mutuallyExclusiveStatuses;


    ContainerStatusCode(SerializedContainerStatusCode serialized,
                        int statusCode,
                        int... mutuallyExclusiveStatuses) {
        this.serialized = serialized;
        this.statusCode = statusCode;
        this.mutuallyExclusiveStatuses = mutuallyExclusiveStatuses;
    }

    public int[] getMutuallyExclusiveStatusesCodes() {
        return mutuallyExclusiveStatuses;
    }

    public SerializedContainerStatusCode getSerialized() {
        return serialized;
    }

    public ContainerStatusCode[] getMutuallyExclusiveStatuses() {
        ContainerStatusCode[] statuses = new ContainerStatusCode[mutuallyExclusiveStatuses.length];
        for (int i = 0; i < mutuallyExclusiveStatuses.length; i++) {
            statuses[i] = findByStatusCode(mutuallyExclusiveStatuses[i]);
        }
        return statuses;
    }

    public static ContainerStatusCode findByStatusCode(int status) {
        for (ContainerStatusCode containerStatusCode : ContainerStatusCode.values()) {
            if (containerStatusCode.statusCode == status) {
                return containerStatusCode;
            }
        }
        return null;
    }
}
