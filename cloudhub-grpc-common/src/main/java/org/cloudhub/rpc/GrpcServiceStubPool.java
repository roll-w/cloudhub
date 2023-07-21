/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.rpc;

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
