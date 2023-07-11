package org.huel.cloudhub.client.disk.jobs.trigger;

import org.huel.cloudhub.client.disk.jobs.JobEvent;

/**
 * @author RollW
 */
public class ConditionTriggerEvent<E, M> implements JobEvent {
    private final ConditionTrigger<E, M> conditionTrigger;
    private final E event;

    public ConditionTriggerEvent(ConditionTrigger<E, M> conditionTrigger,
                                 E event) {
        this.conditionTrigger = conditionTrigger;
        this.event = event;
    }

    public E getEvent() {
        return event;
    }

    @Override
    public ConditionTrigger<E, M> getJobTrigger() {
        return conditionTrigger;
    }
}
