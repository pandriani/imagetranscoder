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

public class UploadServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {

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
						System.out.println(filterToApply);
					}
				} else {
					// Process form file field (input type="file").
					// String fieldname = item.getFieldName();
					// String filename = FilenameUtils.getName(item.getName());
					InputStream filecontent = item.getInputStream();
					
					
					ImageResponse imageResponse	= imageTranscoder.apply(filterToApply, filecontent);

					response.setContentType(imageResponse.getContentType());
					// Save it if needed
					// String pathToWeb =
					// getServletContext().getRealPath(File.separator);
					// File f = new File(pathToWeb + "grey.jpg");
					// save the newly created image in a new file.
					// ImageIO.write(cat, "jpg", f);
					// BufferedImage bi = ImageIO.read(f);
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
