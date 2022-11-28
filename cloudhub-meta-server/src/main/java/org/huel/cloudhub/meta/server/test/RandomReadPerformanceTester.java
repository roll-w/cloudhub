package org.huel.cloudhub.meta.server.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.rpc.block.DownloadBlockResponse;
import org.huel.cloudhub.meta.fs.FileObjectUploadStatus;
import org.huel.cloudhub.meta.server.service.file.FileDeleteService;
import org.huel.cloudhub.meta.server.service.file.FileDownloadCallback;
import org.huel.cloudhub.meta.server.service.file.FileDownloadService;
import org.huel.cloudhub.meta.server.service.file.FileDownloadingException;
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
 * 随机文件读取性能测试
 *
 * @author RollW
 */
@SuppressWarnings("all")
public class RandomReadPerformanceTester {
    private final PrintWriter writer;
    private final int testCases;
    private final int dataSize;
    private final int dimensions;
    private final FileDownloadService fileDownloadService;
    private final FileUploadService fileUploadService;
    private final FileDeleteService fileDeleteService;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final List<Result> results = new ArrayList<>();

    public RandomReadPerformanceTester(PrintWriter reportWriter, int testCases,
                                       int dimensions, int dataSize,
                                       FileDownloadService fileDownloadService,
                                       FileUploadService fileUploadService,
                                       @Nullable FileDeleteService fileDeleteService) {
        this.testCases = testCases;
        this.dataSize = dataSize;
        this.dimensions = dimensions;
        this.fileDownloadService = fileDownloadService;
        this.fileUploadService = fileUploadService;
        this.fileDeleteService = fileDeleteService;
        this.writer = reportWriter;
    }

    private List<String> generateFiles() throws IOException {
        List<String> fildIds = new ArrayList<>();
        for (int i = 0; i < testCases; i++) {
            File file = new File(RandomStringUtils.randomAlphanumeric(30));
            file.createNewFile();
            RandomFileGenerator generator = new
                    RandomFileGenerator(file, dataSize, 0);
            generator.generate();
            InputStream stream = new FileInputStream(file);
            CountDownLatch latch = new CountDownLatch(0);
            fileUploadService.uploadFile(stream, new FileUploadStatusDataCallback() {
                @Override
                public void onNextStatus(FileObjectUploadStatus status) {
                    if (status.isLastStatus()) {
                        latch.countDown();
                    }
                }

                @Override
                public void onCalc(String fileId) {
                    fildIds.add(fileId);
                }
            });
            IOUtils.closeQuietly(stream);
            file.delete();
        }
        return fildIds;
    }

    public void startRandomReadTest() throws IOException {
        List<String> files = generateFiles();
        for (int dimen = 1; dimen <= dimensions; dimen++) {
            int index = 1;
            for (String fileId : files) {
                recordNext(index, dimen, fileId);
                index++;
            }
        }
        printResult();
        if (fileDeleteService == null) {
            return;
        }
        for (String file : files) {
            fileDeleteService.deleteFileCompletely(file);
        }
    }

    private void recordNext(int index, int dimension, String fileId) {
        CountDownLatch latch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        fileDownloadService.downloadFile(NullOutputStream.INSTANCE, fileId,
                new Callback(latch, index, dimension, start, fileId));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private class Callback implements FileDownloadCallback {
        private final CountDownLatch latch;
        private final int index, dimension;
        private final long start;
        private long end, find;
        private final String fileId;

        private Callback(CountDownLatch latch, int index, int dimension, long start, String fileId) {
            this.latch = latch;
            this.index = index;
            this.dimension = dimension;
            this.start = start;
            this.fileId = fileId;
        }

        @Override
        public void onDownloadComplete() {
            end = System.currentTimeMillis();
            results.add(new Result(index, dimension, start, end, find, false, fileId));
            latch.countDown();
        }

        @Override
        public void onDownloadError(FileDownloadingException e) {
            results.add(new Result(index, dimension, start, end, find, true, fileId));
            latch.countDown();
        }

        @Override
        public void onSaveCheckMessage(DownloadBlockResponse.CheckMessage checkMessage) {
            find = System.currentTimeMillis();
        }
    }

    @SuppressWarnings("all")
    private void printResult() {
        printHeader();
        long netSum = 0, totalSum = 0, findSum = 0;
        for (Result result : results) {
            long downloadTime = result.end() - result.find();
            long findTime = result.find() - result.start();
            long totalTime = result.end() - result.start();
            netSum += downloadTime;
            totalSum += totalTime;
            findSum += findTime;

            double downloadRate = dataSize / (downloadTime / 1000d);
            double totalRate = dataSize / (totalTime / 1000d);
            printCase(result.dimension(), result.index(), result.start(), findTime, downloadTime, totalTime,
                    downloadRate, totalRate, result.error(), result.fileId());
        }
        printSummary(totalSum, netSum, findSum);
        printFooter();
    }

    private void printHeader() {
        writer.println("Start generate random read performance report at " + startTime + ".");
        writer.println();
        writer.printf("Result table. Cases=%d, Dimension=%d, DataSize=%d(MB)\n",
                testCases, dimensions, dataSize);
        writer.println("=========================================");
        writer.println("dimen  case  start(timestamp)  find(ms)  download(ms)  total(ms)  download(MB/s)  total(MB/s)  error  fileId(cut)");
        writer.flush();
    }

    private void printCase(int dimension, int index, long start, long find, long download, long total,
                           double downloadRate, double totalRate, boolean error, String fileId) {
        writer.printf("%-5d  %4d  %16d  %8d  %12d  %9d  %14.2f  %11.2f  %5b  %-16s\n",
                dimension, index, start, find, download, total,
                downloadRate, totalRate, error, fileId);
        writer.flush();
    }

    private void printSummary(long total, long totalDownload, long totalFind) {
        double avaTotal = (total * 1.d) / (testCases * dimensions);
        double avaNet = (totalDownload * 1.d) / (testCases * dimensions);
        double avaFind = (totalFind * 1.d) / (testCases * dimensions);
        double avaDownloadRate = (testCases * dimensions * dataSize) / (totalDownload / 1000d);
        double avaTotalRate = (testCases * dimensions * dataSize) / (total / 1000d);
        writer.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        writer.println("Result Summary:");
        writer.printf("                       Cases: %-7d\n", testCases);
        writer.printf("                  Dimensions: %-7d\n", dimensions);
        writer.printf("               Data Size(MB): %-7d\n", dataSize);
        writer.printf("                   total(ms): %-7d\n", total);
        writer.printf("           Average total(ms): %-7.2f\n", avaTotal);
        writer.printf("    Average total rate(MB/s): %-7.2f\n", avaTotalRate);
        writer.printf("                Sum find(ms): %-7d\n", totalFind);
        writer.printf("            Average find(ms): %-7.2f\n", avaFind);
        writer.printf("            Sum download(ms): %-7d\n", totalDownload);
        writer.printf("        Average download(ms): %-7.2f\n", avaNet);
        writer.printf(" Average download rate(MB/s): %-7.2f\n", avaDownloadRate);
    }

    private void printFooter() {
        LocalDateTime endTime = LocalDateTime.now();
        writer.println("=========================================");
        writer.println("Start: " + startTime);
        writer.println("  End: " + endTime);
        writer.println("Random read performance test report end. Generated by Cloudhub.");
        writer.flush();
    }

    private record Result(
            int index,
            int dimension,
            long start,
            long end,
            long find,
            boolean error,
            String fileId
    ) {
    }


}
