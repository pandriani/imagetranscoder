package it.uniroma2.imagetranscoder.monitor;

import it.uniroma2.imagetranscoder.WebAppContextListener;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.Gauge;

public class LoadAverageMonitor {

	private Sigar sigar;

	public LoadAverageMonitor(Sigar sigar) {
		this.sigar = sigar;
		initialize();
	}

	private void initialize(){
		WebAppContextListener.mRegistry.register(MetricRegistry.name("monitor.load.loadAverage"), new Gauge<Double>() {
		    
		    public Double getValue() {
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
	}
}
