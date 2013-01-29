package it.uniroma2.imagetranscoder;

import java.awt.Color;
import java.awt.CompositeContext;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.jhlabs.composite.ColorDodgeComposite;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.jhlabs.image.ImageUtils;
import com.jhlabs.image.InvertFilter;
import com.jhlabs.image.PointFilter;

public class ImageTranscoder {
	
	public ImageResponse apply(int filterToApply,
			InputStream filecontent) throws IOException {
		ImageResponse imageResponse;
	     switch (filterToApply) {
	         case 1:
	        	 imageResponse = new ImageResponse(greyImage(filecontent), "image/jpeg", "jpg");
	             break;
	         case 2:
	        	 imageResponse = new ImageResponse(sketchImage(filecontent), "image/png", "png");
	             break;
	         default:
	             throw new IllegalArgumentException("Invalid filter: " + filterToApply);
	     }
	     return imageResponse;		
	}
	
	private BufferedImage sketchImage(InputStream filecontent) throws IOException {
		BufferedImage src = null;
		BufferedImage target = null;
		src = ImageIO.read(filecontent);
		src = ImageUtils.convertImageToARGB(src);

		//transformations begin=============
		//gray scale
		PointFilter grayScaleFilter = new GrayscaleFilter();
		BufferedImage grayScale = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
		grayScaleFilter.filter(src, grayScale);

		//inverted gray scale
		BufferedImage inverted = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
		PointFilter invertFilter = new InvertFilter();
		invertFilter.filter(grayScale,inverted);

		//gaussian blurr
		GaussianFilter gaussianFilter = new GaussianFilter(20);
		BufferedImage gaussianFiltered = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
		gaussianFilter.filter(inverted, gaussianFiltered);

		//color dodge
		ColorDodgeComposite cdc = new ColorDodgeComposite(1.0f);
		CompositeContext cc = cdc.createContext(inverted.getColorModel(), grayScale.getColorModel(), null);
		Raster invertedR = gaussianFiltered.getRaster();
		Raster grayScaleR = grayScale.getRaster();
		BufferedImage composite = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
		WritableRaster colorDodgedR = composite.getRaster();
		cc.compose(invertedR, grayScaleR , colorDodgedR);

		//==================================
		target = composite;
//		File outputfile = new File("/home/pandriani/Pictures/brSketch.png");
//		ImageIO.write(target, "png", outputfile);
		return target;
	}

	private BufferedImage greyImage(InputStream input) throws IOException {
		BufferedImage cat = ImageIO.read(input);

		// Loop through all the pixels in the image (w = width, h = height)
		for (int w = 0; w < cat.getWidth(); w++) {
			for (int h = 0; h < cat.getHeight(); h++) {
				// BufferedImage.getRGB() saves the colour of the pixel as a
				// single integer.
				// use Color(int) to grab the RGB values individually.
				Color color = new Color(cat.getRGB(w, h));

				// use the RGB values to get their average.
				int averageColor = ((color.getRed() + color.getGreen() + color
						.getBlue()) / 3);

				// create a new Color object using the average colour as the
				// red, green and blue
				// colour values
				Color avg = new Color(averageColor, averageColor, averageColor);

				// set the pixel at that position to the new Color object using
				// Color.getRGB().
				cat.setRGB(w, h, avg.getRGB());
			}
		}
		return cat;

	}
	

}
