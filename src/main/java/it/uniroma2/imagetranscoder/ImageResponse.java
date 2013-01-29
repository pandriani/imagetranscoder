package it.uniroma2.imagetranscoder;

import java.awt.image.BufferedImage;

public class ImageResponse{
	private BufferedImage bufferedImage;
	private String contentType;
	private String ext;
	
	public ImageResponse(BufferedImage bufferedImage, String contentType,
			String ext) {
		super();
		this.bufferedImage = bufferedImage;
		this.contentType = contentType;
		this.ext = ext;
	}
	
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public String getContentType() {
		return contentType;
	}

	public String getExt() {
		return ext;
	}
	
}
