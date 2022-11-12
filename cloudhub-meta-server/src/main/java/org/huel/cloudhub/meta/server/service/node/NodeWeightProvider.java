package org.huel.cloudhub.meta.server.service.node;

/**
 * @author RollW
 */
public interface NodeWeightProvider {
    int getWeightOf(NodeServer nodeServer);
}
