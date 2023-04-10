package org.huel.cloudhub.client.server;

import org.huel.cloudhub.server.rpc.server.SerializedFileServer;

/**
 * @author RollW
 */
public record FileServerLocation(
        String host,
        int port,
        String serverId
) {
   public static FileServerLocation from(SerializedFileServer s) {
       return new FileServerLocation(s.getHost(), s.getPort(), s.getId());
   }
}
