package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.meta.server.service.node.ServerEventRegistry;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class FileCheckService implements ServerEventRegistry.ServerEventCallback {

    public FileCheckService(ServerEventRegistry registry) {
        registry.registerCallback(this);
    }

    @Override
    public void registerServer(NodeServer server) {

    }

    @Override
    public void removeActiveServer(NodeServer nodeServer) {
        // TODO: 延迟十分钟查询副本，启动复制进程。若中途重新活动则取消任务

    }

    @Override
    public void addActiveServer(NodeServer nodeServer) {
        // TODO: 延迟十分钟查找所有位置，将backup靠后的都尽可能的删除
    }
}
