package org.agnitas.emm.core.mailing.service.impl;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.emm.core.commons.util.DateUtil;
import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingModel.MaildropStatus;
import org.agnitas.emm.core.mailing.service.MailingNotExistException;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.core.mailing.service.SendDateNotInFutureException;
import org.agnitas.emm.core.mailing.service.TemplateNotExistException;
import org.agnitas.emm.core.mailing.service.WorldMailingAlreadySentException;
import org.agnitas.emm.core.mailinglist.service.MailinglistNotExistException;
import org.agnitas.emm.core.target.service.TargetNotExistException;
import org.agnitas.emm.core.validator.annotation.Validate;
import org.agnitas.mailing.beans.MaildropEntryFactory;
import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

public class MailingServiceImpl implements MailingService, ApplicationContextAware {
	
	private Logger logger = Logger.getLogger(MailingServiceImpl.class);

	@Resource(name="MailingDao")
	protected MailingDao mailingDao;
	@Resource(name="MailinglistDao")
	private MailinglistDao mailinglistDao;
	@Resource(name="TargetDao")
	private TargetDao targetDao;
	@Resource(name="MaildropEntryFactory")
	private MaildropEntryFactory maildropEntryFactory;
//	@Resource(name="ClassicTemplateGenerator")
//	private ClassicTemplateGenerator classicTemplateGenerator;

	protected ApplicationContext applicationContext;

	@Override
	@Transactional
	@Validate("addMailing")
	public int addMailing(MailingModel model) {

		int result = 0;
		Mailing aMailing = prepareMailingForAddOrUpdate(model, (Mailing) applicationContext.getBean("Mailing"));

		MailingComponent comp = null;

		comp = (MailingComponent) applicationContext.getBean("MailingComponent");
		comp.setCompanyID(model.getCompanyId());
		comp.setComponentName("agnText");
		comp.setType(MailingComponent.TYPE_TEMPLATE);
		comp.setEmmBlock("[agnDYN name=\"emailText\"/]");
		comp.setMimeType("text/plain");
		aMailing.addComponent(comp);

		comp = (MailingComponent) applicationContext.getBean("MailingComponent");
		comp.setCompanyID(model.getCompanyId());
		comp.setComponentName("agnHtml");
		comp.setType(MailingComponent.TYPE_TEMPLATE);
		comp.setEmmBlock("[agnDYN name=\"emailHtml\"/]");
		comp.setMimeType("text/html");
		aMailing.addComponent(comp);

		try {
			aMailing.buildDependencies(true, applicationContext);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		mailingDao.saveMailing(aMailing);
		result = aMailing.getId();
		return result;
	}
	
	protected Mailing prepareMailingForAddOrUpdate(MailingModel model, Mailing aMailing) {

		aMailing.setCompanyID(model.getCompanyId());
		aMailing.setDescription(model.getDescription() != null ? model.getDescription() : "");
		aMailing.setShortname(model.getShortname());
		aMailing.setIsTemplate(model.isTemplate());

		if (model.getMailinglistId() != 0) {
			if (!mailinglistDao.exist(model.getMailinglistId(), model.getCompanyId())) {
				throw new MailinglistNotExistException();
			}
			aMailing.setMailinglistID(model.getMailinglistId());
		}

		aMailing.setTargetMode(model.getTargetMode().getValue());
		// aMailing.setTargetID(targetID);
		if (model.getTargetIDList() != null && model.getTargetIDList().size() > 0) {
			Set<Integer> tGroups = new HashSet<Integer>();
			for (Integer targetID : model.getTargetIDList()) {
				if (targetDao.getTarget(targetID, model.getCompanyId()) == null) {
					throw new TargetNotExistException(targetID);
				}
				tGroups.add(targetID);
			}
			aMailing.setTargetGroups(tGroups);
		} else {
			aMailing.setTargetGroups(null);
		}
		aMailing.setMailingType(model.getMailingType().getValue());
		MediatypeEmail paramEmail = aMailing.getEmailParam();
		if (paramEmail == null) {
			paramEmail = (MediatypeEmail) applicationContext.getBean("MediatypeEmail");
			paramEmail.setCompanyID(model.getCompanyId());
			paramEmail.setMailingID(aMailing.getId());
		}
		paramEmail.setStatus(Mediatype.STATUS_ACTIVE);
		paramEmail.setSubject(model.getSubject());
		try {
			InternetAddress adr = new InternetAddress(model.getSenderAddress(), model.getSenderName());
			paramEmail.setFromEmail(adr.getAddress());
			paramEmail.setFromFullname(adr.getPersonal());
			
			InternetAddress reply = new InternetAddress(model.getReplyToAddress(), model.getReplyToName());
			paramEmail.setReplyEmail(reply.getAddress());
			paramEmail.setReplyFullname(reply.getPersonal());
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException in sender/reply address", e);
			throw new RuntimeException(e);
		}
		paramEmail.setCharset(model.getCharset());
		paramEmail.setMailFormat(model.getFormat().getValue());
		paramEmail.setLinefeed(model.getLinefeed());
		paramEmail.setPriority(1);
		
		paramEmail.setOnepixel(model.getOnePixel().getName());
		
		Map<Integer, Mediatype> mediatypes = aMailing.getMediatypes();
		mediatypes.put(0, paramEmail);
		aMailing.setMediatypes(mediatypes);
		
//		aMailing.setUseDynamicTemplate(model.isAutoUpdate());
		
		return aMailing;
	}

	@Override
	@Transactional
	@Validate("updateMailing")
	public void updateMailing(MailingModel model) {

		Mailing aMailing = prepareMailingForAddOrUpdate(model, getMailing(model));
		
		try {
			aMailing.buildDependencies(true, applicationContext);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
       	mailingDao.saveMailing(aMailing);
            
       	// Mark names in dynNamesForDeletion as "deleted"
//        for( String dynName : dynNamesForDeletion)
//        	dynamicTagDao.markNameAsDeleted( aMailing.getId(), dynName);
//            
//            result=true;
//        } catch (Exception e) {
//            System.out.println("Error in create mailing id: "+aMailing.getId()+" msg: "+e);
//            result=false;
//        }
	}


	@SuppressWarnings("unchecked")
	private <T> T cloneBean(T orig, String beanName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T dest = (T) applicationContext.getBean(beanName);
		PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(orig);
		for (int i = 0; i < origDescriptors.length; i++) {
			String name = origDescriptors[i].getName();
			if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
				try {
					Object value = PropertyUtils.getSimpleProperty(orig, name);
					if (!(value instanceof Collection<?>) && !(value instanceof Map<?, ?>)) {
						PropertyUtils.setSimpleProperty(dest, name, value);
					}
				} catch (NoSuchMethodException e) {
					logger.debug("Error writing to '" + name + "' on class '" + dest.getClass() + "'", e);
				}
			}
		}
		//		PropertyUtils.copyProperties(dest, orig);
//		BeanUtils.copyProperties(dest, orig);
		return dest;
	}
	
	
	
	@Override
	@Transactional
	@Validate("addMailingFromTemplate")
	public int addMailingFromTemplate(MailingModel model) {
		int result = 0;
    	Mailing template = mailingDao.getMailing(model.getTemplateId(), model.getCompanyId());
    	
    	if (template == null || !template.isIsTemplate()) {
    		throw new TemplateNotExistException();
    	}
    	
		try {
			Mailing aMailing = cloneBean(template, "Mailing");

			aMailing.setId(0);
			aMailing.setDescription(model.getDescription() != null ? model.getDescription() : "");
			aMailing.setShortname(model.getShortname());
			aMailing.setIsTemplate(false);
			aMailing.setMailTemplateID(model.getTemplateId());

			// copy components
			Iterator<MailingComponent> comps = template.getComponents().values().iterator();
			while (comps.hasNext()) {
				MailingComponent compOrg = comps.next();
				// was in previous web service implementation
				if (compOrg.getBinaryBlock() == null) {
					compOrg.setBinaryBlock(new byte[1]);
				}
				MailingComponent compNew = cloneBean(compOrg, "MailingComponent");
				compNew.setId(0);
				compNew.setMailingID(0);
				aMailing.getComponents().put(compNew.getComponentName(), compNew);
			}

			// copy dyntags
			Iterator<DynamicTag> dyntags = template.getDynTags().values().iterator();
			while (dyntags.hasNext()) {
				DynamicTag tagOrg = dyntags.next();
				DynamicTag tagNew = cloneBean(tagOrg, "DynamicTag");
				tagNew.setId(0);
				tagNew.setMailingID(0);
				tagNew.setDynContent(new HashMap<String, DynamicTagContent>());
				Iterator<DynamicTagContent> contents = tagOrg.getDynContent().values().iterator();
				while (contents.hasNext()) {
					DynamicTagContent contentOrg = (DynamicTagContent) contents.next();
					DynamicTagContent contentNew = cloneBean(contentOrg, "DynamicTagContent");
					contentNew.setId(0);
					contentNew.setDynNameID(0);
					tagNew.addContent(contentNew);
				}
				aMailing.getDynTags().put(tagNew.getDynName(), tagNew);
			}

			// copy urls
			Iterator<TrackableLink> urls = template.getTrackableLinks().values().iterator();
			while (urls.hasNext()) {
				TrackableLink linkOrg = urls.next();
				TrackableLink linkNew =  cloneBean(linkOrg, "TrackableLink");
				linkNew.setId(0);
				linkNew.setMailingID(0);
				aMailing.getTrackableLinks().put(linkNew.getFullUrl(), linkNew);
			}

			// copy mediatypes
			for (Integer index : template.getMediatypes().keySet()) {
				Mediatype mediaTypeOrg = template.getMediatypes().get(index);
				String beanName;
				switch (index) {
				case 0:
					beanName = "MediatypeEmail";
					break;
				case 1:
					beanName = "MediatypeFax";
					break;
				case 2:
					beanName = "MediatypePrint";
					break;
				case 3:
					beanName = "MediatypeMMS";
					break;
				case 4:
					beanName = "MediatypeSMS";
					break;
				default:
					beanName = "Mediatype";
					break;
				}
				Mediatype mediatypeNew = cloneBean(mediaTypeOrg, beanName);
				aMailing.getMediatypes().put(index, mediatypeNew);
			}

			aMailing.setUseDynamicTemplate(model.isAutoUpdate());

	        mailingDao.saveMailing(aMailing);
	        result=aMailing.getId();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

        return result;
	}

	@Override
	@Validate("getMailing")
	@Transactional(readOnly = true)
	public Mailing getMailing(MailingModel model) {
		Mailing mailing = mailingDao.getMailing(model.getMailingId(), model.getCompanyId());
		if (model.isTemplate() && (mailing == null || mailing.getId() == 0 || !mailing.isIsTemplate())) {
			throw new TemplateNotExistException();
		} else if (!model.isTemplate() && (mailing == null || mailing.getId() == 0 || mailing.isIsTemplate())) {
			throw new MailingNotExistException();
		}
		return mailing;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	@Validate("getMailing")
	@Transactional
	public void deleteMailing(MailingModel model) {
		if (!mailingDao.exist(model.getMailingId(), model.getCompanyId(), model.isTemplate())) {
			throw model.isTemplate() ? new TemplateNotExistException() : new MailingNotExistException();
		}
		mailingDao.deleteMailing(model.getMailingId(), model.getCompanyId());
	}

	@Override
	@Validate("company")
	public List<Mailing> getMailings(MailingModel model) {
		return mailingDao.getMailings(model.getCompanyId(), model.isTemplate());
	}

	@Override
	@Validate("getMailingForMLID")
	@Transactional(readOnly = true)
	public List<Mailing> getMailingsForMLID(MailingModel model) {
		if (!mailinglistDao.exist(model.getMailinglistId(), model.getCompanyId())) {
			throw new MailinglistNotExistException();
		}
		return mailingDao.getMailingsForMLID(model.getCompanyId(), model.getMailinglistId());
	}

	@Override
	@Transactional
	@Validate("sendMailing")
	public void sendMailing(MailingModel model) {
		if (!DateUtil.isValidSendDate(model.getSendDate())) {
			throw new SendDateNotInFutureException();
		}
		
        Calendar now = Calendar.getInstance();
        
		Mailing mailing = getMailing(model);
		
		if (model.getMaildropStatus() == MaildropStatus.WORLD && mailing.isWorldMailingSend()) {
			throw new WorldMailingAlreadySentException();
		}

		MaildropEntry maildrop = maildropEntryFactory.createMaildropEntry();

		maildrop.setStatus(model.getMaildropStatus().getValue());
		maildrop.setMailingID(model.getMailingId());
		maildrop.setCompanyID(model.getCompanyId());
        maildrop.setStepping(model.getStepping());
		maildrop.setBlocksize(model.getBlocksize());

		maildrop.setSendDate(model.getSendDate());
		
		Calendar tmpGen = Calendar.getInstance();
        tmpGen.setTime(model.getSendDate());
        tmpGen.add(Calendar.HOUR_OF_DAY, -3);
        if(tmpGen.before(now)) {
            tmpGen=now;
        }
        maildrop.setGenDate(tmpGen.getTime());
		maildrop.setGenChangeDate(now.getTime());
		
		if( model.getMaildropStatus() == MaildropStatus.WORLD) {
			maildrop.setGenStatus(DateUtil.isDateForImmediateGeneration(maildrop.getGenDate()) ? 1 : 0);
		} else if( model.getMaildropStatus() == MaildropStatus.TEST || model.getMaildropStatus() == MaildropStatus.ADMIN) {
			maildrop.setGenStatus( 1);
		}
              
        mailing.getMaildropStatus().add(maildrop);
        
        mailingDao.saveMailing(mailing);
        if (maildrop.getGenStatus() == 1) {
//        	classicTemplateGenerator.generate(mailingId, adminId, companyId, checkMailingType, copyImages)
            mailing.triggerMailing(maildrop.getId(), new Hashtable<String, Object>(), applicationContext);
        }
        if (logger.isInfoEnabled()) {
        	logger.info("send mailing id: "+mailing.getId()+" type: "+maildrop.getStatus());
        }
        
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}
	
}
