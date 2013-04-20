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

package org.agnitas.actions.ops;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.agnitas.actions.ActionOperation;
import org.agnitas.dao.MailingDao;
import org.agnitas.preview.Preview;
import org.agnitas.preview.PreviewFactory;
import org.agnitas.preview.PreviewHelper;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author  mhe
 * @version
 */
public class GetArchiveMailing extends ActionOperation implements Serializable {

    static final long serialVersionUID = -7088135693353506633L;

    public static TimeoutLRUMap mailingCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("archive.maxCache"), AgnUtils.getDefaultIntValue("archive.maxCacheTimeMillis"));
    public static TimeoutLRUMap adrCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("archive.maxCache"), AgnUtils.getDefaultIntValue("archive.maxCacheTimeMillis"));
    public static TimeoutLRUMap subjectCache=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("archive.maxCache"), AgnUtils.getDefaultIntValue("archive.maxCacheTimeMillis"));

    /** Creates new ActionOperationUpdateCustomer */
    public GetArchiveMailing() {
    }

    /** Executes ActionOperation  */
    public boolean executeOperation(Connection dbConn, int companyID, int customerID, int callerMailingID, HttpServletRequest aReq) {

        // do nothing, deprecated
        return false;
    }

    /**
     *
     * @param req
     * @param index
     * @return
     */
    public boolean buildOperationFromRequest(ServletRequest req, int index) {
        boolean exitValue=true;

        return exitValue;
    }

    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
    }

    public boolean executeOperation(Connection dbConn, int companyID, Map params) {
        return false;
    }

    /**
     *
     * @param dbConn
     * @param companyID
     * @param params
     * @return
     */
    public boolean executeOperation(ApplicationContext con, int companyID, Map params) {
        Integer tmpNum=null;
        int customerID=0;
        boolean returnValue=false;
        int tmpMailingID=0;
        MailingDao mDao=(MailingDao) con.getBean("MailingDao");
        String key=null;
        String archiveHtml=null;
        String archiveSubject=null;
        String archiveSender=null;

        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        } else {
            return returnValue;
        }

        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            tmpMailingID=tmpNum.intValue();
        } else {
            return returnValue;
        }

        key = companyID + "_" + tmpMailingID + "_" + customerID;
        
        
        
        
        archiveHtml=(String)mailingCache.get(key);
        archiveSender=(String)adrCache.get(key);
        archiveSubject=(String)subjectCache.get(key);

        if (archiveHtml == null || archiveSender == null || archiveSubject == null) {

            try {

                Hashtable<String, Object> previewResult = generateBackEndPreview(tmpMailingID, customerID, con);
                if (mDao.exist(tmpMailingID,companyID)) {
                    archiveHtml = (String) previewResult.get(Preview.ID_HTML);
                } else {
                    archiveHtml = SafeString.getLocaleString("mailing.content.not.avaliable",(Locale)params.get("locale"));
                }
                String header = (String) previewResult.get(Preview.ID_HEAD);
                if (header != null) {
                    archiveSender = PreviewHelper.getFrom(header);
                    archiveSubject = PreviewHelper.getSubject(header);
                }

                mailingCache.put(key, archiveHtml);
                adrCache.put(key, archiveSender);
                subjectCache.put(key, archiveSubject);
                returnValue = true;
            } catch (Exception e) {
                AgnUtils.logger().error("archive problem: " + e);
                AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                returnValue = false;
            }

        } else {
            returnValue=true;
        }

        if(returnValue) {
            params.put("archiveHtml", archiveHtml);
            params.put("archiveSender", archiveSender);
            params.put("archiveSubject", archiveSubject);
        }
        return returnValue;
    }
    
    protected Hashtable<String, Object> generateBackEndPreview(int mailingID,int customerID, ApplicationContext con) {
		PreviewFactory previewFactory = (PreviewFactory) con.getBean("PreviewFactory");
		Preview preview = previewFactory.createPreview();
		Hashtable<String,Object> output = preview.createPreview (mailingID,customerID, false);
		preview.done();
		return output;
	}
    
    
}
