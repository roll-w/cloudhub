package org.huel.cloudhub.file.server.service;

import org.huel.cloudhub.server.ServerStatusMonitor;

/**
 * @author RollW
 */
public interface Monitorable {
    ServerStatusMonitor getMonitor();
}
