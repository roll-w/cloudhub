package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.node.NodeRegisterService;
import org.huel.cloudhub.meta.server.node.NodeServer;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Collection;

/**
 * @author RollW
 */
@ShellComponent
public class NodeCommand extends AbstractShellComponent {
    private final NodeRegisterService nodeRegisterService;

    public NodeCommand(NodeRegisterService nodeRegisterService) {
        this.nodeRegisterService = nodeRegisterService;
    }

    @ShellMethod(value = "node operations.", key = {"node"})
    public void nodeAction(@ShellOption(help = "options of node",
            value = {"--option", "-o"}) String option) {
        if (option == null) {
            getTerminal().writer().println("no option provide");
        }
        switch (option) {
            case "show" -> showActiveNodes();
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

}
