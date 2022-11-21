package org.huel.cloudhub.server;

import org.huel.cloudhub.server.rpc.server.SerializedCpuUsageInfo;

/**
 * @author RollW
 */
public class ServerInfoSerializeHelper {
    public static CpuUsageInfo deserializeFrom(SerializedCpuUsageInfo serializedCpuUsageInfo) {
        return new CpuUsageInfo(
                serializedCpuUsageInfo.getCpuCores(),
                serializedCpuUsageInfo.getSysUsed(),
                serializedCpuUsageInfo.getUserUsed(),
                serializedCpuUsageInfo.getWait(),
                serializedCpuUsageInfo.getFree());

    }

    public static SerializedCpuUsageInfo serializeFrom(CpuUsageInfo cpuUsageInfo) {
        return SerializedCpuUsageInfo.newBuilder()
                .setCpuCores(cpuUsageInfo.getCpuCores())
                .setFree(cpuUsageInfo.getFree())
                .setWait(cpuUsageInfo.getWait())
                .setSysUsed(cpuUsageInfo.getSysUsed())
                .setUserUsed(cpuUsageInfo.getUserUsed())
                .build();
    }

    private ServerInfoSerializeHelper() {
    }

}
