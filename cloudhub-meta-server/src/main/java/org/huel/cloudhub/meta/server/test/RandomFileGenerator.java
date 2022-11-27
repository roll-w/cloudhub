package org.huel.cloudhub.meta.server.test;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author RollW
 */
public class RandomFileGenerator {
    private final File file;
    private final int sizeInMb;
    private final int addBytes;

    public RandomFileGenerator(String path, int sizeInMb, int addBytes) {
        this(new File(path), sizeInMb, addBytes);
    }

    public RandomFileGenerator(File file, int sizeInMb, int addBytes) {
        this.file = file;
        this.sizeInMb = sizeInMb;
        this.addBytes = addBytes;
    }

    public void generate() throws IOException {
        file.createNewFile();
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(file, false));
        for (int i = 0; i < sizeInMb; i++) {
            outputStream.write(RandomStringUtils.randomAlphanumeric(1024 * 1024)
                    .getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        outputStream.write(RandomStringUtils.randomAlphanumeric(addBytes)
                .getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
