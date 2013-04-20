<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   int isTemplate=0;
   MailingBaseForm aForm=null;
   if(session.getAttribute("mailingBaseForm")!=null) {
      aForm=(MailingBaseForm)session.getAttribute("mailingBaseForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
      if(aForm.isIsTemplate()) {
         isTemplate=1;
      }
   }
   request.setAttribute("tmpShortname", tmpShortname);
   request.setAttribute("tmpMailingID", tmpMailingID);
   request.setAttribute("isTemplate", isTemplate);
%>

<% if(isTemplate==0) { %>
<agn:Permission token="mailing.delete"/>
<% } else { %>
<agn:Permission token="template.delete"/>
<% } %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% // template navigation:
  request.setAttribute("sidemenu_active", new String("Mailings"));
  request.setAttribute("sidemenu_sub_active", new String("none"));
  request.setAttribute("agnNavigationKey", new String("templateDelete"));
  request.setAttribute("agnHighlightKey", new String("Template"));
  request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  request.setAttribute("agnTitleKey", new String("Template"));
  request.setAttribute("agnSubtitleKey", new String("Template"));
  request.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<%
// mailing navigation:
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("sidemenu_sub_active", new String("none"));
    request.setAttribute("agnNavigationKey", new String("mailingDelete"));
    request.setAttribute("agnHighlightKey", new String("Mailing"));
    request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    request.setAttribute("agnSubtitleValue", tmpShortname);
    request.setAttribute("agnTitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleKey", new String("Mailing"));
%>
</logic:equal>