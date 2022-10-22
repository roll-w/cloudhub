package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.node.HeartbeatService;
import org.huel.cloudhub.meta.server.node.HeartbeatWatcher;
import org.huel.cloudhub.meta.server.node.NodeRegisterService;
import org.huel.cloudhub.meta.server.node.NodeServer;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Collection;
import java.util.List;

/**
 * 调试用
 *
 * @author RollW
 */
@ShellComponent
public class NodeCommand extends AbstractShellComponent {
    private final NodeRegisterService nodeRegisterService;
    private final HeartbeatService heartbeatService;

    public NodeCommand(NodeRegisterService nodeRegisterService,
                       HeartbeatService heartbeatService) {
        this.nodeRegisterService = nodeRegisterService;
        this.heartbeatService = heartbeatService;
    }

    @ShellMethod(value = "node operations.", key = {"node"})
    public void nodeAction(
            @ShellOption(help = "options of node", defaultValue = "show",
                    value = {"--option", "-o"}) String option) {
        if (option == null) {
            getTerminal().writer().println("no option provide");
        }
        switch (option) {
            case "show" -> showActiveNodes();
            case "heartbeat" -> showActiveHeartbeatWatchers();
            default -> getTerminal().writer().println("Unknown node command '%s'.".formatted(option));
        }
        getTerminal().writer().flush();
    }

    private void showActiveNodes() {
        Collection<NodeServer> activeNodes = nodeRegisterService.selectActiveNodes();
        getTerminal().writer().println("shows all active nodes: active nodes count = [%d]"
                .formatted(activeNodes.size()));
        activeNodes
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }

    private void showActiveHeartbeatWatchers() {
        List<HeartbeatWatcher> heartbeatWatchers =
                heartbeatService.activeHeartbeatWatchers();
        getTerminal().writer().println("shows all active heartbeat watchers: active watchers count = [%d]"
                .formatted(heartbeatWatchers.size()));
        heartbeatWatchers
                .forEach(getTerminal().writer()::println);
        getTerminal().writer().flush();
    }
}
