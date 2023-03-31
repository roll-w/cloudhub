package org.huel.cloudhub.file.fs.container.replica;

import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public class SourcedContainerGroup {
    private final String sourceId;
    private final Map<String, ContainerGroup> containerGroupMap =
            new HashMap<>();

    public SourcedContainerGroup(String sourceId) {
        this.sourceId = sourceId;
    }

    public void put(Container container) {
        ContainerGroup group = getGroup(container.getIdentity().id());
        if (group == null) {
            group = newGroup(container.getIdentity().id());
            containerGroupMap.put(group.getContainerId(), group);
        }
        group.put(container);
    }

    public Container getContainer(String containerId, long serial) {
        ContainerGroup group = getGroup(containerId);
        if (group == null) {
            return null;
        }
        return group.getContainer(serial);
    }

    private ContainerGroup newGroup(String containerId) {
        return new ContainerGroup(containerId, sourceId);
    }

    private ContainerGroup newGroup(String containerId, Collection<Container> containers) {
        return new ContainerGroup(containerId, sourceId, containers);
    }

    public ContainerGroup getGroup(String containerId) {
        return containerGroupMap.getOrDefault(containerId, null);
    }

    public ContainerGroup getOrCreateGroup(String containerId) {
        ContainerGroup group = getGroup(containerId);
        if (group == null) {
            group = newGroup(containerId);
            containerGroupMap.put(containerId, group);
        }
        return group;
    }

    public String getSourceId() {
        return sourceId;
    }

    public List<ContainerGroup> listGroups() {
        return containerGroupMap.values().stream().toList();
    }
}
