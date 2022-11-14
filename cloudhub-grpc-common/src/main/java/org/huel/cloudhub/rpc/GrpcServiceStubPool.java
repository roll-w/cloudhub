package org.huel.cloudhub.rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class GrpcServiceStubPool<Stub> {
    private final Map<String, Stub> stubMap = new HashMap<>();

    public GrpcServiceStubPool() {
    }

    public void registerStub(String serverId, Stub stub) {
        stubMap.put(serverId, stub);
    }

    public Stub getStub(String serverId) {
        return stubMap.get(serverId);
    }
}
