package org.huel.cloudhub.client.disk.jobs.time;

import org.huel.cloudhub.client.disk.jobs.TriggerType;

/**
 * @author RollW
 */
public class TimeTriggerType implements TriggerType {
    private final Kind kind;
    private final long time;
    private final String cron;

    public TimeTriggerType(long time) {
        this.kind = Kind.SPECIFIC_TIME;
        this.time = time;
        this.cron = null;
    }

    public TimeTriggerType(String cron) {
        this.kind = Kind.PERIODICALLY;
        this.time = 0;
        this.cron = cron;
    }

    public Kind getKind() {
        return kind;
    }

    public long getTime() {
        return time;
    }

    public String getCron() {
        return cron;
    }

    public enum Kind {
        PERIODICALLY,
        SPECIFIC_TIME,
    }

}
