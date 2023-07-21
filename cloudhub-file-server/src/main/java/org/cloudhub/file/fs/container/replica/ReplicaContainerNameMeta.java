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

package org.cloudhub.file.fs.container.replica;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Meta parsed from container name.
 *
 * @author RollW
 */
public class ReplicaContainerNameMeta {
    private final String sourceId;
    private final String id;
    private final long serial;
    private final String name;

    public ReplicaContainerNameMeta(String sourceId, String id, long serial) {
        this.sourceId = sourceId;
        this.id = id;
        this.serial = serial;
        this.name = toName();
    }

    public static boolean check(String containerLocator) {
        String[] metas = containerLocator.split(Pattern.quote("_"));
        return metas.length == 3;
    }

    private String toName() {
        return "%s_%s_%010d".formatted(sourceId, id, serial);
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getId() {
        return id;
    }

    public long getSerial() {
        return serial;
    }

    public String getName() {
        return name;
    }

    public static ReplicaContainerNameMeta parse(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        if (metas.length != 3) {
            throw new IllegalArgumentException("Not a valid container file name");
        }
        return new ReplicaContainerNameMeta(metas[0], metas[1],
                Long.parseLong(metas[2]));
    }

    public ReplicaContainerNameMeta forkSerial(long serial) {
        return new ReplicaContainerNameMeta(sourceId, id, serial);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicaContainerNameMeta that = (ReplicaContainerNameMeta) o;
        return serial == that.serial && Objects.equals(sourceId, that.sourceId) && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, id, serial, name);
    }
}
