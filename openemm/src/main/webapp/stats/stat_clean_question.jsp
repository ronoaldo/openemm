<%--
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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.beans.*, org.agnitas.util.*, org.agnitas.web.*, java.util.* " %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpMailingID=0;
   //int tmpTargetID=0;
   //int tmpUniqueClicks=0;
   String tmpShortname=new String("");
   MailingStatForm aForm=null;
   if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      //tmpTargetID=aForm.getTempTargetID();
      tmpShortname=aForm.getMailingShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>

<%@include file="/header.jsp"%>
<%@include file="/messages.jsp" %>

    <table border="0" cellspacing="0" cellpadding="0">
        <html:form action="mailing_stat">

             <html:hidden property="mailingID"/>
             <html:hidden property="action"/>
             <html:hidden property="targetID"/>
             <html:hidden property="netto"/>

            <tr>
                <td><span class="head3"><bean:message key="statistic.DeleteAdminClicks"/></span></td>
            </tr>

            <tr>
                <td>&nbsp;&nbsp;<td>
            </tr>

            <tr>
                <td>&nbsp;&nbsp;<td>
            </tr>

            <tr>
                <td><b><bean:message key="statistic.AreYouSure"/></b><td>
            </tr>

            <tr>
                <td>&nbsp;&nbsp;<td>
            </tr>

            <tr>
                <td><html:link page='<%= new String( \"/mailing_stat.do?action=\" + MailingStatAction.ACTION_CLEAN + \"&mailingID=\"+tmpMailingID) %>'>
                    <html:img src="button?msg=button.OK" border="0"/></html:link>&nbsp;&nbsp;
                    <html:link page='<%= new String( \"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + tmpMailingID ) %>'>
                    <html:img src="button?msg=button.Cancel" border="0"/></html:link></td>
            </tr>

        </html:form>
    </table><%@include file="/footer.jsp"%>