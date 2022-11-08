package org.huel.cloudhub.meta.server.service.node;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author RollW
 */
@ConfigurationProperties("cloudhub.heartbeat")
public class HeartbeatServerProperties {
    /**
     * In ms, the standard heartbeat period.
     * Will sends to file servers.
     */
    private int standardPeriod = 1000;
    /**
     * Timeout cycles for the heartbeat period.
     * <p>
     * For example: when the heartbeat period is 200ms and
     * the timeout cycle is set to 2, the following effect will occur:
     * when the last heartbeat to now interval exceeds 400ms,
     * the server will be removed from the active servers list.
     */
    private int timeoutCycle = 2;

    public HeartbeatServerProperties(int standardPeriod, int timeoutCycle) {
        this.standardPeriod = standardPeriod;
        this.timeoutCycle = timeoutCycle;
    }

    public HeartbeatServerProperties() {
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
