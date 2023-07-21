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

package org.cloudhub.meta.server.command;

import org.cloudhub.meta.server.service.node.HeartbeatService;
import org.cloudhub.meta.server.service.node.HeartbeatWatcher;
import org.cloudhub.meta.server.service.node.NodeServer;
import org.cloudhub.meta.server.service.node.ServerContainerStatus;
import org.cloudhub.meta.server.service.node.HeartbeatService;
import org.cloudhub.meta.server.service.node.HeartbeatWatcher;
import org.cloudhub.meta.server.service.node.NodeServer;
import org.cloudhub.meta.server.service.node.ServerContainerStatus;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 调试用
 *
 * @author RollW
 */
@ShellComponent
public class NodeCommand extends AbstractShellComponent {
    private final HeartbeatService heartbeatService;

    public NodeCommand(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @ShellMethod(value = "node operations.", key = {"node"})
    public void nodeAction(
            @ShellOption(help = "options of node", defaultValue = "show",
                    value = {"--option", "-o"}) String option,
            @ShellOption(help = "", defaultValue = "key",
                    value = {"-k"}) String key) throws IOException {
        if (option == null) {
            getTerminal().writer().println("no option provide");
            return;
        }
        switch (option) {
            case "show" -> showActiveNodes();
            case "heartbeat" -> showActiveHeartbeatWatchers();
            case "mapping" -> testMapping(key);
            case "status" -> containerStatus();
            default -> getTerminal().writer().println("Unknown node command '%s'.".formatted(option));
        }
        getTerminal().writer().flush();
    }

    private void showActiveNodes() {
        Collection<NodeServer> activeNodes = heartbeatService.activeServers();
        getTerminal().writer().println("shows all active nodes: active nodes count = [%d]"
                .formatted(activeNodes.size()));
        activeNodes
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }

    private void showActiveHeartbeatWatchers() {
        Collection<HeartbeatWatcher> heartbeatWatchers =
                heartbeatService.activeHeartbeatWatchers();
        getTerminal().writer().println("shows all active heartbeat watchers: active watchers count = [%d]"
                .formatted(heartbeatWatchers.size()));
        heartbeatWatchers
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }

    private void testMapping(String key) {
        NodeServer server = heartbeatService.getNodeAllocator().allocateNode(key);
        getTerminal().writer().printf("test mapping key %s.\nmapping to: %s\n", key,
                server);
        getTerminal().writer().flush();
    }

    private void containerStatus() {
        List<ServerContainerStatus> statuses =
                heartbeatService.getDamagedContainerReports();
        getTerminal().writer().printf("shows all damaged container reports: reports count = [%d]\n",
                statuses.size());
        statuses.forEach(status ->
                getTerminal().writer().printf("server: %s, reports: %s\n",
                        status.serverId(), status.damagedContainerReports()));
        getTerminal().writer().flush();
    }
}
