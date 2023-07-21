/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.server.service.heartbeat;

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
    private int heartbeatPeriod = 500;


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
