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

package org.cloudhub.file.server.command;

import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.server.service.container.ContainerService;
import org.cloudhub.file.server.service.container.ReplicaContainerDelegate;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

/**
 * @author RollW
 */
@ShellComponent
public class ContainerCommand extends AbstractShellComponent {
    private final ContainerService containerService;
    private final ReplicaContainerDelegate replicaContainerDelegate;
    private final ContainerProperties containerProperties;

    public ContainerCommand(ContainerService containerService,
                            ReplicaContainerDelegate replicaContainerDelegate,
                            ContainerProperties containerProperties) {
        this.containerService = containerService;
        this.replicaContainerDelegate = replicaContainerDelegate;
        this.containerProperties = containerProperties;
    }

    @ShellMethod(value = "container operations.", key = {"cont"})
    public void contAction(
            @ShellOption(help = "options of container", defaultValue = "show",
                    value = {"--option", "-o"}) String option) {
        if (option == null) {
            getTerminal().writer().println("no option provide");
            return;
        }
        if ("show".equals(option)) {
            showContainers();
        } else {
            showContainers(option);
        }
        getTerminal().writer().flush();
    }
    @ShellMethod(value = "replica container operations.", key = {"rcont show"})
    public void contAction() {
        showReplicaContainers();
        getTerminal().writer().flush();
    }


    private void deleteAll(String path) throws IOException {
        File file = new File(path);
        if (!file.isDirectory() || !file.exists()) {
            return;
        }
        Files.walk(file.toPath()).forEach(p ->
                p.toFile().delete());
    }

    private void showReplicaContainers() {
        Collection<Container> containers = replicaContainerDelegate.listContainers();
        getTerminal().writer().println("shows all replica containers.\tcount = [%d]"
                .formatted(containers.size()));
        containers
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }

    private void showContainers() {
        Collection<Container> containers = containerService.listContainers();
        getTerminal().writer().println("shows all containers.\tcount = [%d]"
                .formatted(containers.size()));
        containers
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }

    private void showContainers(String id) {
        Collection<Container> containers = containerService.listContainers(id);
        getTerminal().writer().println("shows containers of %s.\tcount = [%d]"
                .formatted(id, containers.size()));
        containers
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }
}
