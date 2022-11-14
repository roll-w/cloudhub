package org.huel.cloudhub.file.diagnosis;

/**
 * @author RollW
 */
public interface Diagnosable<D> {
    DiagnosisReport<D> getDiagnosisReport();
}
