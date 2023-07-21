/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.fs.container.validate;

import org.cloudhub.server.rpc.status.SerializedContainerStatusCode;

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
