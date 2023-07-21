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

package org.cloudhub.file.fs.container;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Meta parsed from container name.
 *
 * @author RollW
 */
public class ContainerNameMeta {
    private final String id;
    private final long serial;
    private final String name;

    public ContainerNameMeta(String id, long serial) {
        this.id = id;
        this.serial = serial;
        this.name = toName();
    }

    private String toName() {
        return "%s_%010d".formatted(id, serial);
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

    public static ContainerNameMeta parse(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        if (metas.length != 2) {
            throw new IllegalArgumentException("Not a valid container file name");
        }
        return new ContainerNameMeta(metas[0],
                Long.parseLong(metas[1]));
    }

    public static boolean check(String name) {
        String[] metas = name.split(Pattern.quote("_"));
        return metas.length == 2;
    }

    public ContainerNameMeta forkSerial(long serial) {
        return new ContainerNameMeta(id, serial);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerNameMeta that = (ContainerNameMeta) o;
        return serial == that.serial && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serial, name);
    }
}
