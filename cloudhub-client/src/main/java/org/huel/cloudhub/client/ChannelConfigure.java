package org.huel.cloudhub.client;

import io.grpc.ManagedChannelBuilder;

/**
 * @author RollW
 */
public interface ChannelConfigure {
    void configure(ManagedChannelBuilder<?> builder);
}
