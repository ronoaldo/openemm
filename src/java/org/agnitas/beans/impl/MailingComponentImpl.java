/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans.impl;

/**
 * 
 * @author mhe
 * @version
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.Date;

import javax.mail.internet.MimeUtility;

import org.agnitas.beans.MailingComponent;
import org.agnitas.util.AgnUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

public class MailingComponentImpl implements MailingComponent {
	private static final long serialVersionUID = 6149094755767488656L;

	private static final transient Logger logger = Logger.getLogger( MailingComponentImpl.class);

	protected int id;

	protected String mimeType;

	protected String componentName;

	protected String description;

	protected int mailingID;

	protected int companyID;

	protected int type;

	protected StringBuffer emmBlock;

	protected byte[] binaryBlock;

	protected Date timestamp;
	
	protected String link;
	
	protected int urlID;

	protected int targetID;

    protected Date startDate;

    protected Date endDate;

	public static final int TYPE_FONT = 6;

	public MailingComponentImpl() {
		id = 0;
		componentName = null;
		mimeType = " ";
		mailingID = 0;
		companyID = 0;
		type = TYPE_IMAGE;
		emmBlock = null;
		targetID = 0;
	}

	public void setComponentName(String cid) {
		if (cid != null) {
			componentName = cid;
		} else {
			componentName = "";
		}
	}

	public void setType(int cid) {
		type = cid;
		if ((type != TYPE_IMAGE) && (type != TYPE_TEMPLATE)
				&& (type != TYPE_ATTACHMENT)
				&& (type != TYPE_PERSONALIZED_ATTACHMENT)
				&& (type != TYPE_HOSTED_IMAGE) && (type != TYPE_FONT)
                && (type != TYPE_THUMBMAIL_IMAGE)
				&& (type != TYPE_PREC_ATTACHMENT)) {
			type = TYPE_IMAGE;
		}
	}

	public void setMimeType(String cid) {
		if (cid != null) {
			mimeType = cid;
		} else {
			mimeType = "";
		}
	}

	public void setId(int cid) {
		id = cid;
	}

	public String getComponentName() {
		if (componentName != null) {
			return componentName;
		}

		return "";
	}

	public void setMailingID(int cid) {
		mailingID = cid;
	}

	public void setCompanyID(int cid) {
		companyID = cid;
	}

	public void setEmmBlock(String cid) {
		if (cid != null) {
			emmBlock = new StringBuffer(cid);
		} else {
			emmBlock = new StringBuffer();
		}
	}

	public void setBinaryBlock(byte[] cid) {
		binaryBlock = cid;
	}

	public boolean loadContentFromURL() {
		boolean returnValue = true;

		// return false;

		if ((type != TYPE_IMAGE) && (type != TYPE_ATTACHMENT)) {
			return false;
		}
		
		HttpClient httpClient = new HttpClient();
		String encodedURI = encodeURI(componentName);
		GetMethod get = new GetMethod(encodedURI);		
		get.setFollowRedirects(true);
		
		try {			
			httpClient.getParams().setParameter("http.connection.timeout", 5000);

			if (httpClient.executeMethod(get) == 200) {	
				get.getResponseHeaders();
				this.binaryBlock = get.getResponseBody();
				setEmmBlock(makeEMMBlock());
				mimeType = get.getResponseHeader("Content-Type").getValue();
			}
		} catch (Exception e) {
			logger.error("loadContentFromURL", e);
			returnValue = false;
		}
		// lets clear connection 
		finally {
			get.releaseConnection();
		}
		
		if( logger.isInfoEnabled())
			logger.info("loadContentFromURL: loaded " + componentName);
		
		return returnValue;
	}

	public String makeEMMBlock() {
		ByteArrayOutputStream baos = null;
		if (type == TYPE_TEMPLATE) {
			try {
				return new String(binaryBlock, "UTF8");
			} catch (Exception e) {
				logger.error("makeEMMBlock: encoding error", e);
				return " ";
			}
		} else {
			try {
				baos = new ByteArrayOutputStream();
				OutputStream dos = MimeUtility.encode(
						new DataOutputStream(baos), "base64");
				dos.write(binaryBlock);
				dos.flush();
				return baos.toString();
			} catch (Exception e) {
				return null;
			}
		}
	}

	public String getEmmBlock() {
		return new String(emmBlock);
	}

	public int getId() {
		return id;
	}

	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Getter for property targetID.
	 * 
	 * @return Value of property targetID.
	 */
	public int getTargetID() {
		return this.targetID;
	}

	/**
	 * Setter for property targetID.
	 * 
	 * @param targetID
	 *            New value of property targetID.
	 */
	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

	/**
	 * Getter for property type.
	 * 
	 * @return Value of property type.
	 */
	public int getType() {
		return this.type;
	}

	/** Don't invoke this. Used by Hibernate only. */
	public void setBinaryBlob(Blob imageBlob) {
		this.binaryBlock = AgnUtils.BlobToByte(imageBlob);
	}

	/** Don't invoke this. Used by Hibernate only. */
	public Blob getBinaryBlob() {
		return Hibernate.createBlob(this.binaryBlock);
	}

	/**
	 * Getter for property binaryBlock.
	 * 
	 * @return Value of property binaryBlock.
	 * 
	 */
	public byte[] getBinaryBlock() {
		return this.binaryBlock;
	}

	/**
	 * Getter for property mailingID.
	 * 
	 * @return Value of property mailingID.
	 */
	public int getMailingID() {
		return this.mailingID;
	}

	/**
	 * Getter for property companyID.
	 * 
	 * @return Value of property companyID.
	 */
	public int getCompanyID() {
		return this.companyID;
	}

	/**
	 * Getter for property timestamp.
	 * 
	 * @return Value of property timestamp.
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getUrlID() {
		return urlID;
	}

	public void setUrlID(int urlID) {
		this.urlID = urlID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
	 * This method encodes some parts of a URI. If in the given URI a "[", "]", "{" or "}" are found, they
	 * will be replaced by appropriate HEX-Identifiers.
	 * See here for more information:
	 * http://www.ietf.org/rfc/rfc3986.txt
	 * http://stackoverflow.com/questions/40568/square-brackets-in-urls
	 * http://www.blooberry.com/indexdot/html/topics/urlencoding.htm  
	 * 
	 * @return "cleaned" URI
	 */
	private String encodeURI(String uri) {		
		// TODO Replace this version with a more generic approach. Now only one special
		// case is fixed. This method should convert ALL special characters.
		
		/*
		 * Note: Using a generic method to URL-encode a String may lead to another problem:
		 * The URLs found in the mailing may already be URL encoded (irrespective to some
		 * of the dirty things, that got common practice, like "{" or "}" in URLs), so we
		 * URL-encode an URL-encoded URI.
		 * 
		 * This method should simply "clean" the given URL/URI and do no further encoding.
		 * TODO: In this case, renaming of the method would be a great deal!
		 */
		
		uri = uri.replace("[", "%5B");
		uri = uri.replace("]", "%5D");
		uri = uri.replace("{", "%7B");
		uri = uri.replace("}", "%7D");
		
		return uri;
	}
}
