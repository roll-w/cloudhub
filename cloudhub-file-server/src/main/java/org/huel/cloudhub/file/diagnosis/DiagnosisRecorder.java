package org.huel.cloudhub.file.diagnosis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class DiagnosisRecorder<D> {
    private DiagnosisReportSegment.Type reportType = DiagnosisReportSegment.Type.HEALTHY;

    private final List<DiagnosisReportSegment<D>> segments = new ArrayList<>();

    // TODO:
    public DiagnosisRecorder() {
    }

    public void onRecord(D d, DiagnosisReportSegment.Type type) {
        switch (type) {
            case HEALTHY -> onHealthy(d);
            case DAMAGED -> onPartDamaged(d);
            case DOWN -> onError(d);
        }
    }

    public void onHealthy(D d) {
        addLog(d, DiagnosisReportSegment.Type.HEALTHY);
    }

    public void onError(D d) {
        addLog(d, DiagnosisReportSegment.Type.DOWN);
        reportType = reportType.plus(DiagnosisReportSegment.Type.DOWN);
    }


    public void onPartDamaged(D d) {
        addLog(d, DiagnosisReportSegment.Type.DAMAGED);
        reportType = reportType.plus(DiagnosisReportSegment.Type.DAMAGED);
    }


    private void addLog(D d, DiagnosisReportSegment.Type type) {
        DiagnosisReportSegment<D> segment =
                new DiagnosisReportSegment<>(type, d);
        segments.add(segment);
    }

    public DiagnosisReport<D> generateReport() {
        return new DiagnosisReport<>();
    }

}
