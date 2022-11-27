package org.huel.cloudhub.meta.server.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.meta.fs.FileObjectUploadStatus;
import org.huel.cloudhub.meta.server.service.file.FileDeleteService;
import org.huel.cloudhub.meta.server.service.file.FileUploadService;
import org.huel.cloudhub.meta.server.service.file.FileUploadStatusDataCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 写入性能测试
 *
 * @author RollW
 */
public class WritePerformanceTester {
    private final PrintWriter writer;
    private final int testCases;
    private final int dataSize;
    private final FileUploadService fileUploadService;
    private final FileDeleteService fileDeleteService;
    private final CountDownLatch latch;
    private final List<FileDelete> tempFiles = new ArrayList<>();

    // if file delete service not null, enable auto-cleaning.
    public WritePerformanceTester(PrintWriter reportWriter, int testCases, int dataSize,
                                  FileUploadService fileUploadService,
                                  @Nullable FileDeleteService fileDeleteService) {
        this.testCases = testCases;
        this.dataSize = dataSize;
        this.fileUploadService = fileUploadService;
        this.fileDeleteService = fileDeleteService;
        this.writer = reportWriter;
        this.latch = new CountDownLatch(testCases);
    }

    public void startWriteTest() throws IOException, InterruptedException {
        printTableHeader();
        for (int i = 1; i <= testCases; i++) {
            recordNext(i);
        }
        latch.await();
        printFooter();

        for (FileDelete tempFile : tempFiles) {
            tempFile.file.delete();
            if (fileDeleteService != null) {
                fileDeleteService.deleteFileCompletely(tempFile.fileId());
            }
        }
    }


    private File generateRandomFile() throws IOException {
        File file = new File(RandomStringUtils.randomAlphanumeric(30));
        RandomFileGenerator fileGenerator =
                new RandomFileGenerator(file, dataSize, 0);
        fileGenerator.generate();
        return file;
    }

    private void recordNext(int index) throws IOException {
        File randomFile = generateRandomFile();
        InputStream stream = new FileInputStream(randomFile);
        long start = System.currentTimeMillis();
        fileUploadService.uploadFile(stream,
                new CallbackRecorder(index, start, randomFile));
    }

    private class CallbackRecorder implements FileUploadStatusDataCallback {
        private final int caseIndex;
        private final long start;
        private final File randomFile;
        private String fileId;

        long calc, total, temp, store, tempToStore, storeToLast;

        private CallbackRecorder(int caseIndex, long start, File randomFile) {
            this.caseIndex = caseIndex;
            this.start = start;
            this.randomFile = randomFile;
        }

        @Override
        public void onNextStatus(FileObjectUploadStatus status) {
            if (status == FileObjectUploadStatus.TEMPORARY) {
                temp = System.currentTimeMillis();
                calc = temp - start;
                return;
            }
            if (status == FileObjectUploadStatus.STORING) {
                store = System.currentTimeMillis();
                return;
            }

            if (status.isLastStatus()) {
                long end = System.currentTimeMillis();
                total = end - start;
                tempToStore = store - temp;
                storeToLast = end - store;
                printCase(caseIndex, start, calc, total, tempToStore, storeToLast);
                clean();
            }
        }

        private void clean() {
            tempFiles.add(new FileDelete(randomFile, fileId));

            latch.countDown();
        }

        @Override
        public void onCalc(String fileId) {
            this.fileId = fileId;
        }
    }

    private record FileDelete(
            File file,
            String fileId
    ) {
    }

    private void printTableHeader() {
        LocalDateTime localDateTime = LocalDateTime.now();
        writer.println("Start generate write performance report at " + localDateTime + ".");
        writer.println("Note: total time not contains the random file generate time.");
        writer.printf("Cases=%d, DataSize=%d(MB)\n", testCases, dataSize);
        writer.println("=======================");
        writer.println("case    start(timestamp)  calc(ms)  total(ms)  temp->store(ms)  store->last(ms)");
    }

    private void printCase(int caseIndex, long start, long calc, long total, long tempToStore, long storeToLast) {
        writer.printf("%4d    %16d  %8d  %9d  %15d  %15d\n",
                caseIndex, start, calc, total, tempToStore, storeToLast);
    }

    private void printFooter() {
        writer.println("=======================");
        writer.println("Write performance test report end. Generated by Cloudhub.");
    }
}
