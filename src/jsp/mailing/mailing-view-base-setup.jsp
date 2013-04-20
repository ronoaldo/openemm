<%@ page language="java" import="org.agnitas.web.forms.MailingBaseForm" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpMailingID=0;
   MailingBaseForm aForm=null;
   String tmpShortname=new String(""); 
   if((aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm"))!=null) {
      tmpMailingID=((MailingBaseForm)session.getAttribute("mailingBaseForm")).getMailingID();
      tmpShortname=((MailingBaseForm)session.getAttribute("mailingBaseForm")).getShortname();
   }
   if(aForm.isIsTemplate()) {
       aForm.setShowTemplate(true);
   }
    request.setAttribute("tmpMailingID",tmpMailingID);
%>

<% if(aForm.isIsTemplate()) { %>
<agn:Permission token="template.show"/>
<% } else { %>
<agn:Permission token="mailing.show"/>
<% } %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% // template navigation:
 request.setAttribute("sidemenu_active", new String("Mailings"));
  if(tmpMailingID!=0) {
     request.setAttribute("sidemenu_sub_active", new String("none"));
     request.setAttribute("agnNavigationKey", new String("templateView"));
     request.setAttribute("agnHighlightKey", new String("Template"));
     request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
     request.setAttribute("agnSubtitleValue", tmpShortname);
  } else {
     request.setAttribute("sidemenu_sub_active", new String("mailing.New_Template"));
     request.setAttribute("agnNavigationKey", new String("TemplateNew"));
     request.setAttribute("agnHighlightKey", new String("mailing.New_Template"));
  }
  request.setAttribute("agnTitleKey", new String("Template"));
  request.setAttribute("agnSubtitleKey", new String("Template"));
  request.setAttribute("agnHelpKey", new String("newTemplate"));
%>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<%
// mailing navigation:
   request.setAttribute("sidemenu_active", new String("Mailings"));
    if(tmpMailingID!=0) {
        request.setAttribute("sidemenu_sub_active", new String("none"));
        request.setAttribute("agnNavigationKey", new String("mailingView"));
        request.setAttribute("agnHighlightKey", new String("Mailing"));
        request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        request.setAttribute("agnSubtitleValue", tmpShortname);
    } else {
        request.setAttribute("sidemenu_sub_active", new String("mailing.New_Mailing"));
        request.setAttribute("agnNavigationKey", new String("MailingNew"));
        request.setAttribute("agnHighlightKey", new String("mailing.New_Mailing"));
    }
    request.setAttribute("agnTitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleKey", new String("Mailing"));
    request.setAttribute("agnHelpKey", new String("newMailingNormal"));
%>
</logic:equal>
