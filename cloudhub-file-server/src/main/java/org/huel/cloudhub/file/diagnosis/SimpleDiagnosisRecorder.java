package org.huel.cloudhub.file.diagnosis;

import java.util.ArrayList;

/**
 * @author RollW
 */
public class SimpleDiagnosisRecorder<D> implements DiagnosisRecorder<D> {
    private final DiagnosisReport<D> report;

    public SimpleDiagnosisRecorder() {
        this.report = new DiagnosisReport<>(new ArrayList<>());
    }

    @Override
    public void record(DiagnosisReportSegment<D> report) {
        this.report.addSegment(report);
    }

    @Override
    public DiagnosisReport<D> getDiagnosisReport() {
        return report;
    }
}
