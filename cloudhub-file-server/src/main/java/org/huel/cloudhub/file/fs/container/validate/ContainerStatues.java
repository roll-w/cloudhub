package org.huel.cloudhub.file.fs.container.validate;

import java.util.Set;

/**
 * @author RollW
 */
public class ContainerStatues {
    private final Set<ContainerStatus> containerStatuses;
    private final boolean valid;

    public ContainerStatues(Set<ContainerStatus> containerStatuses,
                            boolean valid) {
        this.containerStatuses = containerStatuses;
        this.valid = valid;
    }

    public Set<ContainerStatus> getContainerStatuses() {
        return containerStatuses;
    }

    public boolean isValid() {
        return valid;
    }
}
