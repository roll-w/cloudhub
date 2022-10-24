package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.file.FileBlockService;
import org.huel.cloudhub.meta.server.node.HeartbeatService;
import org.huel.cloudhub.meta.server.node.HeartbeatWatcher;
import org.huel.cloudhub.meta.server.node.NodeServer;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * 调试用
 *
 * @author RollW
 */
@ShellComponent
public class NodeCommand extends AbstractShellComponent {
    private final HeartbeatService heartbeatService;
    private final FileBlockService fileBlockService;

    public NodeCommand(HeartbeatService heartbeatService,
                       FileBlockService fileBlockService) {
        this.heartbeatService = heartbeatService;
        this.fileBlockService = fileBlockService;
    }

    @ShellMethod(value = "node operations.", key = {"node"})
    public void nodeAction(
            @ShellOption(help = "options of node", defaultValue = "show",
                    value = {"--option", "-o"}) String option) throws IOException {
        if (option == null) {
            getTerminal().writer().println("no option provide");
            return;
        }
        switch (option) {
            case "show" -> showActiveNodes();
            case "heartbeat" -> showActiveHeartbeatWatchers();
            case "test" -> sendTestFile();
            case "test2" -> sendTest2File();
            default -> getTerminal().writer().println("Unknown node command '%s'.".formatted(option));
        }
        getTerminal().writer().flush();
    }

    private void showActiveNodes() {
        List<NodeServer> activeNodes = heartbeatService.activeServers();
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

    private void sendTestFile() throws IOException {
        getTerminal().writer().println("send test file.");
        getTerminal().writer().flush();
        byte[] data = Files.readAllBytes(new File("test.txt").toPath());
        fileBlockService.sendBlock(data);
    }

    private void sendTest2File() throws IOException {
        getTerminal().writer().println("send test2 file.");
        getTerminal().writer().flush();
        byte[] data = Files.readAllBytes(new File("test2.txt").toPath());
        fileBlockService.sendBlock(data);
    }
}
