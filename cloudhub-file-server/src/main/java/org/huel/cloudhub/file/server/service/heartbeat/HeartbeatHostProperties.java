package org.huel.cloudhub.file.server.service.heartbeat;

/**
 * Heartbeat properties, including meta server address.
 *
 * @author RollW
 */
public class HeartbeatHostProperties {
    /**
     * The address of the meta server.
     */
    private String address;

    /**
     * in ms, the interval between two heartbeats.
     */
    private int heartbeatPeriod = 200;


    public HeartbeatHostProperties(String address, int heartbeatPeriod) {
        this.address = address;
        this.heartbeatPeriod = heartbeatPeriod;
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

}
