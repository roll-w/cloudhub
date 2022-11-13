package org.huel.cloudhub.server;

import oshi.hardware.CentralProcessor;
import oshi.util.Util;

/**
 * @author RollW
 */
public class CpuUsageInfo {
    private final int cpuCores;
    private double sysUsed;
    private double userUsed;
    private double wait;
    private double free;

    private CpuUsageInfo(int cpuCores) {
      this.cpuCores = cpuCores;
    }

    public CpuUsageInfo(int cpuCores, double sysUsed, double userUsed,
                        double wait, double free) {
        this.cpuCores = cpuCores;
        this.sysUsed = sysUsed;
        this.userUsed = userUsed;
        this.wait = wait;
        this.free = free;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public double getSysUsed() {
        return sysUsed;
    }

    public double getUserUsed() {
        return userUsed;
    }

    public double getWait() {
        return wait;
    }

    public double getFree() {
        return free;
    }

    public CpuUsageInfo fork() {
        return new CpuUsageInfo(cpuCores, sysUsed, userUsed, wait, free);
    }

    protected CpuUsageInfo reload(CentralProcessor centralProcessor, long ms) {
        long[] prevTicks = centralProcessor.getSystemCpuLoadTicks();
        Util.sleep(ms);
        long[] ticks = centralProcessor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softline = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long total = user + nice + cSys + idle + ioWait + irq + softline + steal;
        this.sysUsed = toPercentage(1.0d * cSys / total);
        this.userUsed = toPercentage(1.0 * user / total);
        this.free = toPercentage(1.0 * idle / total);
        this.wait =  toPercentage(1.0 * ioWait / total);
        return this;
    }

    public static CpuUsageInfo load(CentralProcessor centralProcessor) {
        return new CpuUsageInfo(centralProcessor.getLogicalProcessorCount());
    }

    private static double toPercentage(double d) {
        return formatDouble(d * 100d);
    }

    private static double formatDouble(double d) {
        String s = String.format("%.2f", d);
        return Double.parseDouble(s);
    }
}
