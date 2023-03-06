package org.huel.cloudhub.file.fs.container.validate;

/**
 * When a group index lost or unreadable, still try to find container and
 * validate it. And returns one or more of the following statuses.
 *
 * @author RollW
 */
public enum ContainerStatus {

    /**
     * Metadata lost.
     */
    META_LOST(0, 0, 1, 4),

    /**
     * Metadata has been changed unexpectedly.
     */
    META_FAILED(1, 0, 1, 4),

    /**
     * Container file has been lost.
     */
    CONTAINER_LOST(2, 2, 3, 4),

    /**
     * Container file has been changed unexpectedly.
     */
    CONTAINER_FAILED(3, 2, 3, 4),

    /**
     * Container file is valid.
     */
    VALID(4, 0, 1, 2, 3, 4),
    ;


    private final int statusCode;
    private final int[] mutuallyExclusiveStatuses;


    ContainerStatus(int statusCode, int... mutuallyExclusiveStatuses) {
        this.statusCode = statusCode;
        this.mutuallyExclusiveStatuses = mutuallyExclusiveStatuses;
    }

    public int[] getMutuallyExclusiveStatusesCodes() {
        return mutuallyExclusiveStatuses;
    }

    public ContainerStatus[] getMutuallyExclusiveStatuses() {
        ContainerStatus[] statuses = new ContainerStatus[mutuallyExclusiveStatuses.length];
        for (int i = 0; i < mutuallyExclusiveStatuses.length; i++) {
            statuses[i] = findByStatusCode(mutuallyExclusiveStatuses[i]);
        }
        return statuses;
    }

    public static ContainerStatus findByStatusCode(int status) {
        for (ContainerStatus containerStatus : ContainerStatus.values()) {
            if (containerStatus.statusCode == status) {
                return containerStatus;
            }
        }
        return null;
    }
}
