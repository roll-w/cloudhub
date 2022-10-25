package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.service.file.FileBlockService;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author RollW
 */
@ShellComponent
public class SendCommand extends AbstractShellComponent {
    private final FileBlockService fileBlockService;

    public SendCommand(FileBlockService fileBlockService) {
        this.fileBlockService = fileBlockService;
    }

    @ShellMethod(value = "send file to file server.", key = {"send"})
    public void nodeAction(
            @ShellOption(help = "send file at this path",
                    value = {"--path", "-p"}) String path) throws IOException {
        if (path == null || path.isEmpty()) {
            getTerminal().writer().println("no path provide");
            return;
        }
        sendFile(path);
        getTerminal().writer().flush();
    }

    private void sendFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            getTerminal().writer().printf("file path at '%s' not exists.\n",
                    path);
            return;
        }
        if (!file.isFile()) {
            getTerminal().writer().printf("path at '%s' is not a file.\n",
                    path);
            return;
        }
        getTerminal().writer().printf("send file in absolute path '%s'.\n",
                file.getAbsolutePath());
        getTerminal().writer().flush();
        byte[] data = Files.readAllBytes(file.toPath());
        fileBlockService.sendBlock(data);
    }
}
