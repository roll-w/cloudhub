package org.huel.cloudhub.meta.server.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author RollW
 */
public class RandomFileGenerator {
    private static final int BUFFER_SIZE = 1024 * 1024;

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

    private static final Random RANDOM = new Random();

    public void generate() throws IOException {
        file.createNewFile();
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, false));
        write(sizeInMb, outputStream);
        if (addBytes > 0) {
            byte[] data = new byte[addBytes];
            RANDOM.nextBytes(data);
            outputStream.write(data);
            outputStream.flush();
        }
        outputStream.close();
    }

    private void write(int size, OutputStream outputStream) throws IOException {
        for (int i = 0; i < size; i++) {
            byte[] data = new byte[BUFFER_SIZE];
            RANDOM.nextBytes(data);
            outputStream.write(data);
        }
        outputStream.flush();
    }
}
