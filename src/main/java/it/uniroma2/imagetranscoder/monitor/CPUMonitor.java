package it.uniroma2.imagetranscoder.monitor;

import it.uniroma2.imagetranscoder.WebAppContextListener;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.yammer.metrics.Gauge;
import com.yammer.metrics.MetricRegistry;

public class CPUMonitor{

	private Sigar sigar;
	private CpuPerc cpuPerc;
	private long pid;

	public CPUMonitor(Sigar sigar) {
		this.sigar = sigar;
		initialize();
	}

	private void initialize(){
		try {
			cpuPerc = sigar.getCpuPerc();
		} catch (SigarException e1) {
			e1.printStackTrace();
		}
		this.pid = sigar.getPid();
		WebAppContextListener.mRegistry.register(
				MetricRegistry.name("monitor.cpu.cpu-sys-percent"),
				new Gauge<Double>() {
					public Double getValue() {
						double ret = cpuPerc.getSys();
						return ret;
					}
				});
		WebAppContextListener.mRegistry.register(
				MetricRegistry.name("monitor.cpu.cpu-user-percent"),
				new Gauge<Double>() {
					public Double getValue() {
						double ret = cpuPerc.getUser();
						return ret;
					}

				});
		WebAppContextListener.mRegistry.register(
				MetricRegistry.name("monitor.cpu.cpu-idle-percent"),
				new Gauge<Double>() {
					public Double getValue() {
						double ret = cpuPerc.getIdle();
						return ret;
					}
				});
		WebAppContextListener.mRegistry.register(
				MetricRegistry.name("monitor.cpu.cpu-process-percent"),
				new Gauge<Double>() {
					public Double getValue() {
						double ret = 0;
						try {
							ret = sigar.getProcCpu(pid).getPercent();
						} catch (SigarException e) {
							e.printStackTrace();
						}
						return ret;
					}
				});		
	}
}
