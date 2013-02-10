package it.uniroma2.imagetranscoder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Histogram;

public class UploadServlet extends HttpServlet {
	
	private final Counter imageCounter = WebAppContextListener.mRegistry.newCounter(UploadServlet.class, "processed-images");
	private final Histogram requestImageSize = WebAppContextListener.mRegistry.newHistogram(UploadServlet.class, "image-input-size");
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			imageCounter.inc();
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
					requestImageSize.update(sizeInKb);
					
					
					ImageResponse imageResponse	= imageTranscoder.apply(filterToApply, filecontent);
					response.setContentType(imageResponse.getContentType());
					OutputStream out = response.getOutputStream();
					
					ImageIO.write(imageResponse.getBufferedImage(), imageResponse.getExt(), out);
					out.close();
					
				}

			}

		} catch (FileUploadException e) {
			throw new ServletException("Cannot parse multipart request.", e);
		}

		// ...
	}



}
