package org.huel.cloudhub.file.server.service.container;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.ServerFile;
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
@SuppressWarnings({"UnstableApiUsage"})
public class ContainerCheckService implements ContainerChecker {
    private final ContainerReadOpener containerReadOpener;

    public ContainerCheckService(ContainerReadOpener containerReadOpener) {
        this.containerReadOpener = containerReadOpener;
    }

    @Override
    public boolean checkContainer(Container container) {
        // need first check the container's checksum
        // then check the container's files
        String calcChecksum;
        try {
            calcChecksum = calculateChecksum(container);
        } catch (LockException | IOException e) {
            return false;
        }
        String containerChecksum = container.getIdentity().crc();
        if (!calcChecksum.equals(containerChecksum)) {
            return false;
        }
        // TODO: check the files

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

    public static String calculateChecksum(ServerFile serverFile) throws IOException {
        Hasher crc32 = Hashing.crc32().newHasher();
        InputStream inputStream = serverFile.openInput();
        HasherInputStream hasherInputStream = new HasherInputStream(inputStream);
        hasherInputStream.addHasher("CRC32", crc32);
        readFully(hasherInputStream);
        inputStream.close();
        return hasherInputStream.getHash("CRC32").toString();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static final int EOF = -1;

    private static void readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != input.read(buffer)) {
            // leave it empty
        }
    }
}
