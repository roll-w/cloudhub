package org.huel.cloudhub.file.server.service.container;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerChecker;
import org.huel.cloudhub.file.fs.container.ContainerReadOpener;
import org.huel.cloudhub.file.io.HasherInputStream;
import org.huel.cloudhub.file.io.SeekableInputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
@Service
public class ContainerCheckService implements ContainerChecker {
    private final ContainerReadOpener containerReadOpener;

    public ContainerCheckService(ContainerReadOpener containerReadOpener) {
        this.containerReadOpener = containerReadOpener;
    }

    @Override
    public boolean checkContainer(Container container) {
        return false;
    }

    @Override
    public String calculateChecksum(Container container) throws LockException, IOException {
        Hasher crc32 = Hashing.crc32().newHasher();
        SeekableInputStream seekableInputStream =
                containerReadOpener.openContainerRead(container);
        HasherInputStream hasherInputStream = new HasherInputStream(seekableInputStream);
        hasherInputStream.addHasher("CRC32", crc32);
        readFully(hasherInputStream);
        containerReadOpener.closeContainerRead(container, hasherInputStream);
        return hasherInputStream.getHash("CRC32").toString();
    }
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static final int EOF = -1;

    private static void readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != input.read(buffer)) {
            continue;
        }
    }
}
