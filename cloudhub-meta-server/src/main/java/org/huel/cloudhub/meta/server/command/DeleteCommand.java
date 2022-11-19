package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.service.file.FileDeleteService;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @author RollW
 */
@ShellComponent
public class DeleteCommand extends AbstractShellComponent {
    private final FileDeleteService fileDeleteService;

    public DeleteCommand(FileDeleteService fileDeleteService) {
        this.fileDeleteService = fileDeleteService;
    }

    @ShellMethod(value = "delete file.", key = {"delete"})
    public void deleteFile(String file) {
        getTerminal().writer().println("Start delete file, id=" + file);
        getTerminal().writer().flush();
        fileDeleteService.deleteFileCompletely(file);
    }
}
