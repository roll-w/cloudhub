package org.huel.cloudhub.file.server.service.container.event;

import org.huel.cloudhub.file.fs.container.ContainerType;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author RollW
 */
public class OnCheckContainerFailureEvent extends ApplicationEvent {
    private final SerializedDamagedContainerReport report;
    private String sourceId;
    private ContainerType containerType;
    private SynchroType synchroType;

    public OnCheckContainerFailureEvent(SerializedDamagedContainerReport source) {
        super(source);
        this.report = source;
    }

    public OnCheckContainerFailureEvent(SerializedDamagedContainerReport source, Clock clock) {
        super(source, clock);
        this.report = source;
    }

    public OnCheckContainerFailureEvent(SerializedDamagedContainerReport report,
                                        String sourceId,
                                        ContainerType containerType,
                                        SynchroType synchroType) {
        super(report);
        this.report = report;
        this.sourceId = sourceId;
        this.containerType = containerType;
        this.synchroType = synchroType;
    }

    public SerializedDamagedContainerReport getReport() {
        return report;
    }


    public String getSourceId() {
        return sourceId;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public SynchroType getSynchroType() {
        return synchroType;
    }

    public enum SynchroType {
        META,
        CONTAINER,
        ALL, NONE
    }

}
