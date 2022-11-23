package org.huel.cloudhub.server;

import org.huel.cloudhub.server.rpc.server.*;

/**
 * @author RollW
 */
public class ServerInfoSerializeHelper {

    private ServerInfoSerializeHelper() {
    }

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

    public static DiskUsageInfo deserializeFrom(
            SerializedDiskUsageInfo serializedDiskUsageInfo) {
        return new DiskUsageInfo(
                serializedDiskUsageInfo.getFree(),
                serializedDiskUsageInfo.getRead(),
                serializedDiskUsageInfo.getTotal(),
                serializedDiskUsageInfo.getWrite(),
                null
        );

    }

    public static SerializedDiskUsageInfo serializeFrom(DiskUsageInfo diskUsageInfo) {
        return SerializedDiskUsageInfo.newBuilder()
                .setFree(diskUsageInfo.getFree())
                .setRead(diskUsageInfo.getRead())
                .setTotal(diskUsageInfo.getTotal())
                .setWrite(diskUsageInfo.getWrite())
                .build();
    }

    public static JvmUsageInfo deserializeFrom(SerializedJvmUsageInfo serializedJvmUsageInfo) {
        return new JvmUsageInfo(
                serializedJvmUsageInfo.getTotal(),
                serializedJvmUsageInfo.getMax(),
                serializedJvmUsageInfo.getFree());
    }

    public static SerializedJvmUsageInfo serializeFrom(JvmUsageInfo JvmUsageInfo) {
        return SerializedJvmUsageInfo.newBuilder()
                .setFree(JvmUsageInfo.getFree())
                .setTotal(JvmUsageInfo.getTotal())
                .setMax(JvmUsageInfo.getMax())
                .build();
    }

    public static MemoryUsageInfo deserializeFrom(SerializedMemoryUsageInfo serializedMemoryUsageInfo) {
        return new MemoryUsageInfo(
                serializedMemoryUsageInfo.getTotal(),
                serializedMemoryUsageInfo.getUsed(),
                serializedMemoryUsageInfo.getFree()
        );
    }

    public static SerializedMemoryUsageInfo serializeFrom(MemoryUsageInfo memoryUsageInfo) {
        return SerializedMemoryUsageInfo.newBuilder()
                .setFree(memoryUsageInfo.getFree())
                .setTotal(memoryUsageInfo.getTotal())
                .setUsed(memoryUsageInfo.getUsed())
                .build();
    }

    public static NetworkUsageInfo deserializeFrom(SerializedNetworkUsageInfo serializedNetworkUsageInfo) {
        return new NetworkUsageInfo(
                serializedNetworkUsageInfo.getRecv(),
                serializedNetworkUsageInfo.getSent(),
                serializedNetworkUsageInfo.getSpeed()
        );
    }

    public static SerializedNetworkUsageInfo serializeFrom(NetworkUsageInfo networkUsageInfo) {
        return SerializedNetworkUsageInfo.newBuilder()
                .setRecv(networkUsageInfo.getRecv())
                .setSent(networkUsageInfo.getSent())
                .setSpeed(networkUsageInfo.getSpeed())
                .build();
    }

    public static RuntimeEnvironment deserializeFrom(SerializedRuntimeEnvironment runtimeEnvironment) {
        return new RuntimeEnvironment(
                runtimeEnvironment.getHostName(),
                runtimeEnvironment.getHostAddress(),
                runtimeEnvironment.getRunUser(),
                runtimeEnvironment.getUserHome(),
                runtimeEnvironment.getWorkDir(),
                runtimeEnvironment.getJavaVersion(),
                runtimeEnvironment.getJavaHome(),
                runtimeEnvironment.getOsName(),
                runtimeEnvironment.getOsVersion(),
                runtimeEnvironment.getOsArch()
        );
    }

    public static SerializedRuntimeEnvironment serializeFrom(RuntimeEnvironment runtimeEnvironment) {
        return SerializedRuntimeEnvironment.newBuilder()
                .setHostAddress(runtimeEnvironment.getHostAddress())
                .setHostAddress(runtimeEnvironment.getHostAddress())
                .setHostName(runtimeEnvironment.getHostName())
                .setRunUser(runtimeEnvironment.getRunUser())
                .setUserHome(runtimeEnvironment.getUserHome())
                .setWorkDir(runtimeEnvironment.getWorkDir())
                .setJavaVersion(runtimeEnvironment.getJavaVersion())
                .setJavaHome(runtimeEnvironment.getJavaHome())
                .setOsName(runtimeEnvironment.getOsName())
                .setOsVersion(runtimeEnvironment.getOsVersion())
                .setOsArch(runtimeEnvironment.getOsArch())
                .build();
    }


    public static SerializedServerStatus serializeFrom(ServerHostInfo serverHostInfo) {
        return SerializedServerStatus.newBuilder()
                .setCpuInfo(serializeFrom(serverHostInfo.getPersistedCpuUsageInfo()))
                .setJvmInfo(serializeFrom(serverHostInfo.getPersistedJvmUsageInfo()))
                .setMemInfo(serializeFrom(serverHostInfo.getMemoryUsageInfo()))
                .setDiskInfo(serializeFrom(serverHostInfo.getDiskUsageInfo()))
                .setNetInfo(serializeFrom(serverHostInfo.getNetworkUsageInfo()))
                .build();
    }

    public static ServerHostInfo deserializeFrom(SerializedServerStatus serializedServerStatus) {
        return new ServerHostInfo(
                null, null,
                deserializeFrom(serializedServerStatus.getCpuInfo()),
                deserializeFrom(serializedServerStatus.getJvmInfo()),
                deserializeFrom(serializedServerStatus.getMemInfo()),
                deserializeFrom(serializedServerStatus.getDiskInfo()),
                deserializeFrom(serializedServerStatus.getNetInfo()),
                deserializeFrom(serializedServerStatus.getEnvInfo()));
    }

}
