<%@ page language="java" import="org.agnitas.web.forms.*" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.beans.MailingComponent" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.components.show"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
   MailingComponentsForm aForm=null;
   if(request.getAttribute("mailingComponentsForm")!=null) {
      aForm=(MailingComponentsForm)request.getAttribute("mailingComponentsForm");
      tmpMailingID=aForm.getMailingID();
      tmpShortname=aForm.getShortname();
   }
    request.setAttribute("tmpMailingID", tmpMailingID);
    request.setAttribute("isWorldMailingSend", aForm.isWorldMailingSend());
%>

<logic:equal name="mailingComponentsForm" property="isTemplate" value="true">
<% // template navigation:
  request.setAttribute("sidemenu_active", new String("Mailings"));
  request.setAttribute("sidemenu_sub_active", new String("none"));
  request.setAttribute("agnNavigationKey", new String("templateView"));
  request.setAttribute("agnHighlightKey", new String("mailing.Graphics_Components"));
  request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
  request.setAttribute("agnTitleKey", new String("Template"));
  request.setAttribute("agnSubtitleKey", new String("Template"));
  request.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>

<logic:equal name="mailingComponentsForm" property="isTemplate" value="false">
<%
// mailing navigation:
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("sidemenu_sub_active", new String("none"));
    request.setAttribute("agnNavigationKey", new String("mailingView"));
    request.setAttribute("agnHighlightKey", new String("mailing.Graphics_Components"));
    request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
    request.setAttribute("agnTitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleKey", new String("Mailing"));
    request.setAttribute("agnSubtitleValue", tmpShortname);
%>
</logic:equal>
<c:set var="agnHelpKey" value="pictureComponents" scope="request" />
<c:set var="MAILING_COMPONENT_TYPE_IMAGE" value="<%= MailingComponent.TYPE_IMAGE %>" scope="request" />
<c:set var="MAILING_COMPONENT_TYPE_HOSTED_IMAGE" value="<%= MailingComponent.TYPE_HOSTED_IMAGE %>" scope="request" />
<c:set var="COMPANY_ID" value="<%= AgnUtils.getCompanyID(request) %>" scope="request" />
