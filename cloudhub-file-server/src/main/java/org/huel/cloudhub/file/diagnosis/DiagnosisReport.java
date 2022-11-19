package org.huel.cloudhub.file.diagnosis;

import java.util.List;

/**
 * @author RollW
 */
public class DiagnosisReport<D> {
    private final List<DiagnosisReportSegment<D>> segments;
    public DiagnosisReport(List<DiagnosisReportSegment<D>> segments) {
        this.segments = segments;
    }

    // TODO:


    public List<DiagnosisReportSegment<D>> getSegments() {
        return segments;
    }
}
