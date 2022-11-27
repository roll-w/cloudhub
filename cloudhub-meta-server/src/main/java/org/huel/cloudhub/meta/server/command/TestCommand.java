package org.huel.cloudhub.meta.server.command;

import org.huel.cloudhub.meta.server.service.file.FileDeleteService;
import org.huel.cloudhub.meta.server.service.file.FileUploadService;
import org.huel.cloudhub.meta.server.test.WritePerformanceTester;
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
    private final FileDeleteService fileDeleteService;

    public TestCommand(FileUploadService fileUploadService,
                       FileDeleteService fileDeleteService) {
        this.fileUploadService = fileUploadService;
        this.fileDeleteService = fileDeleteService;
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    @ShellMethod(key = "test write", value = "write performance test.")
    public void startTest(int caseCount, int dataSize) throws IOException {
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
}
