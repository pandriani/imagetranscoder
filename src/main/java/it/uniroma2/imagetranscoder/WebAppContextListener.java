package it.uniroma2.imagetranscoder;

import it.uniroma2.imagetranscoder.monitor.CPUMonitorThread;
import it.uniroma2.imagetranscoder.monitor.LoadAverageMonitorThread;
import it.uniroma2.imagetranscoder.monitor.MemMonitorThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hyperic.sigar.Sigar;

import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.GraphiteReporter;

public class WebAppContextListener implements ServletContextListener {

	public static final MetricsRegistry mRegistry = new MetricsRegistry();

	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("contextInitialized");
		String cloudProvider = System.getenv("CLOUD_PROVIDER");
		System.out.println("CLOUD_PROVIDER="+cloudProvider);
//		System.out.println(System.getProperty("java.library.path"));
		try {
			Sigar sigar = new Sigar();
			long sleep = 1000;
			MemMonitorThread memMonitorThread = new MemMonitorThread(sigar, sleep);
			memMonitorThread.start();
			CPUMonitorThread cpuMon = new CPUMonitorThread(sigar, sleep);
			cpuMon.start();
			LoadAverageMonitorThread load = new LoadAverageMonitorThread(sigar, sleep);
			load.start();
			
			GraphiteReporter.enable(mRegistry, 1, TimeUnit.MINUTES,
					"ec2-23-22-114-15.compute-1.amazonaws.com", 2023, InetAddress.getLocalHost().getHostName()+"."+cloudProvider);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("contextDestroyed");
	}

}
