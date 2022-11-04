package org.huel.cloudhub.file.server.service.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerLocation;
import org.huel.cloudhub.file.fs.container.ContainerReadOpener;
import org.huel.cloudhub.file.fs.container.ContainerWriterOpener;
import org.huel.cloudhub.file.io.*;
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
                .formatted(container.getResourceLocator()));
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
                .formatted(container.getResourceLocator()));

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
