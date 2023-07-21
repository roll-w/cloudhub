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

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author RollW
 */
public class ContainerStatues {
    private final Set<ContainerStatusCode> containerStatusCodes;
    private final boolean valid;

    public ContainerStatues(Set<ContainerStatusCode> containerStatusCodes,
                            boolean valid) {
        this.containerStatusCodes = containerStatusCodes;
        this.valid = valid;
    }

    public Set<ContainerStatusCode> getContainerStatuses() {
        return containerStatusCodes;
    }

    public List<SerializedContainerStatusCode> serialized() {
        return containerStatusCodes
                .stream()
                .map(ContainerStatusCode::getSerialized)
                .toList();
    }

    public boolean hasContainerBroken() {
        return containerStatusCodes.contains(ContainerStatusCode.CONTAINER_FAILED)
                || containerStatusCodes.contains(ContainerStatusCode.CONTAINER_LOST);
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerStatues that)) return false;
        return valid == that.valid && Objects.equals(containerStatusCodes, that.containerStatusCodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containerStatusCodes, valid);
    }

    @Override
    public String toString() {
        return "ContainerStatues{" +
                "containerStatusCodes=" + containerStatusCodes +
                ", valid=" + valid +
                '}';
    }


}
