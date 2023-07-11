package org.huel.cloudhub.client.disk.jobs.trigger;

import org.springframework.expression.ExpressionException;
import org.springframework.scheduling.support.CronExpression;

/**
 * @author RollW
 */
public class TimeTriggerType {
    private final Kind kind;
    private final long time;
    private final String cron;
    private final CronExpression cronExpression;

    public TimeTriggerType(long time) {
        this.kind = Kind.SPECIFIC_TIME;
        this.time = time;
        this.cron = null;
        this.cronExpression = null;
    }

    public TimeTriggerType(String cron) {
        this.kind = Kind.PERIODICALLY;
        this.time = 0;
        this.cron = cron;
        try {
            this.cronExpression = CronExpression.parse(cron);
        } catch (IllegalArgumentException e) {
            throw new ExpressionException("Cron expression is invalid [" + cron + "].");
        }
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

    public CronExpression getCronExpression() {
        return cronExpression;
    }

    public enum Kind {
        PERIODICALLY,
        SPECIFIC_TIME,
    }

    public static TimeTriggerType of(long time) {
        return new TimeTriggerType(time);
    }

    public static TimeTriggerType of(String cron) {
        return new TimeTriggerType(cron);
    }
}
