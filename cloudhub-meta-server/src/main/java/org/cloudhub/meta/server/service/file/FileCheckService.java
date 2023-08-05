/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.meta.server.service.file;

import org.cloudhub.meta.server.service.node.FileNodeServer;
import org.cloudhub.meta.server.service.node.ServerEventRegistry;
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
    public void registerServer(FileNodeServer server) {

    }

    @Override
    public void removeActiveServer(FileNodeServer nodeServer) {
        // TODO: 延迟十分钟查询副本，启动复制进程。若中途重新活动则取消任务

    }

    @Override
    public void addActiveServer(FileNodeServer nodeServer) {
        // TODO: 延迟十分钟查找所有位置，将backup靠后的都尽可能的删除
    }
}
