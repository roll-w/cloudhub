package org.huel.cloudhub.client.controller.server;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.service.server.ServerStatusService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.BusinessRuntimeException;
import org.huel.cloudhub.common.CommonErrorCode;
import org.huel.cloudhub.fs.CFSClient;
import org.huel.cloudhub.fs.server.ConnectedServers;
import org.huel.cloudhub.fs.server.ContainerStatus;
import org.huel.cloudhub.server.DiskUsageInfo;
import org.huel.cloudhub.server.NetworkUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author RollW
 */
@RestController
@ServerApi
public class ServerStatusController {
    private final UserGetter userGetter;
    private final CFSClient cfsClient;
    private final ServerStatusService serverStatusService;

    public ServerStatusController(UserGetter userGetter,
                                  CFSClient cfsClient,
                                  ServerStatusService serverStatusService) {
        this.userGetter = userGetter;
        this.cfsClient = cfsClient;
        this.serverStatusService = serverStatusService;
    }

    @GetMapping("/time")
    public HttpResponseEntity<Long> getRunTimeFromStart(HttpServletRequest request) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        return HttpResponseEntity.success(serverStatusService.getRunTimeLength());
    }

    @GetMapping("/connected")
    public HttpResponseEntity<ConnectedServers> getConnectedServers(HttpServletRequest request) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        ConnectedServers connectedServers =
                cfsClient.getConnectedServers();
        return HttpResponseEntity.success(connectedServers);
    }

    // 获取指定服务器下所有容器信息
    @GetMapping("/container/get")
    public HttpResponseEntity<List<ContainerStatus>> getFileServerContainerStatuses(
            HttpServletRequest request,
            @RequestParam String serverId) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        if (serverId == null || serverId.isEmpty()) {
            throw new BusinessRuntimeException(WebCommonErrorCode.ERROR_PARAM_MISSING,
                    "Missing server id.");
        }
        return HttpResponseEntity.success(
                cfsClient.getContainerStatuses(serverId));
    }

    public static final String META_SERVER_ID = "meta";

    @GetMapping("/server/get")
    public HttpResponseEntity<ServerHostInfo> getServerStatus(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "") String serverId) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        if (serverId == null || serverId.isEmpty()) {
            return HttpResponseEntity.success(
                    serverStatusService.getCurrentInfo());
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerInfo());
        }
        return HttpResponseEntity.success(
                cfsClient.getFileServerInfo(serverId));
    }

    @GetMapping("/server/get/net")
    public HttpResponseEntity<List<NetworkUsageInfo>> getServerNetInfos(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "") String serverId) {
        ValidateHelper.validateUserAdmin(request, userGetter);
        if (serverId == null || serverId.isEmpty()) {
            return HttpResponseEntity.success(
                    serverStatusService.getNetInfos());
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerNetRecords());
        }
        List<NetworkUsageInfo> nets =
                cfsClient.getFileNetRecords(serverId);
        if (nets == null) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        return HttpResponseEntity.success(
                cfsClient.getFileNetRecords(serverId));
    }

    @GetMapping("/server/get/disk")
    public HttpResponseEntity<List<DiskUsageInfo>> getServerDiskInfos(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "") String serverId) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        if (serverId == null || serverId.isEmpty()) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerDiskRecords());
        }
        List<DiskUsageInfo> disks =
                cfsClient.getFileServerDiskRecords(serverId);
        if (disks == null) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        return HttpResponseEntity.success(disks);
    }
}
