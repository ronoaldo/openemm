<%@ page language="java" import="org.agnitas.web.MailingWizardForm" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailingWizardAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<%	Integer tmpMailingID=0;
	MailingWizardForm aForm=null;
	// String permToken=null; wird nicht benutzt
	String tmpShortname=new String("");

	if((aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm"))!=null) {
       tmpMailingID=aForm.getMailing().getId();
       tmpShortname=aForm.getMailing().getShortname();
	}
    request.setAttribute("tmpMailingID",tmpMailingID);
    request.setAttribute("aForm",aForm);
%>
<agn:Permission token="mailing.show"/>

<%
// mailing navigation:
request.setAttribute("sidemenu_active", new String("Mailings"));
request.setAttribute("sidemenu_sub_active", new String("mailing.New_Mailing"));
request.setAttribute("agnNavigationKey", new String("MailingNew"));
request.setAttribute("agnHighlightKey", new String("mailing.New_Mailing"));
request.setAttribute("agnTitleKey", new String("Mailing"));
request.setAttribute("agnSubtitleKey", new String("Mailing"));
request.setAttribute("agnSubtitleValue", tmpShortname);
request.setAttribute("ACTION_FINISH", MailingWizardAction.ACTION_FINISH);
%>
<% request.setAttribute("agnHelpKey", new String("newMailingWizard")); %>
