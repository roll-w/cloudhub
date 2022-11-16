package org.huel.cloudhub.file.server.service.container.event;

import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author RollW
 */
public class OnCheckContainerFailureEvent extends ApplicationEvent {
    private final SerializedDamagedContainerReport report;

    public OnCheckContainerFailureEvent(SerializedDamagedContainerReport source) {
        super(source);
        this.report = source;
    }

    public OnCheckContainerFailureEvent(SerializedDamagedContainerReport source, Clock clock) {
        super(source, clock);
        this.report = source;
    }

    public SerializedDamagedContainerReport getReport() {
        return report;
    }
}
