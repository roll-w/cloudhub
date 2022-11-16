package org.huel.cloudhub.file.server.service.container.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.diagnosis.Diagnosable;
import org.huel.cloudhub.file.diagnosis.DiagnosisReport;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Component
public class OnCheckContainerFailureListener implements
        ApplicationListener<OnCheckContainerFailureEvent>,
        Diagnosable<SerializedDamagedContainerReport> {
    private final List<SerializedDamagedContainerReport> reports = new ArrayList<>();

    @Override
    public void onApplicationEvent(@NonNull OnCheckContainerFailureEvent event) {

    }

    @Override
    public DiagnosisReport<SerializedDamagedContainerReport> getDiagnosisReport() {
        return null;
        // TODO: allocates report
    }
}
