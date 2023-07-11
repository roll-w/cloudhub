package org.huel.cloudhub.client.disk.jobs.trigger;

import org.huel.cloudhub.client.disk.event.EventCallback;
import org.huel.cloudhub.client.disk.event.EventRegistry;
import org.huel.cloudhub.client.disk.jobs.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class ConditionTrigger<R, M> implements JobTrigger,
        EventCallback<R> {

    private final AtomicLong lastTime = new AtomicLong();
    private final EventRegistry<R, M> eventRegistry;
    private final M condition;

    private String eventId;

    private JobTask jobTask;
    private JobExecutor jobExecutor;

    private JobStatus jobStatus;

    public ConditionTrigger(EventRegistry<R, M> eventRegistry,
                            M condition) {

        this.eventRegistry = eventRegistry;
        this.condition = condition;
    }

    @Override
    public void start() {
        if (jobStatus == JobStatus.RUNNING) {
            return;
        }
        jobStatus = JobStatus.RUNNING;
        eventId = eventRegistry.register(this, condition);
    }

    @Override
    public void stop() {
        if (jobStatus != JobStatus.RUNNING) {
            return;
        }
        jobStatus = JobStatus.STOPPED;
        eventRegistry.unregister(eventId);
    }

    @Override
    public JobTask getJobTask() {
        return jobTask;
    }

    @Override
    public void setJobTask(JobTask jobTask) {
        this.jobTask = jobTask;
    }

    @Override
    public JobExecutor getJobExecutor() {
        return jobExecutor;
    }

    @Override
    public void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    @Override
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    @Override
    public long lastExecuteTime() {
        return lastTime.get();
    }

    @Override
    public long nextExecuteTime() {
        // cannot calculate next execute time
        return 0;
    }

    @Override
    public String getName() {
        return "ConditionTrigger[Condition=" + condition +
                ";Registry=" + eventRegistry.getClass().getSimpleName() +
                ";EventId=" + eventId + "]";
    }

    @Override
    public void onEvent(R event) {
        lastTime.set(System.currentTimeMillis());
        execute(event);
    }

    private void execute(R event) {
        jobExecutor.execute(
                jobTask,
                new ConditionTriggerEvent<>(this, event)
        );
    }
}
