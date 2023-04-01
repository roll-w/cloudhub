package org.huel.cloudhub.file.server.service;

import org.huel.cloudhub.file.diagnosis.Diagnosable;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;

/**
 * @author RollW
 */
public interface ContainerDiagnosable {
    Diagnosable<SerializedDamagedContainerReport> getDiagnosable();
}
