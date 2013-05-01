package it.uniroma2.imagetranscoder;

import it.uniroma2.imagetranscoder.monitor.CPUMonitor;
import it.uniroma2.imagetranscoder.monitor.LoadAverageMonitor;
import it.uniroma2.imagetranscoder.monitor.MemMonitor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hyperic.sigar.Sigar;

import com.yammer.metrics.MetricFilter;
import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.graphite.Graphite;
import com.yammer.metrics.graphite.GraphiteReporter;

public class WebAppContextListener implements ServletContextListener {

	public static String metricsRegistryName = "ImageTranscoderApp"
			+ UUID.randomUUID().toString();

	public static final MetricRegistry mRegistry = new MetricRegistry(
			metricsRegistryName);

	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("contextInitialized");
		String cloudProvider = System.getenv("CLOUD_PROVIDER");
		System.out.println("CLOUD_PROVIDER=" + cloudProvider);
		// System.out.println(System.getProperty("java.library.path"));
		try {
			Sigar sigar = new Sigar();

			MemMonitor memMonitor = new MemMonitor(sigar);
			CPUMonitor cpuMon = new CPUMonitor(sigar);
			LoadAverageMonitor load = new LoadAverageMonitor(sigar);

			final Graphite graphite = new Graphite(new InetSocketAddress(
					"ec2-54-224-249-14.compute-1.amazonaws.com", 2023));
			final GraphiteReporter reporter = GraphiteReporter
					.forRegistry(mRegistry)
					.prefixedWith(
							getIP() + "."
									+ cloudProvider + ".imagetranscoder")
					// InetAddress.getLocalHost().getHostName()+"."+cloudProvider
					.convertRatesTo(TimeUnit.SECONDS)
					.convertDurationsTo(TimeUnit.MILLISECONDS)
					.filter(MetricFilter.ALL).build(graphite);

			reporter.start(10, TimeUnit.SECONDS);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("contextDestroyed");
	}

	private String getIP() {

		// Replace eth0 with your interface name
		NetworkInterface i = null;
		try {
			i = NetworkInterface.getByName("eth0");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (i != null) {

			Enumeration<InetAddress> iplist = i.getInetAddresses();

			InetAddress addr = null;

			while (iplist.hasMoreElements()) {
				InetAddress ad = iplist.nextElement();
				byte bs[] = ad.getAddress();
				if (bs.length == 4 && bs[0] != 127) {
					addr = ad;
					// You could also display the host name here, to
					// see the whole list, and remove the break.
					break;
				}
			}
			System.out.println(addr.getCanonicalHostName());
			System.out.println(addr.getHostAddress());
			
			return addr.getHostAddress().replace('.', '-');
		}
		else
			return "unknownHostAddress";
	}

}
