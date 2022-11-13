package org.huel.cloudhub.file.server.controller.server;

import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.ContainerProperties;
import org.huel.cloudhub.server.ServerHostInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RollW
 */
@RestController
@RequestMapping("/api/server")
public class ServerStatusController {
    private final ServerHostInfo serverHostInfo;

    public ServerStatusController(ContainerProperties containerProperties,
                                  LocalFileServer localFileServer) {
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getFilePath());
        serverHostInfo = ServerHostInfo.load(file.getPath());
        serverHostInfo.reload();
    }

    @GetMapping("info")
    public HttpResponseEntity<ServerHostInfo> getServerInfo() {
        return HttpResponseEntity.success(serverHostInfo.reload());
    }
}
