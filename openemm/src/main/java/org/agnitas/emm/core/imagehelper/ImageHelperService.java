package org.agnitas.emm.core.imagehelper;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.agnitas.emm.core.commons.util.ConfigService;
import org.apache.log4j.Logger;

public class ImageHelperService {
	private static final transient Logger logger = Logger.getLogger(ImageHelperService.class);
	
	public static int MAXSCALE=100;	// max scale value.
	
	private ConfigService configService = null;
	
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection
	
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic
	
	/**
	 * This method creates a thumbnail from the given byte-array.
	 * The values for x and y are fetched from DB. Also the quality of 
	 * the thumbnail is fetched from DB and the type of the target image.
	 * @return
	 */
	byte[] createThumbnail(byte[] sourceImage) {
		ByteArrayOutputStream out = null;
		float quality = 0.4f;

//		BufferedImage buff = null;
		try {
			BufferedImage buff = ImageIO.read(new ByteArrayInputStream(sourceImage));

			ImageWriter writer = ImageIO.getImageWritersByFormatName( "jpg" ).next();
			ImageOutputStream ios;
		
			ios = ImageIO.createImageOutputStream( out );		
			writer.setOutput( ios );
			ImageWriteParam iwparam = new JPEGImageWriteParam( Locale.getDefault() );
			iwparam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT ) ;	
			iwparam.setCompressionQuality( quality );	   
			writer.write( null, new IIOImage(buff, null, null), iwparam );
			ios.flush();
			writer.dispose();
			ios.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * This method takes a buffered image and scales it with the given scaleX and scaleY values.
	 * If only one scale value is given, it will be used for both dimensions.
	 * Take care that the given scale values are valid. 
	 * @param originalImage
	 * @param scaleX
	 * @param scaleY
	 * @return
	 * @throws Exception 
	 */
	public BufferedImage scaleImage(BufferedImage originalImage, float scaleX, float scaleY) throws Exception {
		logger.info("ImageHelperService: called scaleImage");
		// basic checking if scaleX or scaleY are valid.
		if (scaleX == 0 && scaleY == 0) {
			throw new Exception("Invalid scale values given: X: " + scaleX + " Y: " + scaleY); 
		}
		if (scaleX >= MAXSCALE || scaleY >= MAXSCALE ) {
			throw new Exception("Scale value too high. Maxscale is: " + MAXSCALE + " ,X: " + scaleX + " Y: " + scaleY);
		}
		if (scaleX == 0) {
			scaleX = scaleY;
		}
		if (scaleY == 0) {
			scaleY = scaleX;
		}
		
		// calculate new size
		int width = (int)(originalImage.getWidth() * scaleX);
		int height =(int)(originalImage.getHeight() * scaleY);
		// generate new image
		BufferedImage tmpImg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		// get context
		Graphics2D g2 = (Graphics2D) tmpImg.getGraphics();
		// scale
		g2.scale(scaleX, scaleY);
		// paint
		g2.drawImage(originalImage, 0, 0, null);
		// the scaled image is now drawn on the context of tmpImg.
		g2.dispose();
		return tmpImg;
	}

	/**
	 * This method returns a BufferedImage with the scaled image.  
	 * @param originalImage
	 * @param scaleX
	 * @param scaleY
	 * @return
	 */
	public BufferedImage createThumbnail(BufferedImage originalImage, float scaleX, float scaleY) {
		BufferedImage returnImage = null;
		try {
			returnImage = scaleImage(originalImage, scaleX, scaleY);
		} catch (Exception e) {
			logger.error("An error occured during creation of thumbnails." + e);
		}		
		return returnImage;
	}
	
	/**
	 * This method gets the scale values from the DB and returns a scaled image with this values.
	 * @param originalImage
	 * @return
	 */
	public BufferedImage createThumbnail(BufferedImage originalImage) {
		float[] scales = getScaleFactor(originalImage);// scales[0] = X, scales[1] = Y.
		BufferedImage returnImage = createThumbnail(originalImage, scales[0], scales[1]);
		return returnImage;
	}
	
	public byte[] createPNGThumbnailFromByteArray(byte[] img) {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(img);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			BufferedImage buffImg = ImageIO.read(byteIn);
			BufferedImage thumbImg = createThumbnail(buffImg);
			ImageOutputStream ios;
			ios = ImageIO.createImageOutputStream( byteOut );
			ImageWriter writer = ImageIO.getImageWritersByFormatName( "png" ).next();
			writer.setOutput(ios);				
			writer.write( null, new IIOImage(thumbImg, null, null), writer.getDefaultWriteParam() );
			ios.flush();
			writer.dispose();
			ios.close();
		} catch (IOException e) {
			logger.error("Error reading image from byte array");
			e.printStackTrace();
		}
		return byteOut.toByteArray();
	}
	
	/**
	 * This method returns the x,y scale factor calculated from the size given with the
	 * bufferedImage and the values in the DB.
	 * Example:
	 * DB-Values for sizeX = 500, sizeY=500;
	 * BufferedImage has sizeX = 1000,sizeY = 1000
	 * Scale factor is {0.5, 0.5} (500 / 1000)
	 * Hint: If scale for one value is not set, the appropriate scale size is calculated to 
	 * maintain aspect ratio.
	 * @param buffImg
	 * @return
	 */
	private float[] getScaleFactor(BufferedImage buffImg) {
		float[] returnValues = new float[2];
		int sizex = configService.getIntegerValue(ConfigService.Value.Thumbnail_Sizex);
		int sizey = configService.getIntegerValue(ConfigService.Value.Thumbnail_Sizey);
		if (buffImg.getWidth() == 0 && buffImg.getHeight() == 0) {
			// setting default values and writing warning
			returnValues[0] = 119;
			returnValues[1] = 84;
			logger.error("ImageHelperService: getScaleFactor: getting XYSize for thumbnails from DB failed!" +
					" Please check, if values are given in config_tbl. Defaults are set the hard way.");
		} else {
			returnValues[0] = (float)sizex / (float)buffImg.getWidth();
			returnValues[1] = (float)sizey / (float)buffImg.getHeight();
			if (returnValues[0] == 0) {
				returnValues[0] = returnValues[1];
			}
			if (returnValues[1] == 0) {
				returnValues[1] = returnValues[0];
			}
		}
		return returnValues;
	}
	
	/**
	 * This method returns a thumbnailed version of the given PNG image if the scale factor
	 * would be more than the DB parameter.
	 * The return value is the same image if we dont need scaling! 
	 * @param img
	 * @return
	 */
	public byte[] createThumbIfNeeded(byte[] img) {
		logger.info("ImageHelperService: called createThumbIfNeeded");
		byte[] returnImage = img;
		float scaleLimit = configService.getFloatValue(ConfigService.Value.Thumbnail_Treshold);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(img);
		BufferedImage buffImg = null;
		try {
			 buffImg = ImageIO.read(byteIn);
		} catch (IOException e) {		
			logger.error("Error creating image from given buffer");
		}
		float scales[] = getScaleFactor(buffImg);
		if (scales[0] > scaleLimit || scales[1] > scaleLimit) {
			logger.info("ImageHelperService::createThumbIfNeeded: createPNGThumbnailFromByteArray called");
			returnImage = createPNGThumbnailFromByteArray(img);			
		} else {
			logger.info("ImageHelperService::createThumbIfNeeded: Scale factor prevents calculating thumbnail");
		}
		return returnImage;
	}
	
	public byte[] createDummyIfNeeded( byte[] img) {
		if( img == null) {
			try {
				BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_3BYTE_BGR);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( image, "png", baos);
				img = baos.toByteArray();
			} catch( IOException e) {
				Logger.getLogger( getClass()).error( "Unable to create dummy image", e);
			}
		}
		
		return img;
	}
}
