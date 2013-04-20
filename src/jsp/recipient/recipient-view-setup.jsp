<%@ page language="java" import="org.agnitas.web.*, org.agnitas.beans.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<agn:CheckLogon/>
<agn:Permission token="recipient.view"/>	 

<% 
    ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
    RecipientForm recipient=(RecipientForm) session.getAttribute("recipientForm");
    Recipient cust=(Recipient) aContext.getBean("Recipient");
    if(recipient == null) {
        recipient=new RecipientForm();
    }

    request.setAttribute("cust", cust);
    request.setAttribute("recipient", recipient);
%>
<%
if(recipient.getRecipientID()!=0) {
     request.setAttribute("sidemenu_sub_active", new String("none"));
     request.setAttribute("agnNavigationKey", new String("subscriber_editor"));
     request.setAttribute("agnHighlightKey", new String("recipient.RecipientEdit"));
     request.setAttribute("agnHelpKey", new String("recipientView"));
  } else {
     request.setAttribute("sidemenu_sub_active", new String("recipient.New_Recipient"));
     request.setAttribute("agnNavigationKey", new String("RecipientNew"));
     request.setAttribute("agnHighlightKey", new String("recipient.NewRecipient"));
     request.setAttribute("agnHelpKey", new String("newRecipient"));
  }

%>
<% request.setAttribute("sidemenu_active", new String("Recipients")); %>
<% request.setAttribute("agnTitleKey", new String("Recipients")); %>
<% request.setAttribute("agnSubtitleKey", new String("Recipients")); %>
<% request.setAttribute("agnSubtitleValue", recipient.getEmail()); %>
<% request.setAttribute("agnNavHrefAppend", new String("")); %>
<% request.setAttribute("ACTION_LIST", RecipientAction.ACTION_LIST); %>
<% request.setAttribute("ACTION_NEW", RecipientAction.ACTION_NEW); %>
<c:set var="ACTION_SAVE" value="<%= RecipientAction.ACTION_SAVE %>" scope="request" />

<script type="text/javascript">
<!--
 function cancel() {
 	document.getElementsByName('action')[0].value = ${ACTION_LIST};
 	document.getElementsByName()('recipientForm')[0].submit();
 }

function saveRecipient() {
 	document.getElementsByName('action')[0].value = ${ACTION_SAVE};
 	document.getElementsByName()('recipientForm')[0].submit();
 }

//-->
</script>
