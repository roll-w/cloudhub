package org.huel.cloudhub.client.disk.controller.status;

import org.huel.cloudhub.client.CFSClient;
import org.huel.cloudhub.client.disk.common.CloudhubBizRuntimeException;
import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.cfsserver.ServerStatusService;
import org.huel.cloudhub.client.server.ConnectedServers;
import org.huel.cloudhub.client.server.ContainerStatus;
import org.huel.cloudhub.server.DiskUsageInfo;
import org.huel.cloudhub.server.NetworkUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class ServerStatusController {
    private final CFSClient cfsClient;
    private final ServerStatusService serverStatusService;

    public ServerStatusController(CFSClient cfsClient,
                                  ServerStatusService serverStatusService) {
        this.cfsClient = cfsClient;
        this.serverStatusService = serverStatusService;
    }

    @GetMapping("/server/status/time")
    public HttpResponseEntity<Long> getRunTimeFromStart() {
        return HttpResponseEntity.success(serverStatusService.getRunTimeLength());
    }

    @GetMapping("/server/cfs/connected")
    public HttpResponseEntity<ConnectedServers> getConnectedServers() {
        ConnectedServers connectedServers =
                cfsClient.getConnectedServers();
        return HttpResponseEntity.success(connectedServers);
    }

    @GetMapping("/server/cfs/status/{serverId}/containers")
    public HttpResponseEntity<List<ContainerStatus>> getFileServerContainerStatuses(
            @PathVariable("serverId") String serverId) {
        if (serverId == null || serverId.isEmpty()) {
            throw new CloudhubBizRuntimeException(WebCommonErrorCode.ERROR_PARAM_MISSING,
                    "Missing server id.");
        }
        if (serverId.equalsIgnoreCase("meta")) {
            throw new CloudhubBizRuntimeException(WebCommonErrorCode.ERROR_NOT_SUPPORT,
                    "Not support meta server.");
        }

        return HttpResponseEntity.success(
                cfsClient.getContainerStatuses(serverId));
    }

    public static final String META_SERVER_ID = "meta";

    @GetMapping("/server/cfs/status/{serverId}")
    public HttpResponseEntity<ServerHostInfo> getServerStatus(
            @PathVariable("serverId") String serverId) {
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

    @GetMapping("/server/cfs/status/{serverId}/net")
    public HttpResponseEntity<List<NetworkUsageInfo>> getServerNetInfos(
            @PathVariable("serverId") String serverId) {
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
            throw new CloudhubBizRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        return HttpResponseEntity.success(
                cfsClient.getFileNetRecords(serverId));
    }

    @GetMapping("/server/cfs/status/{serverId}/disk")
    public HttpResponseEntity<List<DiskUsageInfo>> getServerDiskInfos(
            @PathVariable("serverId") String serverId) {

        if (serverId == null || serverId.isEmpty()) {
            throw new CloudhubBizRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        if (serverId.equalsIgnoreCase(META_SERVER_ID)) {
            return HttpResponseEntity.success(
                    cfsClient.getMetaServerDiskRecords());
        }
        List<DiskUsageInfo> disks =
                cfsClient.getFileServerDiskRecords(serverId);
        if (disks == null) {
            throw new CloudhubBizRuntimeException(CommonErrorCode.ERROR_NOT_FOUND,
                    "Not found server: " + serverId);
        }
        return HttpResponseEntity.success(disks);
    }
}
