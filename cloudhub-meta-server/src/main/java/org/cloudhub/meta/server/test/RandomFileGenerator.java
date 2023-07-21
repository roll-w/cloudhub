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

package org.cloudhub.meta.server.test;

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
