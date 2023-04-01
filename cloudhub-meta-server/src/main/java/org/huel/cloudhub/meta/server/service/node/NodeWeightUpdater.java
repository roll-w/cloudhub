package org.huel.cloudhub.meta.server.service.node;

/**
 * @author RollW
 */
public interface NodeWeightUpdater {
    void onNewNodeWeight(String nodeId, int weight);
}
