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

package org.cloudhub.file;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * A properties class that can store UTF-8 characters without escaping,
 * and removes the date and time information in the comment.
 *
 * @author RollW
 */
public class CleanProperties extends Properties {

    @Override
    public synchronized void load(Reader reader) throws IOException {
        super.load(reader);
    }

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer
                        : new BufferedWriter(writer),
                comments
        );
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        store0(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)),
                comments
        );
    }


    private void store0(BufferedWriter bw, String comments)
            throws IOException {
        if (comments != null) {
            writeComments(bw, comments);
        }
        bw.newLine();
        synchronized (this) {
            for (Map.Entry<Object, Object> e : entrySet()) {
                String key = (String) e.getKey();
                String val = (String) e.getValue();
                key = saveConvert(key, true);
                /*
                 * No need to escape embedded and trailing spaces for value, hence
                 * pass false to flag.
                 */
                val = saveConvert(val, false);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }

    private String saveConvert(String theString,
                               boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);
        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ' -> {
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                }
                case '\t' -> {
                    outBuffer.append('\\');
                    outBuffer.append('t');
                }
                case '\n' -> {
                    outBuffer.append('\\');
                    outBuffer.append('n');
                }
                case '\r' -> {
                    outBuffer.append('\\');
                    outBuffer.append('r');
                }
                case '\f' -> {
                    outBuffer.append('\\');
                    outBuffer.append('f');
                }
                case '=', ':', '#', '!' -> {
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                }
                default -> outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    private static void writeComments(BufferedWriter bw, String comments)
            throws IOException {
        bw.write("# ");
        int len = comments.length();
        int current = 0;
        int last = 0;
        while (current < len) {
            char c = comments.charAt(current);
            if (c == '\n' || c == '\r') {
                if (last != current)
                    bw.write(comments.substring(last, current));
                bw.newLine();
                if (c == '\r' &&
                        current != len - 1 &&
                        comments.charAt(current + 1) == '\n') {
                    current++;
                }
                if (current == len - 1 ||
                        (comments.charAt(current + 1) != '#' &&
                                comments.charAt(current + 1) != '!'))
                    bw.write("# ");
                last = current + 1;
            }
            current++;
        }
        if (last != current)
            bw.write(comments.substring(last, current));
        bw.newLine();
    }

}