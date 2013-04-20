<%@ page language="java" import="org.agnitas.beans.Mailing, org.agnitas.web.MailingBaseAction, org.agnitas.web.MailingWizardAction, org.agnitas.web.MailingWizardForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=null;
   aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
   Mailing mailing=aForm.getMailing();
%>

<agn:Permission token="mailing.show"/>

<c:set var="sidemenu_active" value="Mailings" scope="request"/>
<c:set var="sidemenu_sub_active" value="mailing.New_Mailing" scope="request"/>
<c:set var="agnNavigationKey" value="MailingNew" scope="request"/>
<c:set var="agnHighlightKey" value="mailing.New_Mailing" scope="request"/>
<c:set var="agnTitleKey" value="Mailing" scope="request"/>
<c:set var="agnSubtitleKey" value="Mailing" scope="request"/> 

<c:set var="ACTION_NEW" value="<%= MailingBaseAction.ACTION_NEW %>" scope="request"/>
<c:set var="ACTION_START" value="<%= MailingWizardAction.ACTION_START %>" scope="request"/>
<% request.setAttribute("agnHelpKey", new String("createNewMailing")); %>

