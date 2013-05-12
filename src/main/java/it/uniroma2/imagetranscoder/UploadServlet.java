package it.uniroma2.imagetranscoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.yammer.metrics.Counter;
import com.yammer.metrics.Histogram;
import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.Meter;
import com.yammer.metrics.Timer;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
	
	private final Counter requestCounter = WebAppContextListener.mRegistry.counter(MetricRegistry.name("requestCounter-inside"));
//	private final Histogram requestImageSize = WebAppContextListener.mRegistry.histogram("requestSize");
	private final Meter requestRate = WebAppContextListener.mRegistry.meter("requestRate-inside");
	private final Meter serviceRate = WebAppContextListener.mRegistry.meter("serviceRate");
	private final Timer processingDuration = WebAppContextListener.mRegistry.timer("processing-duration");
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			requestCounter.inc();
			requestRate.mark();
			
			@SuppressWarnings("unchecked")
			List<FileItem> items = new ServletFileUpload(
					new DiskFileItemFactory()).parseRequest(request);
			ImageTranscoder imageTranscoder = new ImageTranscoder();
			int filterToApply = -1;
			for (FileItem item : items) {
				if (item.isFormField()) {
					// Process regular form field (input
					// type="text|radio|checkbox|etc", select, etc).
					if (item.getFieldName().equals("filter")) {
						filterToApply = Integer.parseInt(item.getString());
					}
				} else {
					// Process form file field (input type="file").
					// String fieldname = item.getFieldName();
					// String filename = FilenameUtils.getName(item.getName());
					InputStream filecontent = item.getInputStream();
					
					long sizeInKb = item.getSize()/1024;
//					requestImageSize.update(sizeInKb);
					
					final Timer.Context context = processingDuration.time();
					ImageResponse imageResponse	= imageTranscoder.apply(filterToApply, filecontent);
					context.stop();
					response.setContentType(imageResponse.getContentType());
					OutputStream out = response.getOutputStream();
					ImageIO.write(imageResponse.getBufferedImage(), imageResponse.getExt(), out);
					out.close();
					
				}

			}
			serviceRate.mark();
		} catch (FileUploadException e) {
			throw new ServletException("Cannot parse multipart request.", e);
		}

		// ...
	}



}
