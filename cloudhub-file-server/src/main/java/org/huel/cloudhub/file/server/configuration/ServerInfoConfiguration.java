package org.huel.cloudhub.file.server.configuration;

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
public class ServerInfoConfiguration {

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

    @Bean
    public InetAddress localhostInetAddress() throws UnknownHostException, SocketException {
        return InetAddress.getLocalHost();
    }
}