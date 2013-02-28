package it.uniroma2.imagetranscoder.monitor;

import it.uniroma2.imagetranscoder.WebAppContextListener;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.yammer.metrics.core.Gauge;

public class MemMonitorThread extends Thread {

	private Sigar sigar;
	private long sleep;

	public MemMonitorThread(Sigar sigar, long sleep) {
		this.sigar = sigar;
		this.sleep = sleep;
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				WebAppContextListener.mRegistry.newGauge(MemMonitorThread.class, "memory-used-percent", new Gauge<Double>() {
				    @Override
				    public Double value() {
				        double freePercent = 0D;
				    	try {
							freePercent = sigar.getMem().getUsedPercent();
						} catch (SigarException e) {
							e.printStackTrace();
						}
				    	return freePercent;
				    }
				    
				});
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
