package org.huel.cloudhub.client.controller.server;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.fs.ConnectedServers;
import org.huel.cloudhub.client.service.rpc.FileServerCheckService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
@RestController
@ServerApi
public class ServerStatusController {
    private final UserGetter userGetter;
    private final FileServerCheckService fileServerCheckService;

    public ServerStatusController(UserGetter userGetter,
                                  FileServerCheckService fileServerCheckService) {
        this.userGetter = userGetter;
        this.fileServerCheckService = fileServerCheckService;
    }

    @GetMapping("/get")
    public HttpResponseEntity<ConnectedServers> getConnectedServers(HttpServletRequest httpServletRequest) {
        var validateMessage =
                ValidateHelper.validateUserAdmin(httpServletRequest, userGetter);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody(d -> null));
        }

        ConnectedServers connectedServers =
                fileServerCheckService.getConnectedServers();
        return HttpResponseEntity.success(connectedServers);
    }
}
