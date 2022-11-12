package org.huel.cloudhub.file.server.command;

import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.file.server.service.container.ContainerService;
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
    private final ContainerProperties containerProperties;

    public ContainerCommand(ContainerService containerService,
                            ContainerProperties containerProperties) {
        this.containerService = containerService;
        this.containerProperties = containerProperties;
    }

    @ShellMethod(value = "container operations.", key = {"cont"})
    public void contAction(
            @ShellOption(help = "options of container", defaultValue = "show",
                    value = {"--option", "-o"}) String option) throws IOException {
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

    private void deleteAll(String path) throws IOException {
        File file = new File(path);
        if (!file.isDirectory() || !file.exists()) {
            return;
        }
        Files.walk(file.toPath()).forEach(p ->
                p.toFile().delete());
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
