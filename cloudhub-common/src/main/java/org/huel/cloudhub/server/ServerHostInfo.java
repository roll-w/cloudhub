package org.huel.cloudhub.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author RollW
 */
public class ServerHostInfo {
    public static final long WAIT_REFRESH = 500;// 0.5s

    private static final RuntimeEnvironment RUNTIME_ENV = RuntimeEnvironment.load();

    @JsonIgnore
    private final HWDiskStore hwDiskStore;

    @JsonIgnore
    private final NetworkIF networkIF;

    @JsonProperty("cpu")
    private final CpuUsageInfo cpuUsageInfo;

    @JsonProperty("jvm")
    private final JvmUsageInfo jvmUsageInfo;

    @JsonProperty("mem")
    private final MemoryUsageInfo memoryUsageInfo;

    @JsonProperty("disk")
    private final DiskUsageInfo diskUsageInfo;

    @JsonProperty("net")
    private final NetworkUsageInfo networkUsageInfo;

    @JsonProperty("env")
    private final RuntimeEnvironment runtimeEnvironment;

    ServerHostInfo(HWDiskStore hwDiskStore,
                   NetworkIF networkIF,
                   CpuUsageInfo cpuUsageInfo,
                   JvmUsageInfo jvmUsageInfo,
                   MemoryUsageInfo memoryUsageInfo,
                   DiskUsageInfo diskUsageInfo,
                   NetworkUsageInfo networkUsageInfo,
                   RuntimeEnvironment runtimeEnvironment) {
        this.hwDiskStore = hwDiskStore;
        this.networkIF = networkIF;

        this.cpuUsageInfo = cpuUsageInfo;
        this.jvmUsageInfo = jvmUsageInfo;
        this.memoryUsageInfo = memoryUsageInfo;
        this.diskUsageInfo = diskUsageInfo;
        this.networkUsageInfo = networkUsageInfo;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public CpuUsageInfo getCpuUsageInfo() {
        return cpuUsageInfo;
    }

    public JvmUsageInfo getJvmUsageInfo() {
        return jvmUsageInfo;
    }

    public MemoryUsageInfo getMemoryUsageInfo() {
        return memoryUsageInfo;
    }

    public DiskUsageInfo getDiskUsageInfo() {
        return diskUsageInfo;
    }

    public NetworkUsageInfo getNetworkUsageInfo() {
        return networkUsageInfo;
    }

    public RuntimeEnvironment getRuntimeEnvironment() {
        return runtimeEnvironment;
    }

    @JsonIgnore
    public ServerHostInfo reload() {
        if (networkIF == null || hwDiskStore == null) {
            return this;
        }

        SystemInfo systemInfo = new SystemInfo();
        jvmUsageInfo.reload();
        memoryUsageInfo.reload(systemInfo.getHardware().getMemory());
        allWait(
                () -> cpuUsageInfo.reload(systemInfo.getHardware().getProcessor(), WAIT_REFRESH),
                () -> networkUsageInfo.reload(networkIF, WAIT_REFRESH),
                () -> diskUsageInfo.reload(hwDiskStore, WAIT_REFRESH)
        );

        return this;
    }

    private static final ExecutorService sThreadPool =
            Executors.newFixedThreadPool(3);


    private static void allWait(Runnable... runnables) {
        CompletableFuture[] completableFutures = new CompletableFuture[runnables.length];
        for (int i = 0; i < runnables.length; i++) {
            completableFutures[i] =
                    CompletableFuture.runAsync(runnables[i], sThreadPool);
        }
        try {
            CompletableFuture.allOf(completableFutures).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static ServerHostInfo load(String fullPath) {
        SystemInfo systemInfo = new SystemInfo();
        CpuUsageInfo cpuUsageInfo = CpuUsageInfo.load(systemInfo.getHardware().getProcessor());
        JvmUsageInfo jvmUsageInfo = JvmUsageInfo.load();
        MemoryUsageInfo memoryUsageInfo =
                MemoryUsageInfo.load(systemInfo.getHardware().getMemory());
        NetworkIF networkIF = findNetworkIF(systemInfo.getHardware());
        NetworkUsageInfo networkUsageInfo = NetworkUsageInfo.load(networkIF);

        HWDiskStore hwDiskStore = findDiskAt(fullPath, systemInfo.getHardware());
        DiskUsageInfo diskUsageInfo = DiskUsageInfo.load(fullPath);

        return new ServerHostInfo(hwDiskStore, networkIF, cpuUsageInfo,
                jvmUsageInfo, memoryUsageInfo, diskUsageInfo, networkUsageInfo, RUNTIME_ENV);
    }

    private static NetworkIF findNetworkIF(HardwareAbstractionLayer layer) {
        NetworkIF networkIF = findNetworkFromAddress(RUNTIME_ENV.getHostAddress(),
                layer.getNetworkIFs(false));
        if (networkIF == null) {
            throw new NullPointerException();
        }
        return networkIF;
    }

    private static NetworkIF findNetworkFromAddress(String ipv4Address,
                                                    List<NetworkIF> networkIFS) {
        for (NetworkIF networkIF : networkIFS) {
            if (networkIF.getIPv4addr().length == 0) {
                continue;
            }
            for (String s : networkIF.getIPv4addr()) {
                if (Objects.equals(ipv4Address, s)) {
                    return networkIF;
                }
            }
        }

        return null;
    }

    // bytes/s
    public static long calcRate(long last, long next, long timeInMs) {
        long diff = next - last;
        double sec = timeInMs / 1000d;
        if (timeInMs == 0 || diff == 0) {
            return 0;
        }
        return (long) (diff / sec);
    }

    private static HWDiskStore findDiskAt(String fullPath, HardwareAbstractionLayer layer) {
        List<HWDiskStore> hwDiskStores = layer.getDiskStores();
        for (HWDiskStore diskStore : layer.getDiskStores()) {
            for (HWPartition partition : diskStore.getPartitions()) {
                if (fullPath.startsWith(partition.getMountPoint())) {
                    return diskStore;
                }
            }
        }
        return hwDiskStores.get(0);
    }

    @JsonIgnore
    public ServerHostInfo fork() {
        return new ServerHostInfo(hwDiskStore, networkIF,
                getPersistedCpuUsageInfo(),
                getPersistedJvmUsageInfo(),
                getPersistedMemoryUsageInfo(),
                getPersistedDiskUsageInfo(),
                getPersistedNetworkInfo(), runtimeEnvironment);
    }

    @JsonIgnore
    public NetworkUsageInfo getPersistedNetworkInfo() {
        return networkUsageInfo.fork();
    }

    @JsonIgnore
    public CpuUsageInfo getPersistedCpuUsageInfo() {
        return cpuUsageInfo.fork();
    }

    @JsonIgnore
    public DiskUsageInfo getPersistedDiskUsageInfo() {
        return diskUsageInfo.fork();
    }

    @JsonIgnore
    public JvmUsageInfo getPersistedJvmUsageInfo() {
        return jvmUsageInfo.fork();
    }

    @JsonIgnore
    public MemoryUsageInfo getPersistedMemoryUsageInfo() {
        return memoryUsageInfo.fork();
    }
}
