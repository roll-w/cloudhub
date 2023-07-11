package org.huel.cloudhub.client.disk.jobs;

/**
 * Job status. Not all statuses are used.
 *
 * @author RollW
 */
public enum JobStatus {
    NOT_STARTED,
    RUNNING,
    PAUSED,
    STOPPED,
    FINISHED,
    FAILED,
    CANCELLED
    ;
}
