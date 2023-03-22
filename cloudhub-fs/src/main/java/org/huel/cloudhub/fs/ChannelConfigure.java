package org.huel.cloudhub.fs;

import io.grpc.ManagedChannelBuilder;

/**
 * @author RollW
 */
public interface ChannelConfigure {
    void configure(ManagedChannelBuilder<?> builder);
}
