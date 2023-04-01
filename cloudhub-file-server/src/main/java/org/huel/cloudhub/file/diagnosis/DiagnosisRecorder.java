package org.huel.cloudhub.file.diagnosis;

/**
 * @author RollW
 */
public interface DiagnosisRecorder<D> extends Diagnosable<D> {
    void record(DiagnosisReportSegment<D> report);
}
