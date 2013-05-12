package it.uniroma2.imagetranscoder;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.yammer.metrics.Counter;
import com.yammer.metrics.Meter;
import com.yammer.metrics.MetricRegistry;

public class RequestRateFilter implements Filter {

	private Meter requestRate;
	private Counter requestCounter;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		requestRate = WebAppContextListener.mRegistry.meter("requestRate");
		requestCounter = WebAppContextListener.mRegistry.counter(MetricRegistry.name("requestCounter"));
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		requestCounter.inc();
		requestRate.mark();
		chain.doFilter(request, response);
	}

	public void destroy() {
		
	}

}
