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

package org.cloudhub.file.server.configuration;

import org.cloudhub.rpc.GrpcProperties;
import org.cloudhub.server.ServerIdentifiable;
import org.cloudhub.server.ServerInfo;
import org.cloudhub.server.SourceServerGetter;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author RollW
 */
@Configuration
public class ServerInfoConfiguration implements SourceServerGetter {
    private final ServerInfo serverInfo;

    public ServerInfoConfiguration(ServerIdentifiable serverIdentifiable,
                                   GrpcProperties grpcProperties)
            throws UnknownHostException {
        this.serverInfo = new ServerInfo(
                serverIdentifiable.getServerId(),
                localhostInetAddress().getHostAddress(),
                grpcProperties.getPort()
        );
    }

    @Override
    public ServerInfo getLocalServer() {
        return serverInfo;
    }

    private InetAddress tryGetLocalOutboundAddress() throws UnknownHostException, SocketException {
        InetAddress candidateAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            for (Enumeration<InetAddress> inetAddrs = ni.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                InetAddress inetAddr = inetAddrs.nextElement();
                if (inetAddr.isLoopbackAddress()) {
                    continue;
                }
                if (inetAddr.isSiteLocalAddress()) {
                    return inetAddr;
                }
                if (candidateAddress == null) {
                    candidateAddress = inetAddr;
                }
            }
        }

        return candidateAddress == null
                ? InetAddress.getLocalHost()
                : candidateAddress;
    }

    public InetAddress localhostInetAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}