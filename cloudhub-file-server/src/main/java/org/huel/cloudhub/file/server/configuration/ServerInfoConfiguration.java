package org.huel.cloudhub.file.server.configuration;

import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.file.server.service.id.ServerIdService;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
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



    public ServerInfoConfiguration(ServerIdService serverIdService,
                                   GrpcProperties grpcProperties)
            throws UnknownHostException {
        this.serverInfo = new ServerInfo(serverIdService.getServerId(),
                localhostInetAddress().getHostAddress(),
                grpcProperties.getPort());
    }

    @Override
    @Bean
    public ServerInfo getLocalServer() {
        return serverInfo;
    }

    private InetAddress tryGetLocalOutboundAddress() throws UnknownHostException, SocketException {
        InetAddress candidateAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface iface = networkInterfaces.nextElement();
            for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
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