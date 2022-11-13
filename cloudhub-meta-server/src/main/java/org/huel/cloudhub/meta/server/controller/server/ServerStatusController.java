package org.huel.cloudhub.meta.server.controller.server;

import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.server.ServerHostInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @author RollW
 */
@RestController
@RequestMapping("/api/server")
public class ServerStatusController {
    private final ServerHostInfo serverHostInfo;

    public ServerStatusController() {
        serverHostInfo = ServerHostInfo.load(new File(".").getPath());
        serverHostInfo.reload();
    }

    @GetMapping("info")
    public HttpResponseEntity<ServerHostInfo> getServerInfo() {
        return HttpResponseEntity.success(serverHostInfo.reload());
    }
}
