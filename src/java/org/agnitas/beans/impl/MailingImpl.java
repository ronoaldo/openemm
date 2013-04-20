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

import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.agnitas.backend.Mailgun;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.beans.TagDetails;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.DynamicTagDao;
import org.agnitas.dao.MaildropStatusDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnTagUtils;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import bsh.Interpreter;

/**
 * 
 * @author Martin Helff, Nicole Serek
 */
public class MailingImpl extends MailingBaseImpl implements Mailing {
	private static final transient Logger logger = Logger.getLogger(MailingImpl.class);

	private static final long serialVersionUID = -6126128329645532973L;
	protected int mailTemplateID;
	protected int targetID;
	protected Map<String, DynamicTag> dynTags = new LinkedHashMap<String, DynamicTag>();
	protected Map<String, MailingComponent> components = new LinkedHashMap<String, MailingComponent>();
	protected Hashtable<String, MailingComponent> attachments;
	protected Map<String, TrackableLink> trackableLinks = new LinkedHashMap<String, TrackableLink>();
	protected int searchPos;
	protected int clickActionID;
	protected int openActionID;
	protected Set<MaildropEntry> maildropStatus = new LinkedHashSet<MaildropEntry>();
	protected Map<Integer, Mediatype> mediatypes = new LinkedHashMap<Integer, Mediatype>();
	protected Timestamp creationDate;
	protected Map<Integer, Target> allowedTargets = null;
	protected Collection<Integer> targetGroups;
	protected int maildropID;
	protected int templateOK;
	protected boolean isTemplate;
	protected int targetMode;
	protected String targetExpression;
	protected int deleted;
	protected boolean needsTarget;
	protected boolean locked;
	protected boolean archived;

	/**
	 * mailingType can hold the values 0-3 0: Normal mailing 1: Action-Based 2:
	 * Date-Based 3: Followup Defined in Mailing.java eg. TYPE_NORMAL
	 */
	protected int mailingType;

	@Override
	public boolean parseTargetExpression(String tExp) {
		boolean result = true;
		int posA = 0;
		int posB = 0;
		String targetID = null;
		Integer tmpInt = null;
		char tmpOp = '&';

		this.targetMode = MailingImpl.TARGET_MODE_AND;

		if (tExp == null) {
			return result;
		}

		if (tExp.indexOf('|') != -1) {
			this.targetMode = MailingImpl.TARGET_MODE_OR;
			tmpOp = '|';
		}

		while ((posB = tExp.indexOf(tmpOp, posA)) != -1) {
			targetID = tExp.substring(posA, posB).trim();
			posA = posB + 1;
			try {
				tmpInt = new Integer(Integer.parseInt(targetID));
				if (this.targetGroups == null) {
					this.targetGroups = new HashSet<Integer>();
				}
				this.targetGroups.add(tmpInt);
			} catch (Exception e) {
				logger.error("parseTargetExpression", e);
				tmpInt = null;
			}
		}

		if (posA < tExp.length()) {
			targetID = tExp.substring(posA).trim();
			try {
				tmpInt = new Integer(Integer.parseInt(targetID));
				if (this.targetGroups == null) {
					this.targetGroups = new HashSet<Integer>();
				}
				this.targetGroups.add(tmpInt);
			} catch (Exception e) {
				logger.error("parseTargetExpression", e);
				tmpInt = null;
			}
		}

		return result;
	}

    public void updateTargetExpression() {
        this.targetExpression = this.generateTargetExpression();
    }

	protected String generateTargetExpression() {
		StringBuffer tmp = new StringBuffer("");
		Integer tmpInt = null;
		boolean isFirst = true;
		String opTmp = " & ";

		if (this.targetMode == MailingImpl.TARGET_MODE_OR) {
			opTmp = " | ";
		}

		if (this.targetGroups == null) {
			return "";
		}

		Iterator<Integer> aIt = this.targetGroups.iterator();
		while (aIt.hasNext()) {
			tmpInt = aIt.next();
			if (tmpInt.intValue() != 0) {
				if (!isFirst) {
					tmp.append(opTmp);
				} else {
					isFirst = false;
				}
				tmp.append(tmpInt.toString());
			}
		}

		return tmp.toString();
	}

	@Override
	public void addComponent(MailingComponent aComp) {

		if (components == null)
			components = new HashMap<String, MailingComponent>();

		if (!components.containsKey(aComp.getComponentName())) {
			components.put(aComp.getComponentName(), aComp);
		}
	}

	private void addComponents( Set<MailingComponent> components) {
		for( MailingComponent component : components)
			addComponent( component);
	}
	
	@Override
	public void addAttachment(MailingComponent aComp) {

		if (attachments == null)
			attachments = new Hashtable<String, MailingComponent>();

		attachments.put(aComp.getComponentName(), aComp);
	}

	@Override
	public void setTargetID(int tmpid) {
		targetID = tmpid;
	}

	@Override
	public void setMailTemplateID(int tmpid) {
		mailTemplateID = tmpid;
	}

	@Override
	public Map<String, DynamicTag> getDynTags() {
		return dynTags;
	}

	@Override
	public Vector<String> findDynTagsInTemplates(String aTemplate, ApplicationContext con) throws Exception {
		DynamicTag aktTag = null;
		Vector<String> addedTags = new Vector<String>();

		searchPos = 0;

		if (aTemplate != null) {
			while ((aktTag = findNextDynTag(aTemplate, con)) != null) {
				addDynamicTag(aktTag);
				addedTags.add(aktTag.getDynName());
			}
		}

		return addedTags;
	}

	public Vector<String> findDynTagsInTemplates(ApplicationContext con) throws Exception {
		Vector<String> addedTags = new Vector<String>();
		MailingComponent tmpComp = null;
		searchPos = 0;

		Iterator<MailingComponent> it = this.components.values().iterator();
		while (it.hasNext()) {
			tmpComp = it.next();
			if (tmpComp.getType() == MailingComponent.TYPE_TEMPLATE) {
				addedTags.addAll(this.findDynTagsInTemplates(tmpComp.getEmmBlock(), con));
			}
		}

		return addedTags;
	}

	public Vector<String> scanForComponents(ApplicationContext con) throws Exception {
		Vector<String> addedTags = new Vector<String>();
		MailingComponent tmpComp = null;
		DynamicTag dyntag = null;
		DynamicTagContent dyncontent = null;

		searchPos = 0;

		Set<MailingComponent> componentsToAdd = new HashSet<MailingComponent>();
		Iterator<MailingComponent> itComponents = this.components.values().iterator();
		while (itComponents.hasNext()) {
			tmpComp = itComponents.next();
			if (tmpComp.getType() == MailingComponent.TYPE_TEMPLATE) {
				addedTags.addAll(this.scanForComponents(tmpComp.getEmmBlock(), con, componentsToAdd));
			}
		}
		addComponents( componentsToAdd);
		
		componentsToAdd.clear();
		Iterator<DynamicTag> itDynTag = this.dynTags.values().iterator();
		while (itDynTag.hasNext()) {
			dyntag = (DynamicTag) itDynTag.next();
			Iterator<DynamicTagContent> it2 = dyntag.getDynContent().values().iterator();
			while (it2.hasNext()) {
				dyncontent = it2.next();
				addedTags.addAll(this.scanForComponents(dyncontent.getDynContent(), con, componentsToAdd));
			}
		}
		addComponents( componentsToAdd);

		return addedTags;
	}

	@Override
	public Vector<String> scanForLinks(ApplicationContext con) throws Exception {
		Vector<String> addedLinks = new Vector<String>();
		MailingComponent tmpComp = null;
		DynamicTag dyntag = null;
		DynamicTagContent dyncontent = null;

		searchPos = 0;

		Iterator<MailingComponent> itComponents = this.components.values().iterator();
		while (itComponents.hasNext()) {
			tmpComp = itComponents.next();
			if (tmpComp.getType() == MailingComponent.TYPE_TEMPLATE) {
				addedLinks.addAll(this.scanForLinks(tmpComp.getEmmBlock(), con));
			}
		}

		Iterator<DynamicTag> itDynTag = this.dynTags.values().iterator();
		while (itDynTag.hasNext()) {
			dyntag = (DynamicTag) itDynTag.next();
			Iterator<DynamicTagContent> it2 = dyntag.getDynContent().values().iterator();
			while (it2.hasNext()) {
				dyncontent = it2.next();
				addedLinks.addAll(this.scanForLinks(dyncontent.getDynContent(), con));
			}
		}

		return addedLinks;
	}

	@Override
	public void addDynamicTag(DynamicTag aTag) {

		if (!this.dynTags.containsKey(aTag.getDynName())) {
			dynTags.put(aTag.getDynName(), aTag);
		}
		DynamicTag dbTag = this.dynTags.get(aTag.getDynName());
		if (dbTag.getGroup() != aTag.getGroup()) {
			dbTag.setGroup(aTag.getGroup());
		}
	}

	@Override
	public DynamicTag findNextDynTag(String aTemplate, ApplicationContext con) throws Exception {
		int valueTagStartPos;
		int oldPos;
		DynamicTag aDynTag = null;
		TagDetails aStartTag = null;
		TagDetails aEndTag = null;
		TagDetails aValueTag = null;

		aStartTag = getOneTag(aTemplate, "agnDYN", searchPos, con);
		if (aStartTag == null)
			return null;

		aStartTag.analyzeParameters();
		aStartTag.findTagParameters();

		searchPos = aStartTag.getEndPos();

		aDynTag = (DynamicTag) con.getBean("DynamicTag");
		aDynTag.setCompanyID(companyID);
		aDynTag.setMailingID(id);
		aDynTag.setComplex(aStartTag.isComplex());
		aDynTag.setDynName(aStartTag.getName());
		int group = 0;

		Map<String, String> params = aStartTag.getTagParameters();

		if (params != null) {
			String gname = (String) params.get("group");

			if (gname != null) {
				DynamicTagDao dao = (DynamicTagDao) con.getBean("DynamicTagDao");
				group = dao.getIdForName(this.id, gname);
			}
		}
		aDynTag.setGroup(group);

		if (aStartTag.isComplex()) {
			oldPos = searchPos;
			do {
				aEndTag = getOneTag(aTemplate, "/agnDYN", searchPos, con);
				if (aEndTag == null) {
					LineNumberReader aReader = new LineNumberReader(new StringReader(aTemplate));
					aReader.skip(searchPos);
					throw new Exception("NoEndTag$" + aReader.getLineNumber() + "$" + aStartTag.getName());
				}
				searchPos = aEndTag.getEndPos();
				aEndTag.analyzeParameters();
			} while (aStartTag.getName().compareTo(aEndTag.getName()) != 0);
			String valueArea = aTemplate.substring(aStartTag.getEndPos(), aEndTag.getStartPos());
			valueTagStartPos = 0;
			do {
				aValueTag = getOneTag(valueArea, "agnDVALUE", valueTagStartPos, con);
				if (aValueTag == null) {
					LineNumberReader aReader = new LineNumberReader(new StringReader(aTemplate));
					aReader.skip(searchPos);
					throw new Exception("NoValueTag$" + aReader.getLineNumber() + "$" + aStartTag.getName());
				}
				valueTagStartPos = aValueTag.getEndPos();
				aValueTag.analyzeParameters();
			} while (aStartTag.getName().compareTo(aValueTag.getName()) != 0);
			searchPos = oldPos;
			aDynTag.setStartTagStart(aStartTag.getStartPos());
			aDynTag.setStartTagEnd(aStartTag.getEndPos());
			aDynTag.setValueTagStart(aStartTag.getEndPos() + aValueTag.getStartPos());
			aDynTag.setValueTagEnd(aStartTag.getEndPos() + aValueTag.getEndPos());
			aDynTag.setEndTagStart(aEndTag.getStartPos());
			aDynTag.setEndTagEnd(aEndTag.getEndPos());
		} else {
			aDynTag.setStartTagStart(aStartTag.getStartPos());
			aDynTag.setStartTagEnd(aStartTag.getEndPos());
		}

		return aDynTag;
	}

	protected TagDetails getOneTag(String aTemplate, String TagName, int startPos, ApplicationContext con) throws Exception {
		return getOneTag(aTemplate, TagName, startPos, "[", "]", con);
	}

	protected TagDetails getOneTag(String aTemplate, String TagName, int startPos, String startMark, String endMark, ApplicationContext con) throws Exception {
		int posOfDynTag = 0;
		int endOfDynTag = 0;
		TagDetails det = (TagDetails) con.getBean("TagDetails");

		posOfDynTag = aTemplate.indexOf(startMark + TagName, startPos); // Search
																		// for
																		// next
																		// DYN-Tag

		if (posOfDynTag == -1) // if not DYN-Tag is found, return null
			return null;

		endOfDynTag = aTemplate.indexOf(endMark, posOfDynTag + 7); // Search for
																	// a closing
																	// bracket

		if (endOfDynTag == -1) // if the Tag-Closing Bracket is missing, throw a
								// exception
			throw new Exception("Missing Bracket$" + startPos);

		endOfDynTag++;

		det.setStartPos(posOfDynTag);
		det.setEndPos(endOfDynTag);
		det.setFullText(aTemplate.substring(posOfDynTag, endOfDynTag));

		return det;
	}

	private Vector<String> scanForComponents(String aText1, ApplicationContext con, Set<MailingComponent> componentsToAdd) {
		Vector<String> addComps = new Vector<String>();
		String aText = null;
		String aLink = null;
		int startMatch = 0;
		int endMatch = 0;
		MailingComponent tmpComp = null;

		if (aText1 == null) {
			return addComps;
		}

		try {
			aText = aText1.toLowerCase();
			Pattern aRegExp = Pattern.compile("https?://[0-9A-Za-z_.+-]+(:[0-9]+)?(/[^ \t\n\r<>\")]*)?");
			Matcher aMatch = aRegExp.matcher(aText);
			while (true) {
				if (!aMatch.find(endMatch)) {
					break;
				}
				startMatch = aMatch.start();
				endMatch = aMatch.end();
				if (aText.regionMatches(false, startMatch - 5, "src=\"", 0, 5) || aText.regionMatches(false, startMatch - 4, "src=", 0, 4)
						|| aText.regionMatches(false, startMatch - 11, "background=", 0, 11) || aText.regionMatches(false, startMatch - 12, "background=\"", 0, 12)) {
					aLink = aText1.substring(startMatch, endMatch);

					tmpComp = (MailingComponent) con.getBean("MailingComponent");
					tmpComp.setCompanyID(companyID);
					tmpComp.setMailingID(this.id);
					tmpComp.setComponentName(aLink);
					tmpComp.setType(MailingComponentImpl.TYPE_IMAGE);
					if (!components.containsKey(aLink)) {
						tmpComp.loadContentFromURL();
						if (tmpComp.getMimeType().startsWith("image")) {
//							addComponent(tmpComp);		// We will do that later
							componentsToAdd.add( tmpComp);
						}
					} else {
						tmpComp = (MailingComponent) this.components.get(aLink);
					}
					if (tmpComp.getMimeType().startsWith("image")) {
						addComps.add(aLink);
					}
				}
			}

		} catch (Exception e) {
			logger.error("scanForComponents", e);
		}

		return addComps;
	}

	@Override
	public Vector<String> scanForLinks(String aText1, ApplicationContext con) {
		String aLink = null;
		int start = 0;
		int end = 0;
		MailingComponent tmpComp = null;
		TrackableLink trkLink = null;
		Vector<String> addedLinks = new Vector<String>();

		if (aText1 == null) {
			return addedLinks;
		}
		
		TagDetails aDetail = (TagDetails) con.getBean("TagDetails");
		aDetail.setTagName("agnPROFILE");
		aText1 = aText1.replaceAll("\\[agnPROFILE\\]", AgnTagUtils.processTag(aDetail, 0, con, id, mailinglistID, companyID));

		aDetail.setTagName("agnUNSUBSCRIBE");
		aText1 = aText1.replaceAll("\\[agnUNSUBSCRIBE\\]", AgnTagUtils.processTag(aDetail, 0, con, id, mailinglistID, companyID));

		try {
			if ((aDetail = getFormTag(aText1, "agnFORM", 0, "[", "]", con)) != null) {
				aDetail.setTagName("agnFORM");
				int beginIndex = aDetail.getFullText().indexOf('"') + 1;
				int endIndex = aDetail.getFullText().indexOf('"', beginIndex);
				aDetail.setName(aDetail.getFullText().substring(beginIndex, endIndex));
				aText1 = aText1.replaceAll("\\[agnFORM name=\".*?\"\\]", AgnTagUtils.processTag(aDetail, 0, con, id, mailinglistID, companyID));
			}
		} catch (Exception e) {
			logger.error("scanForLinks", e);
		}


		try {
			Pattern aRegExp = Pattern.compile("https?://[0-9A-Za-z_.+-]+(:[0-9]+)?(/[^ \t\n\r<>\")]*)?");
			Matcher aMatch = aRegExp.matcher(aText1);
			while (true) {
				if (!aMatch.find(end)) {
					break;
				}
				start = aMatch.start();
				end = aMatch.end();
				
				try {
					if ((start == 0) || aText1.regionMatches(false, start - 5, "src=\"", 0, 5) || aText1.regionMatches(false, start - 4, "src=", 0, 4)
							|| aText1.regionMatches(false, start - 11, "background=", 0, 11) || aText1.regionMatches(false, start - 12, "background=\"", 0, 12)) {
						aLink = aText1.substring(start, end);
						if (!components.containsKey(aLink) && !trackableLinks.containsKey(aLink)) {
							tmpComp = (MailingComponent) con.getBean("MailingComponent");
							tmpComp.setCompanyID(companyID);
							tmpComp.setMailingID(id);
							tmpComp.setComponentName(aLink);
							tmpComp.setType(MailingComponent.TYPE_IMAGE);
							tmpComp.loadContentFromURL();
							if (!tmpComp.getMimeType().startsWith("image")) {
								// if(start == 0) {
								// aLink = aText1.substring(start, end);
								if (!trackableLinks.containsKey(aLink)) {
									trkLink = (TrackableLink) con.getBean("TrackableLink");
									
									trkLink.setCompanyID(this.companyID);
									trkLink.setFullUrl(aLink);
									trkLink.setMailingID(this.id);
									trkLink.setUsage(TrackableLink.TRACKABLE_TEXT_HTML);
									trkLink.setActionID(0);
					                
									trackableLinks.put(aLink, trkLink);
								}
								// }
							}
						}
						addedLinks.add(aLink);
					} else {
						aLink = aText1.substring(start, end);
						if (!trackableLinks.containsKey(aLink)) {
							trkLink = (TrackableLink) con.getBean("TrackableLink");
							trkLink.setCompanyID(this.companyID);
							trkLink.setFullUrl(aLink);
							trkLink.setMailingID(this.id);
							trkLink.setUsage(TrackableLink.TRACKABLE_TEXT_HTML);
							trkLink.setActionID(0);
							trackableLinks.put(aLink, trkLink);
						}
						addedLinks.add(aLink);
					}
				} catch( Exception e) {
					logger.error( "scanForLinks: Error processing link", e);
				}
			}

		} catch (Exception e) {
			logger.error("scanForLinks: error processing links", e);
		}

		return addedLinks;
	}

	@Override
	public java.sql.Timestamp getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(java.sql.Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public boolean triggerMailing(int maildropStatusID, Hashtable<String, Object> opts, ApplicationContext con) {
		Mailgun aMailgun = null;
		DataSource ds = (DataSource) con.getBean("dataSource");
		Connection dbCon = DataSourceUtils.getConnection(ds);
		boolean exitValue = true;

		try {
			if (maildropStatusID == 0) {
				throw new Exception("maildropStatusID is 0");
			}
			aMailgun = (Mailgun) con.getBean("Mailgun");
			aMailgun.initializeMailgun(Integer.toString(maildropStatusID));
			aMailgun.prepareMailgun(new Hashtable<String, Object>());
			aMailgun.executeMailgun(opts);
		} catch (Exception e) {
			logger.error("triggerMailing", e);
			exitValue = false;
		}
		DataSourceUtils.releaseConnection(dbCon, ds);
		return exitValue;
	}

	@Override
	public boolean isWorldMailingSend() {
		boolean returnValue = false;
		char status = MaildropEntry.STATUS_WORLD;
		MaildropEntry drop = null;

		switch (this.mailingType) {
		case Mailing.TYPE_ACTIONBASED:
			status = MaildropEntry.STATUS_ACTIONBASED;
			break;

		case Mailing.TYPE_DATEBASED:
			status = MaildropEntry.STATUS_DATEBASED;
			break;
		}

		Iterator<MaildropEntry> it = this.maildropStatus.iterator();
		while (it.hasNext()) {
			drop = it.next();
			if (drop.getStatus() == status) {
				returnValue = true;
			}
		}

		return returnValue;
	}

	@Override
	public Map<String, MailingComponent> getComponents() {
		return this.components;
	}

	@Override
	public Map<String, TrackableLink> getTrackableLinks() {
		return this.trackableLinks;
	}

	@Override
	public int getTargetID() {
		return targetID;
	}

	@Override
	public boolean sendEventMailing(int customerID, int delayMinutes, String userStatus, Hashtable<String, String> overwrite, ApplicationContext con) {
		boolean exitValue = true;
		Mailgun aMailgun = null;
		TimeoutLRUMap mailgunCache = (TimeoutLRUMap) con.getBean("mailgunCache");
		MaildropEntry entry = null;
		int maildropStatusID = 0;
		try {
			aMailgun = (Mailgun) mailgunCache.get(Integer.toString(this.companyID) + "_" + Integer.toString(this.id));

			if (aMailgun == null) {
				Iterator<MaildropEntry> it = this.getMaildropStatus().iterator();
				while (it.hasNext()) {
					entry = it.next();
					if (entry.getStatus() == MaildropEntry.STATUS_ACTIONBASED) {
						maildropStatusID = entry.getId();
					}
				}
				if (maildropStatusID == 0) {
					throw new Exception("maildropStatusID is 0");
				}
				aMailgun = (Mailgun) con.getBean("Mailgun");
				if (aMailgun == null) {
					logger.error("Mailgun could not be created: " + this.id);
				}
				aMailgun.initializeMailgun(Integer.toString(maildropStatusID));
				aMailgun.prepareMailgun(new Hashtable<String, Object>());

				mailgunCache.put(Integer.toString(this.companyID) + "_" + Integer.toString(this.id), aMailgun);
			}

			if (aMailgun != null) {
				Hashtable<String, Object> opts = new Hashtable<String, Object>();
				opts.put("customer-id", Integer.toString(customerID));
				if (overwrite != null) {
					opts.put("overwrite", overwrite);
				}
				java.util.Date aDate = new java.util.Date();
				long millis = aDate.getTime();
				millis += (delayMinutes * 60000);
				aDate.setTime(millis);
				opts.put("send-date", aDate);

				if (userStatus != null) {
					opts.put("user-status", userStatus);
				}
				aMailgun.executeMailgun(opts);
			}

		} catch (Exception e) {
			logger.error("Fire Campaign-Mail", e);

			exitValue = false;
		}

		return exitValue;
	}

	/**
	 * Getter for property mailingType.
	 * 
	 * @return Value of property mailingType.
	 */
	@Override
	public int getMailingType() {
		return this.mailingType;
	}

	/**
	 * Setter for property mailingType.
	 * 
	 * @param mailingType
	 *            New value of property mailingType.
	 */
	@Override
	public void setMailingType(int mailingType) {
		this.mailingType = mailingType;
	}

	@Override
	public boolean cleanupMaildrop(ApplicationContext con) {
		Iterator<MaildropEntry> it = this.maildropStatus.iterator();
		MaildropEntry entry = null;
		LinkedList<MaildropEntry> del = new LinkedList<MaildropEntry>();
		MaildropStatusDao dao = (MaildropStatusDao) con.getBean("MaildropStatusDao");

		while (it.hasNext()) {
			entry = (MaildropEntry) it.next();
			if (entry.getStatus() == 'E' || entry.getStatus() == 'R') {
				del.add(entry);

				if (AgnUtils.isOracleDB()) {
					dao.delete(entry.getId());
				}
			}
		}

		it = del.iterator();
		while (it.hasNext()) {
			entry = it.next();
			this.maildropStatus.remove(entry);
		}
		return true;
	}

	@Override
	public String getPreview(String input, int inputType, int customerID, ApplicationContext con) throws Exception {
		return getPreview(input, inputType, customerID, false, con);
	}

	@Override
	public String getPreview(String input_org, int inputType, int customerID, boolean overwriteMailtype, ApplicationContext con) throws Exception {
		DynamicTag aktTag = null;
		DynamicTag tmpTag = null;
		DynamicTag contentTag = null;
		String contentString = null;
		DynamicTagContent aContent = null;
		DynamicTagContent aTmpContent = null;
		Target aTarget = null;
		int aTargetID = 0;
		Hashtable<String, DynamicTag> allTags = new Hashtable<String, DynamicTag>();
		Hashtable<String, Target> allTargets = new Hashtable<String, Target>();
		Interpreter aBsh = null;
		String input = input_org;
		TargetDao tDao = (TargetDao) con.getBean("TargetDao");
		StringBuffer output = new StringBuffer(this.personalizeText(input, customerID, con));

		searchPos = 0;
		aBsh = AgnUtils.getBshInterpreter(companyID, customerID, con);
		if (overwriteMailtype) {
			aBsh.set("mailtype", new Integer(inputType));
		}

		if (aBsh == null) {
			throw new Exception("error.template.dyntags");
		}

		Iterator<DynamicTag> it = this.dynTags.values().iterator();
		while (it.hasNext()) {
			aktTag = it.next();
			tmpTag = (DynamicTag) con.getBean("DynamicTag");
			tmpTag.setDynName(aktTag.getDynName());
			Map<String, DynamicTagContent> contentMap = aktTag.getDynContent();
			if (contentMap != null) {
				Iterator<DynamicTagContent> it2 = contentMap.values().iterator();
				while (it2.hasNext()) {
					aContent = it2.next();
					aTmpContent = (DynamicTagContent) con.getBean("DynamicTagContent");
					aTmpContent.setDynName(aContent.getDynName());
					aTmpContent.setDynOrder(aContent.getDynOrder());
					aTmpContent.setDynContent(this.personalizeText(aContent.getDynContent(), customerID, con));
					aTmpContent.setTargetID(aContent.getTargetID());
					aTmpContent.setId(aContent.getId());
					tmpTag.addContent(aTmpContent);
				}
			}
			allTags.put(aktTag.getDynName(), tmpTag);
		}

		aContent = null;

		this.searchPos = 0;

		while ((aktTag = findNextDynTag(output.toString(), con)) != null) {
			searchPos = aktTag.getStartTagStart(); // always search from
													// beginning of last found
													// tag
			if (allTags.containsKey(aktTag.getDynName())) {
				contentTag = (DynamicTag) allTags.get(aktTag.getDynName());
				Map<String, DynamicTagContent> contentMap = contentTag.getDynContent();
				contentString = null; // reset always
				if (contentMap != null) {
					Iterator<DynamicTagContent> it3 = contentMap.values().iterator();
					while (it3.hasNext()) {
						aContent = it3.next();
						aTargetID = aContent.getTargetID();
						if (allTargets.containsKey(Integer.toString(aTargetID))) {
							aTarget = (Target) allTargets.get(Integer.toString(aTargetID));
						} else {
							aTarget = tDao.getTarget(aTargetID, this.companyID);
							if (aTarget == null) {
								aTarget = (Target) con.getBean("Target");
								aTarget.setCompanyID(this.companyID);
								aTarget.setId(aTargetID);
							}
							allTargets.put(Integer.toString(aTarget.getId()), aTarget);
						}
						aTargetID = aTarget.getId();
						if (aTargetID == 0) { // Target fits
							contentString = aContent.getDynContent();
							break; // stop processing list of content
						} else {
							if (aTarget.isCustomerInGroup(aBsh)) {
								contentString = aContent.getDynContent();
								break; // stop processing list of content
							}
						}
					}
				}
				// insert content if found content
				if (contentString != null) {
					if (aktTag.isComplex()) {
						output.delete(aktTag.getEndTagStart(), aktTag.getEndTagEnd());
						output.replace(aktTag.getValueTagStart(), aktTag.getValueTagEnd(), contentString);
						output.delete(aktTag.getStartTagStart(), aktTag.getStartTagEnd());
					} else {
						output.replace(aktTag.getStartTagStart(), aktTag.getStartTagEnd(), contentString);
					}
				} else {
					if (aktTag.isComplex()) {
						output.delete(aktTag.getStartTagStart(), aktTag.getEndTagEnd());
					} else {
						output.delete(aktTag.getStartTagStart(), aktTag.getStartTagEnd());
					}
				}
			} else { // dyntag not found in list, throw exception!
				throw new Exception("error.template.dyntags");
			}
		}
		if (inputType == MailingImpl.INPUT_TYPE_TEXT) {
			if (this.getEmailParam().getLinefeed() > 0) {
				output = new StringBuffer(SafeString.cutLineLength(SafeString.removeHTMLTags(output.toString()), this.getEmailParam().getLinefeed()));
			} else {
				output = new StringBuffer(SafeString.removeHTMLTags(output.toString()));
			}
		}

		output = new StringBuffer(this.insertTrackableLinks(output.toString(), customerID, con));

		return output.toString();
	}

	/**
	 * Scans a textblock for trackable links and replaces them with encoded
	 * rdir-links.
	 */
	public String insertTrackableLinks(String aText1, int customerID, ApplicationContext con) {
		if (this.trackableLinks == null) {
			return aText1;
		}

		/*
		 * trackableLinks is an unordered HashMap. When there are 2 links in the
		 * Map, where one is part of the other, this could lead to an link
		 * replacement, depending on the map ordering.
		 * 
		 * Link 1: http://www.mydomain.de Link 2:
		 * http://www.mydomain.de/path/index.htm
		 * 
		 * If Link 1 is returned before Link 2 from the iterator this resulted
		 * in: http://rdir.de/r.html?uid=<uid of Link1>/path/index.htm
		 */
		Comparator<String> reverseString = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		};

		Set<String> sorted = new TreeSet<String>(reverseString);
		sorted.addAll(this.trackableLinks.keySet());

		Iterator<String> i = sorted.iterator();
		String aLink = null;
		int start_link = 0;
		int end_link = 0;
		TrackableLink aLinkObj = null;
		StringBuffer aBuf = new StringBuffer(aText1);
		boolean isHref = false;

		if (aText1 == null) {
			return null;
		}

		while (i.hasNext()) {
			aLink = i.next();
			end_link = 0;
			while ((start_link = aBuf.indexOf(aLink, end_link)) != -1) {
				end_link = start_link + 1;
				isHref = false;
				if (start_link > 5 && (aBuf.substring(start_link - 6, start_link).equalsIgnoreCase("href=\""))) {
					isHref = true;
				}
				if (start_link > 6 && (aBuf.substring(start_link - 7, start_link).equalsIgnoreCase("href=\""))) {
					isHref = true;
				}
				if (aBuf.length() > (start_link + aLink.length())) {
					if (!(aBuf.charAt(start_link + aLink.length()) == ' ' || aBuf.charAt(start_link + aLink.length()) == '\'' || aBuf.charAt(start_link + aLink.length()) == '"')) {
						isHref = false;
					}
				}
				if (isHref) {
					aLinkObj = (TrackableLink) this.trackableLinks.get(aLink);
					aBuf.replace(start_link, start_link + aLink.length(), aLinkObj.encodeTagStringLinkTracking(con, customerID));
				}
			}
		}
		return aBuf.toString();
	}

	@Override
	public MailingComponent getTemplate(String type) {
		return (MailingComponent) this.components.get("agn" + type);
	}

	@Override
	public MailingComponent getHtmlTemplate() {
		return getTemplate("Html");
	}

	@Override
	public MailingComponent getTextTemplate() {
		return getTemplate("Text");
	}

	@Override
	public String personalizeText(String input, int customerID, ApplicationContext con) throws Exception {
		StringBuffer output = new StringBuffer(input);
		TagDetails aDetail = null;
		searchPos = 0;
		String aValue = null;

		while ((aDetail = this.getOneTag(output.toString(), "agn", searchPos, con)) != null) {
			searchPos = aDetail.getStartPos() + 1;
			aDetail.findTagName();
			if (!aDetail.getTagName().equals("agnDYN") && !aDetail.getTagName().equals("agnDVALUE")) {
				if (!aDetail.findTagParameters()) {
					throw new Exception("error.personalization_tag_parameter");
				}
				aValue = this.processTag(aDetail, customerID, con);
				if (aValue != null) {
					output.replace(aDetail.getStartPos(), aDetail.getEndPos(), aValue);
				}
				if (logger.isInfoEnabled()) 
					logger.info("personalizeText: Tag value from DB '" + aValue + "'");
			}
		}
		return output.toString();
	}

	@Override
	public boolean checkIfOK() {
		return true;
	}

	/**
	 * Getter for property emailParam.
	 * 
	 * @return Value of property emailParam.
	 */
	@Override
	public MediatypeEmail getEmailParam() {
		return (MediatypeEmail) mediatypes.get(new Integer(0));
	}

	/**
	 * Getter for property targetGroups.
	 * 
	 * @return Value of property targetGroups.
	 */
	@Override
	public Collection<Integer> getTargetGroups() {
		return this.targetGroups;
	}

	/**
	 * Setter for property targetGroups.
	 * 
	 * @param targetGroups
	 *            New value of property targetGroups.
	 */
	@Override
	public void setTargetGroups(Collection<Integer> targetGroups) {
		this.targetGroups = targetGroups;
		this.targetExpression = this.generateTargetExpression();
	}

	/**
	 * Setter for property htmlTemplate.
	 * 
	 * @param htmlTemplate
	 *            New value of property htmlTemplate.
	 */
	@Override
	public void setHtmlTemplate(MailingComponent htmlTemplate) {
		if (htmlTemplate != null) {
			this.components.put("agnHtml", htmlTemplate);
		}
	}

	/**
	 * Setter for property dynTags.
	 * 
	 * @param dynTags
	 *            New value of property dynTags.
	 */
	@Override
	public void setDynTags(Map<String, DynamicTag> dynTags) {
		this.dynTags = dynTags;
	}

	/**
	 * Setter for property dynTags.
	 * 
	 * @param trackableLinks
	 */
	@Override
	public void setTrackableLinks(Map<String, TrackableLink> trackableLinks) {
		this.trackableLinks = trackableLinks;
	}

	/**
	 * Setter for property components.
	 * 
	 * @param components
	 *            New value of property components.
	 */
	@Override
	public void setComponents(Map<String, MailingComponent> components) {
		this.components = components;
	}

	/**
	 * Setter for property textTemplate.
	 * 
	 * @param textTemplate
	 */
	@Override
	public void setTextTemplate(MailingComponent textTemplate) {
		if (textTemplate != null) {
			this.components.put("agnText", textTemplate);
		}
	}

	/**
	 * Getter for property mailTemplateID.
	 * 
	 * @return Value of property mailTemplateID.
	 */
	@Override
	public int getMailTemplateID() {
		return this.mailTemplateID;
	}

	/**
	 * Getter for property templateOK.
	 * 
	 * @return Value of property templateOK.
	 */
	@Override
	public int getTemplateOK() {
		return this.templateOK;
	}

	/**
	 * Setter for property templateOK.
	 * 
	 * @param templateOK
	 *            New value of property templateOK.
	 */
	@Override
	public void setTemplateOK(int templateOK) {
		this.templateOK = templateOK;
	}

	/**
	 * Getter for property isTemplate.
	 * 
	 * @return Value of property isTemplate.
	 */
	@Override
	public boolean isIsTemplate() {
		return this.isTemplate;
	}

	/**
	 * Setter for property isTemplate.
	 * 
	 * @param isTemplate
	 *            New value of property isTemplate.
	 */
	@Override
	public void setIsTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	/**
	 * Getter for property targetMode.
	 * 
	 * @return Value of property targetMode.
	 */
	@Override
	public int getTargetMode() {
		return this.targetMode;
	}

	/**
	 * Setter for property targetMode.
	 * 
	 * @param targetMode
	 *            New value of property targetMode.
	 */
	@Override
	public void setTargetMode(int targetMode) {
		this.targetMode = targetMode;
	}

	/**
	 * Getter for property targetExpression.
	 * 
	 * @return Value of property targetExpression.
	 */
	@Override
	public String getTargetExpression() {
		return this.targetExpression;
	}

	/**
	 * Setter for property targetExpression.
	 * 
	 * @param targetExpression
	 *            New value of property targetExpression.
	 */
	@Override
	public void setTargetExpression(String targetExpression) {

		this.targetExpression = targetExpression;
		this.parseTargetExpression(this.targetExpression);
	}

	/**
	 * Getter for property mediatypes.
	 * 
	 * @return Value of property mediatypes.
	 */
	@Override
	public Map<Integer, Mediatype> getMediatypes() {

		return this.mediatypes;
	}

	/**
	 * Setter for property mediatypes.
	 * 
	 * @param mediatypes
	 *            New value of property mediatypes.
	 */
	@Override
	public void setMediatypes(Map<Integer, Mediatype> mediatypes) {

		this.mediatypes = mediatypes;
	}

	@Override
	public void init(int companyID, ApplicationContext con) {
		MailingComponent comp = null;
		Mediatype type = null;

		this.companyID = companyID;

		comp = (MailingComponent) con.getBean("MailingComponent");
		comp.setCompanyID(companyID);
		comp.setComponentName("agnText");
		comp.setType(MailingComponent.TYPE_TEMPLATE);
		comp.setEmmBlock("[agnDYN name=\"Text-Version\"/]");
		comp.setMimeType("text/plain");
		this.components.put("agnText", comp);

		comp = (MailingComponent) con.getBean("MailingComponent");
		comp.setCompanyID(companyID);
		comp.setComponentName("agnHtml");
		comp.setType(MailingComponent.TYPE_TEMPLATE);
		comp.setEmmBlock("[agnDYN name=\"HTML-Version\"/]");
		comp.setMimeType("text/html");
		this.components.put("agnHtml", comp);

		type = (Mediatype) con.getBean("MediatypeEmail");
		type.setCompanyID(companyID);
		this.mediatypes.put(new Integer(0), type);
	}

	@Override
	public List<String> cleanupDynTags(Vector<String> keep) {
		Vector<String> remove = new Vector<String>();
		String tmp = null;

		// first find keys which should be removed
		Iterator<String> it = this.dynTags.keySet().iterator();
		while (it.hasNext()) {
			tmp = it.next();
			if (!keep.contains(tmp)) {
				remove.add(tmp);
			}
		}

		// now remove them!
		Enumeration<String> e = remove.elements();
		while (e.hasMoreElements()) {
			dynTags.remove(e.nextElement());
		}

		return remove;
	}

	@Override
	public void cleanupTrackableLinks(Vector<String> keep) {
		Vector<String> remove = new Vector<String>();
		String tmp = null;

		// first find keys which should be removed
		Iterator<String> it = this.trackableLinks.keySet().iterator();
		while (it.hasNext()) {
			tmp = it.next();
			if (!keep.contains(tmp)) {
				remove.add(tmp);
			}
		}
		// now remove them!
		Enumeration<String> e = remove.elements();
		while (e.hasMoreElements()) {
			this.trackableLinks.remove(e.nextElement());
		}
	}

	@Override
	public void cleanupMailingComponents(Vector<String> keep) {
		Vector<String> remove = new Vector<String>();
		MailingComponent tmp = null;

		// first find keys which should be removed
		Iterator<MailingComponent> it = this.components.values().iterator();
		while (it.hasNext()) {
			tmp = it.next();
			if (tmp.getType() == MailingComponent.TYPE_IMAGE && !keep.contains(tmp.getComponentName())) {
				remove.add(tmp.getComponentName());
			}
		}

		// now remove them!
		Enumeration<String> e = remove.elements();
		while (e.hasMoreElements()) {
			this.components.remove(e.nextElement());
		}
	}

	@Override
	public DynamicTag getDynamicTagById(int dynId) {
		DynamicTag tmp = null;

		Iterator<DynamicTag> it = this.dynTags.values().iterator();
		while (it.hasNext()) {
			tmp = it.next();
			if (dynId == tmp.getId()) {
				return tmp;
			}
		}
		return null;
	}

	@Override
	public TrackableLink getTrackableLinkById(int urlID) {
		TrackableLink tmp = null;

		Iterator<TrackableLink> it = this.trackableLinks.values().iterator();
		while (it.hasNext()) {
			tmp = it.next();
			if (urlID == tmp.getId()) {
				return tmp;
			}
		}
		return null;
	}

	@Override
	public Object clone(ApplicationContext con) {
		Mailing tmpMailing = (Mailing) con.getBean("Mailing");
		MailingComponent compOrg = null;
		MailingComponent compNew = null;
		TrackableLink linkOrg = null;
		TrackableLink linkNew = null;
		DynamicTag tagOrg = null;
		DynamicTag tagNew = null;
		DynamicTagContent contentOrg = null;
		DynamicTagContent contentNew = null;
		Mediatype emailNew = null;

		try {
			// copy components
			Iterator<MailingComponent> comps = this.components.values().iterator();
			while (comps.hasNext()) {
				compOrg = comps.next();
				compNew = (MailingComponent) con.getBean("MailingComponent");
				if (compOrg.getBinaryBlock() == null) {
					compOrg.setBinaryBlock(new byte[1]);
				}
				BeanUtils.copyProperties(compNew, compOrg);
				compNew.setId(0);
				compNew.setMailingID(0);
				tmpMailing.addComponent(compNew);
			}

			// copy dyntags
			Iterator<DynamicTag> dyntags = this.dynTags.values().iterator();
			while (dyntags.hasNext()) {
				tagOrg = dyntags.next();
				tagNew = (DynamicTag) con.getBean("DynamicTag");
				Iterator<DynamicTagContent> contents = tagOrg.getDynContent().values().iterator();
				while (contents.hasNext()) {
					contentOrg = contents.next();
					contentNew = (DynamicTagContent) con.getBean("DynamicTagContent");
					BeanUtils.copyProperties(contentNew, contentOrg);
					contentNew.setId(0);
					contentNew.setDynNameID(0);
					tagNew.addContent(contentNew);
				}
				tagNew.setCompanyID(tagOrg.getCompanyID());
				tagNew.setDynName(tagOrg.getDynName());
				tmpMailing.addDynamicTag(tagNew);
			}

			// copy urls
			Iterator<TrackableLink> urls = this.trackableLinks.values().iterator();
			while (urls.hasNext()) {
				linkOrg = urls.next();
				linkNew = (TrackableLink) con.getBean("TrackableLink");
				BeanUtils.copyProperties(linkNew, linkOrg);
				linkNew.setId(0);
				linkNew.setMailingID(0);
				linkNew.setActionID(linkOrg.getActionID());
				tmpMailing.getTrackableLinks().put(linkNew.getFullUrl(), linkNew);
			}

			// copy emailparam
			emailNew = (Mediatype) con.getBean("MediatypeEmail");
			emailNew.setParam(this.getEmailParam().getParam());
			tmpMailing.getMediatypes().put(new Integer(0), emailNew);
			tmpMailing.setOpenActionID(this.openActionID);
			tmpMailing.setClickActionID(this.clickActionID);

		} catch (Exception e) {

			logger.error("could not copy", e);
			return null;
		}
		return tmpMailing;
	}

	@Override
	public boolean buildDependencies(boolean scanDynTags, ApplicationContext con) throws Exception {
		return buildDependencies(scanDynTags, null, con);
	}

	@Override
	public boolean buildDependencies(boolean scanDynTags, List<String> dynNamesForDeletion, ApplicationContext con) throws Exception {
		Vector<String> dynTags = new Vector<String>();
		Vector<String> components = new Vector<String>();
		Vector<String> links = new Vector<String>();

		// scan for Dyntags
		// in template-components and Mediatype-Params
		if (scanDynTags) {
			dynTags.addAll(this.findDynTagsInTemplates(con));
			dynTags.addAll(this.findDynTagsInTemplates(this.getEmailParam().getSubject(), con));
			dynTags.addAll(this.findDynTagsInTemplates(this.getEmailParam().getReplyAdr(), con));
			dynTags.addAll(this.findDynTagsInTemplates(this.getEmailParam().getFromAdr(), con));
			List<String> dynNamesList = this.cleanupDynTags(dynTags);

			if (dynNamesForDeletion != null)
				dynNamesForDeletion.addAll(dynNamesList);
		}
		// scan for Components
		// in template-components and dyncontent
		components.addAll(this.scanForComponents(con));
		this.cleanupMailingComponents(components);

		// scan for Links
		// in template-components and dyncontent
		links.addAll(this.scanForLinks(con));
		// if(AgnUtils.isOracleDB()) {
		// causes problem with links in OpenEMM
		this.cleanupTrackableLinks(links);
		// }

		return true;
	}

	@Override
	public Map<Integer, Target> getAllowedTargets(ApplicationContext myContext) {
		if (allowedTargets == null) {
			TargetDao dao = (TargetDao) myContext.getBean("TargetDao");
	
			allowedTargets = dao.getAllowedTargets(companyID);
			if (allowedTargets != null) {
				Target aTarget = (Target) myContext.getBean("Target");
	
				aTarget.setCompanyID(companyID);
				aTarget.setId(0);
				aTarget.setTargetName("All Subscribers");
				allowedTargets.put(new Integer(0), aTarget);
			}
		}
		
		return allowedTargets;
	}
	
	private TagDetails getFormTag(String aTemplate, String TagName, int startPos, String startMark, String endMark, ApplicationContext con) throws Exception {
		int posOfDynTag = 0;
		int endOfDynTag = 0;
		TagDetails detail = (TagDetails) con.getBean("TagDetails");

		posOfDynTag = aTemplate.indexOf(startMark + TagName, startPos); // Search for next DYN-Tag

		if (posOfDynTag == -1) {// if not DYN-Tag is found, return null
			return null;
		}

		endOfDynTag = aTemplate.indexOf(endMark, posOfDynTag + 8); // Search for a closing bracket

		if (endOfDynTag == -1) { // if the Tag-Closing Bracket is missing, throw an exception
			throw new Exception("Missing Bracket$" + startPos);
		}

		endOfDynTag++;

		detail.setStartPos(posOfDynTag);
		detail.setEndPos(endOfDynTag);
		detail.setFullText(aTemplate.substring(posOfDynTag, endOfDynTag));

		return detail;
	}

	/**
	 * Getter for property maildropStatus.
	 * 
	 * @return Value of property maildropStatus.
	 */
	@Override
	public Set<MaildropEntry> getMaildropStatus() {
		return this.maildropStatus;
	}

	/**
	 * Setter for property maildropStatus.
	 * 
	 * @param maildropStatus
	 *            New value of property maildropStatus.
	 */
	@Override
	public void setMaildropStatus(Set<MaildropEntry> maildropStatus) {
		this.maildropStatus = maildropStatus;
	}

	/**
	 * Getter for property deleted.
	 * 
	 * @return Value of property deleted.
	 */
	@Override
	public int getDeleted() {
		return this.deleted;
	}

	/**
	 * Setter for property deleted.
	 * 
	 * @param deleted
	 *            New value of property deleted.
	 */
	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Override
	public boolean getNeedsTarget() {
		return this.needsTarget;
	}

	@Override
	public void setNeedsTarget(boolean needsTarget) {
		this.needsTarget = needsTarget;
	}

	@Override
	public int getLocked() {
		return this.locked ? 1 : 0;
	}

	@Override
	public void setLocked(int locked) {
		this.locked = (locked != 0) ? true : false;
	}

	@Override
	public void setSearchPos(int pos) {
		searchPos = pos;
	}

	@Override
	public int getArchived() {
		return this.archived ? 1 : 0;
	}

	@Override
	public void setArchived(int archived) {
		this.archived = (archived != 0) ? true : false;
	}

	@Override
	public int getOpenActionID() {
		return openActionID;
	}

	@Override
	public void setOpenActionID(int id) {
		this.openActionID = id;
	}

	@Override
	public int getClickActionID() {
		return this.clickActionID;
	}

	@Override
	public void setClickActionID(int id) {
		this.clickActionID = id;
	}

	@Override
	public String processTag(TagDetails detail, int customerID, ApplicationContext con) {
		return AgnTagUtils.processTag(detail, customerID, con, id, mailinglistID, companyID);
	}
}
