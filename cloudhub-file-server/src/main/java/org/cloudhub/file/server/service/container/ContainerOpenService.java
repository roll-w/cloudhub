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

package org.cloudhub.file.server.service.container;

import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.fs.container.ContainerLocation;
import org.cloudhub.file.fs.container.ContainerReadOpener;
import org.cloudhub.file.fs.container.ContainerWriterOpener;
import org.cloudhub.file.io.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Service
public class ContainerOpenService implements ContainerReadOpener, ContainerWriterOpener {
    public static final int LOCKWAIT_SEC = 5;

    @Override
    public SeekableInputStream openContainerRead(Container container) throws IOException, LockException {
        if (checkNoContainerExists(container.getLocation())) {
            return null;
        }
        boolean lock;
        try {
            lock = container.readLock().tryLock(LOCKWAIT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
        if (lock) {
            return new SeekableFileInputStream(container.getLocation());
        }
        throw new LockException("Unable to acquire read lock on container %s."
                .formatted(container.getLocator()));
    }

    @Override
    public void closeContainerRead(Container container, InputStream stream) {
        try {
            container.readLock().unlock();
        } catch (Exception ignored) {
        }
        IoUtils.closeQuietly(stream);
    }

    @Override
    public SeekableOutputStream openContainerWrite(Container container) throws IOException, LockException {
        if (checkNoContainerExists(container.getLocation())) {
            return null;
        }
        boolean lock;
        try {
            // try to acquire lock
            lock = container.writeLock().tryLock(LOCKWAIT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
        if (lock) {
            return new SeekableFileOutputStream(container.getLocation());
        }
        throw new LockException("Unable to acquire write lock on container %s."
                .formatted(container.getLocator()));

    }

    @Override
    public void closeContainerWrite(Container container, OutputStream stream) {
        try {
            container.writeLock().unlock();
        } catch (Exception ignored) {
        }
        IoUtils.closeQuietly(stream);
    }

    private static boolean checkNoContainerExists(ContainerLocation location) {
        return !location.toFile().exists();
    }
}
