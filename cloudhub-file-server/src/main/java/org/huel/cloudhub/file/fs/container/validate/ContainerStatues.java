package org.huel.cloudhub.file.fs.container.validate;

import org.huel.cloudhub.server.rpc.status.SerializedContainerStatusCode;

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
