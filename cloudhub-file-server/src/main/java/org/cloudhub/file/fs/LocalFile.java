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

package org.cloudhub.file.fs;

import space.lingu.NonNull;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class LocalFile implements ServerFile {
    private final File file;


    public LocalFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        this.file = file;
    }

    public LocalFile(String path) {
        this(new File(path));
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getPath() {
        return file.getAbsolutePath();
    }

    @Override
    public long length() throws IOException {
        var res = Files.readAttributes(file.toPath(), "size");
        return (long) res.get("size");
    }

    @Override
    public long lastModified() throws IOException {
        return Files.getLastModifiedTime(file.toPath()).toMillis();
    }

    @Override
    public OutputStream openOutput(boolean overwrite) throws IOException {
        return new FileOutputStream(file, !overwrite);
    }

    @Override
    public InputStream openInput() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public boolean delete() {
        return file.delete();
    }

    @Override
    public boolean delete(boolean recursive) {
        return false;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean createFile() throws IOException {
        if (exists()) {
            return false;
        }
        return file.createNewFile();
    }

    @Override
    public boolean mkdirs() {
        if (file.exists()) {
            return false;
        }
        return file.mkdirs();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public ServerFile getParent() {
        return new LocalFile(file.getParentFile());
    }

    @NonNull
    @Override
    public List<ServerFile> listFiles() {
        File[] files = file.listFiles();
        if (files == null) {
            return List.of();
        }
        List<ServerFile> serverFiles = new ArrayList<>();
        for (File f : files) {
            ServerFile serverFile = new LocalFile(f);
            serverFiles.add(serverFile);
        }
        return serverFiles;
    }

    @NonNull
    @Override
    public List<ServerFile> listFiles(boolean recursive) throws IOException {
        // todo:
        return List.of();
    }
}
