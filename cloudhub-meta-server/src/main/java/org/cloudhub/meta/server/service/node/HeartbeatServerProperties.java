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

package org.cloudhub.meta.server.service.node;

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
