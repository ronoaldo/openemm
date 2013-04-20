package org.agnitas.emm.core.target.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.target.service.TargetService;
import org.agnitas.target.Target;
import org.apache.log4j.Logger;

public class TargetServiceImpl implements TargetService {

	private static final transient Logger logger = Logger.getLogger( TargetServiceImpl.class);
	
	private static final transient Pattern TARGET_IDS_FROM_EXPRESSION_PATTERN = Pattern.compile( "^.*?(\\d+)(.*)$");

	@Override
    public boolean hasMailingDeletedTargetGroups(Mailing mailing) {

		if( logger.isInfoEnabled())
			logger.info( "Checking mailing " + mailing.getId() + " for deleted target groups");
		
    	Set<Integer> targetIds = getAllTargetIdsForMailing( mailing);
    	
    	for( int targetId : targetIds) {
    		Target target = targetDao.getTarget(targetId, mailing.getCompanyID());

    		if( target == null) {
    			if( logger.isInfoEnabled()) {
    				logger.info( "Found non-existing target group " + targetId + ". It's assumed to be physically deleted.");
    			}

    			continue;
    		}
    		
    		if( target.getDeleted() != 0) {
    			if( logger.isInfoEnabled()) {
    				logger.info( "Found deleted target group " + targetId + ".");
    			}
    			
    			return true;
    		}
    	}
    	
    	if( logger.isInfoEnabled())
    		logger.info( "Mailing " + mailing.getId() + " does not contain any deleted target groups");
    	
    	return false;
    }
	
	private Set<Integer> getAllTargetIdsForMailing( Mailing mailing) {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId());
		}
		
    	Set<Integer> targetIds = new HashSet<Integer>();

    	targetIds.addAll(getTargetIdsFromExpression(mailing));
    	targetIds.addAll(getTargetIdsFromContent(mailing));
    	targetIds.addAll(getTargetIdsFromAttachments(mailing));
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs in total for mailing " + mailing.getId());
		}
    	
    	return targetIds;
	}
    
	@Override
    public Set<Integer> getTargetIdsFromExpression(Mailing mailing) {
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId() + " from mailing target expression.");
		}
		
    	Set<Integer> targetIds = new HashSet<Integer>();
    	if (mailing == null) {
    		return targetIds;
    	}
    	
    	String expression = mailing.getTargetExpression();
    	if( expression != null) {
    		Matcher matcher = TARGET_IDS_FROM_EXPRESSION_PATTERN.matcher(expression);
    		
    		while( matcher.matches()) {
    			targetIds.add(Integer.parseInt(matcher.group(1)));
    			expression = matcher.group(2);
    			matcher = TARGET_IDS_FROM_EXPRESSION_PATTERN.matcher(expression);
    		}
    	}
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs from target expression for mailing " + mailing.getId());
		}
     	
    	return targetIds;
    }
    
    private Set<Integer> getTargetIdsFromContent(Mailing mailing) {
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId() + " from content blocks.");
		}
		
    	Set<Integer> targetIds = new HashSet<Integer>();
    	
    	for( DynamicTag tag : mailing.getDynTags().values()) {
    		for( Object contentObject : tag.getDynContent().values()) {
    			DynamicTagContent content = (DynamicTagContent) contentObject;
    			targetIds.add( content.getTargetID());
    		}
    	}
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs from content blocks for mailing " + mailing.getId());
		}
    	
    	return targetIds;
    }
    
    private Set<Integer> getTargetIdsFromAttachments(Mailing mailing) {
		if( logger.isDebugEnabled()) {
			logger.debug( "Collecting target groups IDs for mailing " + mailing.getId() + " from attachments.");
		}

		List<MailingComponent> result = mailingComponentDao.getMailingComponents(mailing.getId(), mailing.getCompanyID(), MailingComponent.TYPE_ATTACHMENT);
    	
    	Set<Integer> targetIds = new HashSet<Integer>();
    	for( MailingComponent component : result) {
    		targetIds.add( component.getTargetID());
    	}
    	
		if( logger.isDebugEnabled()) {
			logger.debug( "Collected " + mailing.getId() + " different target group IDs from attachments for mailing " + mailing.getId());
		}
    	
    	return targetIds;
    }

    // -------------------------------------------------------------- Dependency Injection
    protected TargetDao targetDao;
    private MailingComponentDao mailingComponentDao;
    
    public void setTargetDao( TargetDao targetDao) {
    	this.targetDao = targetDao;
    }
    
    public void setMailingComponentDao( MailingComponentDao mailingComponentDao) {
    	this.mailingComponentDao = mailingComponentDao;
    }

}
