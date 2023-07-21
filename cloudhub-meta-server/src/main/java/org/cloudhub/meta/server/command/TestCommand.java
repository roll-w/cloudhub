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

package org.cloudhub.meta.server.command;

import org.cloudhub.meta.server.service.file.FileDeleteService;
import org.cloudhub.meta.server.service.file.FileDownloadService;
import org.cloudhub.meta.server.service.file.FileUploadService;
import org.cloudhub.meta.server.test.RandomReadPerformanceTester;
import org.cloudhub.meta.server.test.ReadPerformanceTester;
import org.cloudhub.meta.server.test.WritePerformanceTester;
import org.cloudhub.meta.server.service.file.FileDeleteService;
import org.cloudhub.meta.server.service.file.FileDownloadService;
import org.cloudhub.meta.server.service.file.FileUploadService;
import org.cloudhub.meta.server.test.RandomReadPerformanceTester;
import org.cloudhub.meta.server.test.ReadPerformanceTester;
import org.cloudhub.meta.server.test.WritePerformanceTester;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author RollW
 */
@ShellComponent
public class TestCommand extends AbstractShellComponent {
    private final FileUploadService fileUploadService;
    private final FileDownloadService fileDownloadService;
    private final FileDeleteService fileDeleteService;

    public TestCommand(FileUploadService fileUploadService,
                       FileDownloadService fileDownloadService,
                       FileDeleteService fileDeleteService) {
        this.fileUploadService = fileUploadService;
        this.fileDownloadService = fileDownloadService;
        this.fileDeleteService = fileDeleteService;
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    @ShellMethod(key = "test write", value = "write performance test.")
    public void testWrite(int caseCount, int dataSize) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        File report = new File("cloudhub-test-write-report_" + time.format(FORMATTER) + ".clb");
        report.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(report, false));
        WritePerformanceTester tester = new WritePerformanceTester(
                writer, caseCount, dataSize,
                fileUploadService, fileDeleteService);
        tester.startWriteTest();
        writer.close();
    }

    @ShellMethod(key = "test read", value = "read performance test.")
    public void testRead(int caseCount, int dataSize) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        File report = new File("cloudhub-test-read-report_" + time.format(FORMATTER) + ".clb");
        report.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(report, false));
        ReadPerformanceTester tester = new ReadPerformanceTester(
                writer, caseCount, dataSize,
                fileDownloadService, fileUploadService,
                fileDeleteService);
        tester.startReadTest();
        writer.close();
    }

    @ShellMethod(key = "test random read", value = "read performance test.")
    public void testRandomRead(int caseCount, int dimension, int dataSize) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        File report = new File("cloudhub-test-random-read-report_" + time.format(FORMATTER) + ".clb");
        report.createNewFile();
        PrintWriter writer = new PrintWriter(new FileOutputStream(report, false));
        RandomReadPerformanceTester tester = new RandomReadPerformanceTester(
                writer, caseCount, dimension, dataSize,
                fileDownloadService, fileUploadService,
                fileDeleteService);
        tester.startRandomReadTest();
        writer.close();
    }
}
