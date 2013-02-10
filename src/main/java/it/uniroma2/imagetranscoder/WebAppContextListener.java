package it.uniroma2.imagetranscoder;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.GraphiteReporter;


public class WebAppContextListener implements ServletContextListener {

	public static final MetricsRegistry mRegistry = new MetricsRegistry();
	
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("contextInitialized");
		try {
			GraphiteReporter.enable(mRegistry, 1, TimeUnit.SECONDS, "ec2-23-20-108-80.compute-1.amazonaws.com", 2003, InetAddress.getLocalHost().getHostName());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//GraphiteReporter.enable(1, TimeUnit.SECONDS, "ec2-23-20-108-80.compute-1.amazonaws.com", 2003);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("contextDestroyed");
	}

}
