package org.agnitas.cms.utils.preview;

import javax.imageio.*;
import javax.servlet.http.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.concurrent.*;
import java.util.Map;
import java.util.Vector;

import org.agnitas.cms.utils.*;
import org.agnitas.cms.utils.dataaccess.*;
import org.agnitas.cms.web.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mailing;
import org.apache.log4j.Logger;
import org.springframework.context.*;


/**
 * This class generate image from browser`s page.
 * Stores it image in database.
 */
public class PreviewImageGenerator {
	
	private static final transient Logger logger = Logger.getLogger( PreviewImageGenerator.class);

	JEditorPane editor;
	private boolean imagesLoaded = false;
	private boolean pageLoaded = false;
	private boolean rendered = false;
	private int initialWidth = 800;
	private int initialHeight = 600;
	private ApplicationContext aContext;
	private final HttpSession session;
	private int cmTemplateId;
	private int cmId;
	private int cmtId;
	private ThreadPoolExecutor threadPool;
	private int previewMaxWidth;
	private int previewMaxHeight;
    private static final int IMAGE_LOADING_TIMEOUT = 15;

    public PreviewImageGenerator(ApplicationContext applicationContext,
								 HttpSession session, final int previewMaxWidth,
								 final int previewMaxHeight) {

    	if( session == null) {
    		logger.error( "no session found for preview generation");
    		throw new NullPointerException( "SESSION is null");
    	}
    	
		aContext = applicationContext;
		this.session = session;
		final int nThreads = 0;
		final long keepAliveTime = 0;//seconds
        final int maxPoolThreads = 1;
		final LinkedBlockingQueue<Runnable> runnables =
				new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(nThreads, maxPoolThreads, keepAliveTime,
				TimeUnit.SECONDS, runnables);
		this.previewMaxWidth = previewMaxWidth;
		this.previewMaxHeight = previewMaxHeight;
	}

	/**
	 * Generate preview image for one of cms`s element id,
	 * element for wich generates preview must be non zero value and
	 * other two must be equals to zero.
	 *
	 * @param cmTemplateId id of cms`s template
	 * @param cmId		 id of content module
	 * @param cmtId		id of content module type
	 */
	public void generatePreview(int cmTemplateId, int cmId, int cmtId) {
		this.cmTemplateId = cmTemplateId;
		this.cmId = cmId;
		this.cmtId = cmtId;

		String previewUrl = generatePreviewUrl(cmTemplateId, cmId, cmtId);
		if(previewUrl == null) {
			return;
		}
		String systemUrl = AgnUtils.getEMMProperty("system.url");
		final String finalPreviewUrl = systemUrl + previewUrl;

		if( logger.isInfoEnabled())
			logger.info("HTML-preview URL is " + finalPreviewUrl);

		threadPool.execute(new Thread() {
			@Override
			public void run() {
				generatePreview(finalPreviewUrl, false);
			}
		});
	}

    public void generatePreview(final Integer mailingId, final Integer companyId, boolean isUseThread) {
        generatePreview(mailingId, companyId, isUseThread, false);
    }

    public void generatePreview(final Integer mailingId, final Integer companyId, boolean isUseThread, final boolean bulkGenerate) {
        pageLoaded = false;
        imagesLoaded = false;

        if (isUseThread) {
            final Thread command = new Thread() {
                @Override
                public void run() {
                	
                	try {
                		storeMailingPreview(mailingId, companyId, bulkGenerate);
                	} catch( NullPointerException e) {
                		logger.error( "Error generating preview", e);
                	}
                }
            };
            threadPool.execute(command);
        } else {
        	try {
        		storeMailingPreview(mailingId, companyId, bulkGenerate);
        	} catch( NullPointerException e) {
        		logger.error( "Error generating preview", e);
        	}
        }


    }

    private void storeMailingPreview(Integer mailingId, Integer companyId, boolean bulkGenerate) {
        RecipientDao recipientDao = (RecipientDao) aContext.getBean("RecipientDao");
        MailingDao mDao = (MailingDao) aContext.getBean("MailingDao");
        MailingComponentDao componentDao = (MailingComponentDao) aContext.getBean("MailingComponentDao");
        Mailing aMailing = (Mailing) mDao.getMailing(mailingId, companyId);
        final Map<Integer, String> testAdnAdminRecipients = recipientDao.getAdminAndTestRecipientsDescription(companyId, mailingId);
        if (!testAdnAdminRecipients.isEmpty()) {

            String previewUrl = "/mailingsend.do;jsessionid=" + session.getId() +
                    "?action=16&mailingID=" + mailingId + "&previewFormat=1&previewCustomerID="
                    + (Integer) testAdnAdminRecipients.keySet().toArray()[0] + "&previewDay=0&previewMonth=0&previewYear=0";

            String systemUrl = AgnUtils.getEMMProperty("system.url");
            final Vector<MailingComponent> mailingComponents = componentDao.getMailingComponents(mailingId, companyId, MailingComponent.TYPE_THUMBMAIL_IMAGE);
            if (bulkGenerate){
                previewUrl = previewUrl + "&previewCompanyId=" + companyId;
                session.setAttribute("bulkGenerate", "true");
            }
            else {
                session.setAttribute("bulkGenerate", null);
            }
            final String finalPreviewUrl = systemUrl + previewUrl;
            if (!mailingComponents.isEmpty()) {
                MailingComponent component = mailingComponents.get(0);
                component.setBinaryBlock(generatePreview(finalPreviewUrl, true));
                componentDao.saveMailingComponent(component);
            } else {

                MailingComponent component = (MailingComponent) aContext.getBean("MailingComponent");
                component.setCompanyID(companyId);
                component.setMailingID(mailingId);
                component.setType(MailingComponent.TYPE_THUMBMAIL_IMAGE);
                component.setDescription("Mailing preview Image");
                component.setComponentName("THUMBMAIL.png");
                component.setBinaryBlock(generatePreview(finalPreviewUrl, true));
                component.setMimeType("image/png");
                component.setEmmBlock(component.makeEMMBlock());
                aMailing.addComponent(component);
                mDao.saveMailing(aMailing);
            }
        }
    }

    byte[] generatePreview(String url,boolean isMailingPreview) {
    	if( logger.isInfoEnabled())
    		logger.info("Trying to set headless mode");
    	
		System.setProperty("java.awt.headless", "true");
		
		if( logger.isInfoEnabled())
			logger.info("Creating swing html editor");
		editor = new JEditorPane();
		CmsEditorKit editorKit = new CmsEditorKit() {
			@Override
			public void onImagesLoaded() {
				imagesLoaded = true;
				
				if( logger.isInfoEnabled())
					logger.info("preview`s image load finished");
			}
		};
		editor.setEditorKit(editorKit);
		editor.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if("page".equals(event.getPropertyName())) {
					onPageLoaded();
				}
			}
		});

		try {
            URL page = new URL(url);

           if( "https".equalsIgnoreCase( page.getProtocol())) {
                URL realUrl = new URL(null, url, new TrustedHttpsHandler());
                editor.setPage(realUrl);
            } else {
                editor.setPage(url);
            }
		} catch(IOException e) {
			logger.error("URL for preview generation is not valid: " + url, e);
		}

        int secondCounter = 0;
        while (!(pageLoaded && imagesLoaded)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
               logger.error("preview generation failed!!", e);
            }
            secondCounter++;
            if (secondCounter > IMAGE_LOADING_TIMEOUT) break;
        }

          return renderPreview(isMailingPreview);
	}

	private void onPageLoaded() {
		editor.setDoubleBuffered(false);
		editor.setSize(initialWidth, initialHeight);
		editor.addNotify();
		editor.validate();
		BufferedImage dummyImage =
				new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
		Graphics2D imageGraphics = dummyImage.createGraphics();
		editor.paint(imageGraphics);
		pageLoaded = true;
		if(!imagesLoaded) {
			imagesLoaded = ((CmsEditorKit) editor.getEditorKit()).getImageCount() == 0;
		}
		
		if( logger.isInfoEnabled())
			logger.info("page for generation preview was loaded");
	}

	private byte[] renderPreview(boolean isMailingPreview) {
		if(imagesLoaded && pageLoaded && !rendered) {
			if( logger.isInfoEnabled())
				logger.info("preview`s image rendering started...");

			// determine rendering page size
			Dimension preferredSize = editor.getPreferredSize();
			Dimension minimumSize = editor.getMinimumSize();
			Dimension currentSize = editor.getSize();
			if(minimumSize.width > currentSize.width ||
					minimumSize.height > currentSize.height) {
				currentSize.width = minimumSize.width;
				currentSize.height = minimumSize.height;
			} else if(preferredSize.width < currentSize.width) {
				currentSize.width = preferredSize.width;
				currentSize.height = preferredSize.height;
			} else if(preferredSize.height < currentSize.height) {
				currentSize.height = preferredSize.height;
			}

			// relayout page as we have now the new size and all images loaded
			editor.setSize(currentSize.width, currentSize.height);
			editor.addNotify();
			editor.validate();

			// paint page on image
			BufferedImage originalImage = new BufferedImage(currentSize.width,
					currentSize.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D imageGraphics = originalImage.createGraphics();
			editor.paint(imageGraphics);

			// scale image
			Dimension previewSize = getPreviewSize(currentSize.width, currentSize.height, isMailingPreview);
			BufferedImage resultImage;
			if(previewSize.width > currentSize.width) {
				resultImage = originalImage;
			} else {
				BufferedImage croppedImage = cropImage(originalImage, currentSize.width, currentSize.height, isMailingPreview);
				Image scaledImage = croppedImage.getScaledInstance(previewSize.width, previewSize.height, Image.SCALE_SMOOTH);
				resultImage = new BufferedImage(previewSize.width, previewSize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = resultImage.createGraphics();
				graphics.drawImage(scaledImage, 0, 0, null);
				graphics.dispose();
			}

			try {
				ByteArrayOutputStream byteArrayOutputStream =
						new ByteArrayOutputStream();
				ImageIO.write(resultImage, "png", byteArrayOutputStream);

                byte[] imageData = byteArrayOutputStream.toByteArray();
                if (isMailingPreview) {
                    return imageData;
                } else {
                    storePreview(imageData);
                    return null;
                }
			} catch(IOException e) {
				logger.error("Error occurred while saving preview-image", e);
			}
			
			if( logger.isInfoEnabled())
				logger.info("preview`s image rendering finished");
			rendered = true;
		}
        return null;
	}

	private BufferedImage cropImage(BufferedImage originalImage, int width, int height, boolean mailingPreview) {
		if (!mailingPreview) return originalImage;
		double scaleX = ((double) previewMaxWidth) / ((double) width);
		double scaleY = ((double) previewMaxHeight) / ((double) height);
		int cropX = width - 1;
		int cropY = height - 1;
		if (scaleY > scaleX) {
			cropX = (int) (width * (scaleX / scaleY));
		}
		else {
			cropY = (int) (height * (scaleY / scaleX));
		}
		return originalImage.getSubimage(0, 0, cropX, cropY);
	}

	private void storePreview(byte[] imageData) {
		MediaFileManager mediaFileManager =
				CmsUtils.getMediaFileManager(aContext);
		MediaFile mediaFile =
				new MediaFile(cmTemplateId, 1, imageData, cmId, cmtId, 0,
						MediaFileUtils.PREVIEW_TYPE, "image/png", "preview");
		if(cmId != 0) {
			mediaFileManager.removePreviewOfContentModule(cmId);
		} else if(cmtId != 0) {
			mediaFileManager.removePreviewOfContentModuleType(cmtId);
		} else if(cmTemplateId != 0) {
			mediaFileManager.removePreviewOfContentModuleTemplate(cmTemplateId);
		}
		mediaFileManager.createMediaFile(mediaFile);
	}

	private Dimension getPreviewSize(int originalWidth, int originalHeight, boolean isMailingPreview) {
        if (isMailingPreview) return new Dimension(previewMaxWidth, previewMaxHeight);
		double scaleX = ((double) previewMaxWidth) / ((double) originalWidth);
		double scaleY = ((double) previewMaxHeight) / ((double) originalHeight);
		double scale = Math.min(scaleX, scaleY);
		return new Dimension((int) (scale * originalWidth), (int) (scale * originalHeight));
	}

	private String generatePreviewUrl(int cmTemplateId, int cmId, int cmtId) {
		String sessionId = session.getId();
		if(cmId > 0) {
			return "/cms_contentmodule.do;jsessionid=" + sessionId +
					"?action=" +
					ContentModuleAction.ACTION_PURE_PREVIEW +
					"&contentModuleId=" + cmId;
		} else if(cmTemplateId > 0) {
			return "/cms_cmtemplate.do;jsessionid=" + sessionId + "?action=" +
					CMTemplateAction.ACTION_PURE_PREVIEW + "&cmTemplateId=" +
					cmTemplateId;
		} else if(cmtId > 0) {
			return "/cms_cmt.do;jsessionid=" + sessionId + "?action=" +
					ContentModuleTypeAction.ACTION_PURE_PREVIEW + "&cmtId=" +
					cmtId;
		} else {
			return null;
		}
	}

}
