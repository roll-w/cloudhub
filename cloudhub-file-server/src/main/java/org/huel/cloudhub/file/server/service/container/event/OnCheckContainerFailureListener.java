package org.huel.cloudhub.file.server.service.container.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.diagnosis.Diagnosable;
import org.huel.cloudhub.file.diagnosis.DiagnosisReport;
import org.huel.cloudhub.file.diagnosis.DiagnosisReportSegment;
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
    private final List<DiagnosisReportSegment<SerializedDamagedContainerReport>>
            segments = new ArrayList<>();

    @Override
    public void onApplicationEvent(@NonNull OnCheckContainerFailureEvent event) {
        DiagnosisReportSegment<SerializedDamagedContainerReport> diagnosisReport =
                new DiagnosisReportSegment<>(DiagnosisReportSegment.Type.DAMAGED,
                        event.getReport());
        segments.add(diagnosisReport);

    }

    @Override
    public DiagnosisReport<SerializedDamagedContainerReport> getDiagnosisReport() {
        return new DiagnosisReport<>(segments);
        // TODO: allocates report
    }
}
