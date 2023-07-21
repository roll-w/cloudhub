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

package org.cloudhub.file.fs.container;

import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.fs.container.ContainerGroup;

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
