package org.agnitas.emm.core.download.model;

import javax.activation.MimeType;

/**
 * Data structure for download informations.
 * 
 * @author md
 */
public class FileData {
	
	/** Name of temporary file. */
	private final String tempFileName;
	
	/** MIME type of content. */
	private final MimeType mimeType;
	
	/** Name of file used by the client. */
	private final String downloadName;
	
	/**
	 * Creates a new FileData instance. 
	 * 
	 * @param tempFileName name of temporary file
	 * @param mimeType MIME type of content
	 * @param downloadName name of downloaded file
	 */
	public FileData( String tempFileName, MimeType mimeType, String downloadName) {
		this.tempFileName = tempFileName;
		this.mimeType = mimeType;
		this.downloadName = downloadName;
	}
	
	/**
	 * Returns the name of the temporary file.
	 * 
	 * @return name of temporary file
	 */
	public String getTempFileName() {
		return this.tempFileName;
	}
	
	/**
	 * Returns the MIME type of the content.
	 * 
	 * @return MIME type of content
	 */
	public MimeType getMimeType() {
		return this.mimeType;
	}
	
	/**
	 * Returns the filename used by the client.
	 * 
	 * @return filename of downloaded file
	 */
	public String getDownloadName() {
		return this.downloadName;
	}
}
