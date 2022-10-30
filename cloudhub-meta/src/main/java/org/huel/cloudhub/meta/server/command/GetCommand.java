package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.service.file.FileDownloadService;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
@ShellComponent
public class GetCommand extends AbstractShellComponent {
    private final FileDownloadService fileDownloadService;

    public GetCommand(FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
    }

    @ShellMethod(value = "download file to file server.", key = {"get"})
    public void nodeAction(
            @ShellOption(help = "download to path",
                    value = {"--path", "-p"}) String path,
            @ShellOption(help = "file id",
                    value = {"--file", "-f"}) String fileId
            ) throws IOException {
        if (path == null || path.isEmpty()) {
            getTerminal().writer().println("no path provide");
            return;
        }
        downloadTo(path, fileId);
        getTerminal().writer().flush();
    }

    private void downloadTo(String path, String fileId) throws IOException {
        OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(path, false));
        getTerminal().writer().println("download file to %s, from file %s."
                .formatted(path, fileId));
        getTerminal().writer().flush();
        fileDownloadService.downloadFile(outputStream, fileId);
    }
}
