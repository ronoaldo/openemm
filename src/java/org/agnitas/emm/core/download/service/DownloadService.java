package org.agnitas.emm.core.download.service;

import javax.activation.MimeType;
import javax.servlet.http.HttpSession;

import org.agnitas.emm.core.download.model.FileData;

/**
 * Service dealing with download data. 
 * 
 * @author md
 *
 */
public interface DownloadService {

	/**
	 * Register new download informations in the given session. The ID of the download data is returned, so
	 * it can be used for download links.
	 * 
	 * @param temporaryFileName name of the temporary file on the server
	 * @param mimeType MIME type of content
	 * @param downloadName name of file used by the browser
	 * @param session HTTP session containing the download information
	 * 
	 * @return ID of download information
	 */
	public String registerDownloadData(String temporaryFileName, MimeType mimeType, String downloadName, HttpSession session);

	/**
	 * Removes a single download information from session. Attempty to delete the temporary file of the server.
	 * If the ID is unknown, nothing is done.
	 * If the last download information is removed, the entire session attribute is removed.
	 * 
	 * @param id ID of download information to remove.
	 * @param session HTTP session containing the download information.
	 */
	public void removeDownloadData(String id, HttpSession session);

	/**
	 * Removes all download informations, attempts to delete the temporary files and removes the session attribute.
	 * 
	 * @param session HTTP session containing the download informations
	 */
	public void removeAllDownloadData(HttpSession session);
	
	/**
	 * Returns the download informations for the given download ID. If the ID is unknown, a DownloadIdNotFoundException
	 * is thrown.
	 * 
	 * @param id ID of download
	 * @param session HTTP session
	 * 
	 * @return Download information for given ID
	 * 
	 * @throws DownloadIdNotFoundException if ID is unknown
	 */
	public FileData getDownloadData( String id, HttpSession session) throws DownloadIdNotFoundException;

}