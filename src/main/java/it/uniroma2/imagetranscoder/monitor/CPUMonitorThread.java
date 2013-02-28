package it.uniroma2.imagetranscoder.monitor;

import it.uniroma2.imagetranscoder.WebAppContextListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.reporting.GraphiteReporter;

public class CPUMonitorThread extends Thread {

	private Sigar sigar;
	private long sleep;
	private CpuPerc cpuPerc;
	private long pid;

	public CPUMonitorThread(Sigar sigar, long sleep) {
		this.sigar = sigar;
		this.sleep = sleep;
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				cpuPerc = sigar.getCpuPerc();
				this.pid = sigar.getPid();
				WebAppContextListener.mRegistry.newGauge(
						CPUMonitorThread.class, "cpu-sys-percent",
						new Gauge<Double>() {
							@Override
							public Double value() {
								double ret = cpuPerc.getSys();
								return ret;
							}

						});
				WebAppContextListener.mRegistry.newGauge(
						CPUMonitorThread.class, "cpu-user-percent",
						new Gauge<Double>() {
							@Override
							public Double value() {
								double ret = cpuPerc.getUser();
								return ret;
							}

						});
				WebAppContextListener.mRegistry.newGauge(
						CPUMonitorThread.class, "cpu-idle-percent",
						new Gauge<Double>() {
							@Override
							public Double value() {
								double ret = cpuPerc.getIdle();
								return ret;
							}

						});
				WebAppContextListener.mRegistry.newGauge(
						CPUMonitorThread.class, "cpu-process-percent",
						new Gauge<Double>() {
							@Override
							public Double value() {
								double ret = 0;
								try {
									ret = sigar.getProcCpu(pid).getPercent();
								} catch (SigarException e) {
									e.printStackTrace();
								}
								return ret;
							}
						});
				Thread.sleep(sleep);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		System.out.println(System.getProperty("java.library.path"));
		System.out.println(System.getProperty("JAVA_OPTS"));
		GraphiteReporter
				.enable(WebAppContextListener.mRegistry, 1, TimeUnit.MINUTES,
						"23.22.114.15", 2023, ""
								+ InetAddress.getLocalHost().getHostName()
										.replace('.', '-'));
		CPUMonitorThread mem = new CPUMonitorThread(new Sigar(), 1000);
		mem.run();
	}
}
