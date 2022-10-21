package org.huel.cloudhub.file.server.heartbeat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Heartbeat properties, including meta server address.
 *
 * @author RollW
 */
@ConfigurationProperties("cloudhub.meta")
public class HeartbeatHostProperties {
    /**
     * The address of the meta server.
     */
    private String address;

    /**
     * in ms, the interval between two heartbeats.
     */
    private int heartbeatPeriod = 200;

    /**
     * allow the meta server changes the heartbeat period.
     */
    private boolean receiveChange = true;

    public HeartbeatHostProperties(String address, int heartbeatPeriod, boolean receiveChange) {
        this.address = address;
        this.heartbeatPeriod = heartbeatPeriod;
        this.receiveChange = receiveChange;
    }

    public HeartbeatHostProperties() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getHeartbeatPeriod() {
        return heartbeatPeriod;
    }

    public void setHeartbeatPeriod(int heartbeatPeriod) {
        this.heartbeatPeriod = heartbeatPeriod;
    }

    public boolean isReceiveChange() {
        return receiveChange;
    }

    public void setReceiveChange(boolean receiveChange) {
        this.receiveChange = receiveChange;
    }
}
