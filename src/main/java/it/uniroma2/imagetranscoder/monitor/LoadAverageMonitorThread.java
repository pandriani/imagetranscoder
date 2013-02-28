package it.uniroma2.imagetranscoder.monitor;

import it.uniroma2.imagetranscoder.WebAppContextListener;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.yammer.metrics.core.Gauge;

public class LoadAverageMonitorThread extends Thread {

	private Sigar sigar;
	private long sleep;

	public LoadAverageMonitorThread(Sigar sigar, long sleep) {
		this.sigar = sigar;
		this.sleep = sleep;
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				WebAppContextListener.mRegistry.newGauge(LoadAverageMonitorThread.class, "loadAverage", new Gauge<Double>() {
				    @Override
				    public Double value() {
				        double load = 0D;
				    	try {
				    		//load average in past 1-5-15 minutes
							load = sigar.getLoadAverage()[0];
						} catch (SigarException e) {
							e.printStackTrace();
						}
				    	return load;
				    }
				    
				});
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
