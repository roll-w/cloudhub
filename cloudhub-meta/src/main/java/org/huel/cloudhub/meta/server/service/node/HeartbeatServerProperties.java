package org.huel.cloudhub.meta.server.service.node;

/**
 * @author RollW
 */
public class HeartbeatServerProperties {
    /**
     * In ms, the standard heartbeat period.
     * Will sends to file servers.
     */
    private int standardPeriod;
    /**
     * Timeout cycles for the heartbeat period.
     * <p>
     * For example: when the heartbeat period is 200ms and
     * the timeout cycle is set to 2, the following effect will occur:
     * when the last heartbeat to now interval exceeds 400ms,
     * the server will be removed from the active servers list.
     */
    private int timeoutCycle;

    public HeartbeatServerProperties(int standardPeriod, int timeoutCycle) {
        this.standardPeriod = standardPeriod;
        this.timeoutCycle = timeoutCycle;
    }

    public int getStandardPeriod() {
        return standardPeriod;
    }

    public void setStandardPeriod(int standardPeriod) {
        this.standardPeriod = standardPeriod;
    }

    public int getTimeoutCycle() {
        return timeoutCycle;
    }

    public void setTimeoutCycle(int timeoutCycle) {
        this.timeoutCycle = timeoutCycle;
    }
}
