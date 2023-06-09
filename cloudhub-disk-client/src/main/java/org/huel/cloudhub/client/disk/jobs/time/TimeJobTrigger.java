package org.huel.cloudhub.client.disk.jobs.time;

import org.huel.cloudhub.client.disk.jobs.JobTask;
import org.huel.cloudhub.client.disk.jobs.JobTrigger;
import org.huel.cloudhub.client.disk.jobs.TriggerType;
import org.springframework.expression.ExpressionException;
import org.springframework.scheduling.support.CronExpression;

/**
 * @author RollW
 */
public class TimeJobTrigger implements JobTrigger {
    private final TimeTriggerType timeTriggerType;

    public TimeJobTrigger(TimeTriggerType timeTriggerType) {
        this.timeTriggerType = timeTriggerType;
        if (timeTriggerType.getKind() == TimeTriggerType.Kind.PERIODICALLY) {
            checkCronExpression();
        }
    }

    // TODO: impl
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public JobTask getJobTask() {
        return null;
    }

    @Override
    public void setJobTask(JobTask jobTask) {

    }

    @Override
    public TriggerType getTriggerType() {
        return timeTriggerType;
    }

    private void checkCronExpression() {
        // check cron expression
        if (!CronExpression.isValidExpression(timeTriggerType.getCron())) {
            throw new ExpressionException("Cron expression is invalid");
        }
    }
}
