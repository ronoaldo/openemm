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
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.agnitas.actions.ActionOperation;
import org.agnitas.beans.Recipient;
import org.agnitas.util.AgnUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author  mhe
 * @version
 */
public class ServiceMail extends ActionOperation implements Serializable {

	private static final long serialVersionUID = -233579948324840094L;

	/**
     * Holds value of property textMail.
     */
    protected String textMail="";

    /**
     * Holds value of property subjectLine.
     */
    protected String subjectLine="";

    /**
     * Holds value of property toAdr.
     */
    protected String toAdr="";

    /** Creates new ServiceMail */
    public ServiceMail() {
    }

    /**
     *
     * @param req
     * @param index
     * @return
     */
    /**
    public boolean buildOperationFromRequest(ServletRequest req, int index) {
        boolean exitValue=true;

        this.toAdr=req.getParameter("op_adr"+index);
        if(this.toAdr==null) {
            return false;
        }

        this.textMail=req.getParameter("op_text"+index);
        if(this.textMail==null) {
            return false;
        }

        this.subjectLine=req.getParameter("op_subject"+index);
        if(this.subjectLine==null) {
            return false;
        }

        this.htmlMail=req.getParameter("op_html"+index);

        try {
            this.mailtype=Integer.parseInt(req.getParameter("op_mailtype"+index));
        } catch (Exception e) {
            this.mailtype=0;
        }

        return exitValue;
    }
    */

    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;

        allFields=in.readFields();
        this.textMail=(String)allFields.get("textMail", "");
        this.toAdr=(String)allFields.get("toAdr", "");
        this.subjectLine=(String)allFields.get("subjectLine", "");
        this.htmlMail=(String)allFields.get("htmlMail", "");
        if(this.htmlMail==null) {
            this.htmlMail = "";
        }
        this.mailtype=allFields.get("mailtype", 0);
    }

    public boolean executeOperation(ApplicationContext con, int companyID, Map params) {
        // String email_from; // =(String)params.get("from");  comes from customer-record...
        HashMap request=(HashMap)params.get("requestParameters");
        String toAdr = "";


        if(params.get("sendServiceMail")!=null && params.get("sendServiceMail").equals("no")) {
            return true; // do nothing, manually blocked
        }

        if(params.get("sendServiceMailToAdr")!=null) {
            toAdr=(String)params.get("sendServiceMailToAdr");
        } else {
            toAdr=this.toAdr;
        }

        // check sender
        Recipient fromCust=(Recipient) con.getBean("Recipient");

        fromCust.setCompanyID(companyID);
        fromCust.loadCustDBStructure();

        if(params.get("customerID")!=null) {
            Integer tmpNum=(Integer)params.get("customerID");
            fromCust.setCustomerID(tmpNum.intValue());
            if(fromCust.getCustomerID()!=0) {
                fromCust.getCustomerDataFromDb();
            }
        }

        if(fromCust.getCustomerID()==0) {
            String tmpMail=null;
            if(request.get("fromEmail")!=null) {
                tmpMail=(String)request.get("fromEmail");
            }
            if(params.get("sendServiceMailFromAdr")!=null) {
                tmpMail=(String)params.get("sendServiceMailFromAdr");
            }
            if(tmpMail!=null) {
                tmpMail=tmpMail.trim().toLowerCase();
                if(!AgnUtils.checkEmailAdress(tmpMail)) {
                    return false;
                }
                fromCust.setCustParameters("email", tmpMail);
            } else {
                return false;
            }
        }

        if(fromCust.blacklistCheck()) {
            return false;
        }

        StringWriter aWriter=new StringWriter();
        String emailtext = "";
        String emailhtml = "";
        String subject;

        try {
            Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            Velocity.setProperty("runtime.log", AgnUtils.getDefaultValue("system.logdir")+"/velocity.log");
            Velocity.init();
            Velocity.evaluate(new VelocityContext(params), aWriter, null, this.textMail);
            emailtext=aWriter.toString();

            aWriter=new StringWriter();
            Velocity.evaluate(new VelocityContext(params), aWriter, null, this.subjectLine);
            subject=aWriter.toString();

            if(this.mailtype!=0) {
                aWriter=new StringWriter();
                Velocity.evaluate(new VelocityContext(params), aWriter, null, this.htmlMail);
                emailhtml=aWriter.toString();
            }
        } catch(Exception e) {
        	AgnUtils.logger().error("velocity error: "+e);
        	AgnUtils.logger().error(AgnUtils.getStackTrace(e));
            return false;
        }

        // Mail verschicken
        if(AgnUtils.sendEmail(fromCust.getCustParameters("email"), toAdr, subject, emailtext, emailhtml, this.mailtype, "iso-8859-1")==false) {
            return false;
        }

        return true;
    }

    /**
     * Getter for property textMail.
     * @return Value of property textMail.
     */
    public String getTextMail() {
        return this.textMail;
    }

    /**
     * Setter for property textMail.
     * @param textMail New value of property textMail.
     */
    public void setTextMail(String textMail) {
        this.textMail = textMail;
    }

    /**
     * Getter for property subjectLine.
     * @return Value of property subjectLine.
     */
    public String getSubjectLine() {
        return this.subjectLine;
    }

    /**
     * Setter for property subjectLine.
     * @param subjectLine New value of property subjectLine.
     */
    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
    }

    /**
     * Getter for property toAdr.
     * @return Value of property toAdr.
     */
    public String getToAdr() {
        return this.toAdr;
    }

    /**
     * Setter for property toAdr.
     * @param toAdr New value of property toAdr.
     */
    public void setToAdr(String toAdr) {
        this.toAdr = toAdr;
    }

    private int mailtype = 0;

    private String htmlMail;

    /**
     * Getter for property mailtype.
     * @return Value of property mailtype.
     */
    public int getMailtype() {

        return this.mailtype;
    }

    /**
     * Setter for property mailtype.
     * @param mailtype New value of property mailtype.
     */
    public void setMailtype(int mailtype) {

        this.mailtype = mailtype;
    }

    /**
     * Getter for property htmlMail.
     * @return Value of property htmlMail.
     */
    public String getHtmlMail() {

        return this.htmlMail;
    }

    /**
     * Setter for property htmlMail.
     * @param htmlMail New value of property htmlMail.
     */
    public void setHtmlMail(String htmlMail) {

        this.htmlMail = htmlMail;
    }

}
