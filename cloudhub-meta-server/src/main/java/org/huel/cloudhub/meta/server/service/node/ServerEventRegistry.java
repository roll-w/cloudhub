package org.huel.cloudhub.meta.server.service.node;

/**
 * @author RollW
 */
public interface ServerEventRegistry {
    void registerCallback(ServerEventCallback serverEventCallback);

    interface ServerEventCallback {
        void registerServer(NodeServer server);

        void removeActiveServer(NodeServer nodeServer);

        void addActiveServer(NodeServer nodeServer);
    }
}
