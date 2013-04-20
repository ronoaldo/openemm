<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% 	int tmpMailingID=0;
	String tmpShortname=new String("");
   	MailingBaseForm aForm=null;
   	if(session.getAttribute("mailingBaseForm")!=null) {
    	aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm");
      	tmpShortname=aForm.getShortname();
      	tmpMailingID=aForm.getMailingID();
   	}

// mailing navigation:
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("sidemenu_sub_active", new String("none"));
    request.setAttribute("agnNavigationKey", new String("mailingView"));
    request.setAttribute("agnHighlightKey", new String("Mailing"));
    request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    request.setAttribute("agnSubtitleValue", tmpShortname);
    request.setAttribute("agnTitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleKey", new String("Mailing"));
%>
