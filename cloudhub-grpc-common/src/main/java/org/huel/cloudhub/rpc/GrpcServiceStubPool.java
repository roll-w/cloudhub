package org.huel.cloudhub.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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

    public Stub getStub(String serverId, Supplier<Stub> supplier) {
        Stub stub = stubMap.get(serverId);
        if (stub == null) {
            stub = supplier.get();
            stubMap.put(serverId, stub);
        }
        return stub;
    }
}
