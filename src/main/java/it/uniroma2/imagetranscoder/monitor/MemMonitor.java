package it.uniroma2.imagetranscoder.monitor;

import it.uniroma2.imagetranscoder.WebAppContextListener;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.Gauge;

public class MemMonitor{

	private Sigar sigar;
	
	public MemMonitor(Sigar sigar) {
		this.sigar = sigar;
		initialize();
	}

	private void initialize(){
		WebAppContextListener.mRegistry.register(MetricRegistry.name("monitor.memory.memory-used-percent"), new Gauge<Double>() {
		    public Double getValue() {
		        double freePercent = 0D;
		    	try {
					freePercent = sigar.getMem().getUsedPercent();
				} catch (SigarException e) {
					e.printStackTrace();
				}
		    	return freePercent;
		    }
		    
		});
	}
	
}
