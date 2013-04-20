<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.Admin, java.util.*" contentType="text/html; charset=utf-8" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="admin.show"/>

<%
   int tmpAdminID=0;
   String tmpUsername=new String("");
   if(request.getAttribute("adminForm")!=null) {
      tmpAdminID=((AdminForm)request.getAttribute("adminForm")).getAdminID();
      tmpUsername=((AdminForm)request.getAttribute("adminForm")).getUsername();
   }
   request.setAttribute("tmpAdminID", tmpAdminID);
%>


<% request.setAttribute("sidemenu_active", new String("Administration")); %>          <!-- links Button -->
<% request.setAttribute("sidemenu_sub_active", new String("settings.Admins")); %>        <!-- links unter Button -->
<% request.setAttribute("agnTitleKey", new String("settings.Admins")); %>                <!-- Titelleiste -->


<% if(tmpAdminID!=0) {
     request.setAttribute("agnSubtitleKey", new String("settings.Admin"));
     request.setAttribute("agnNavigationKey", new String("admin"));
     request.setAttribute("agnHighlightKey", new String("settings.Edit_Admin"));
   } else {
     request.setAttribute("agnSubtitleKey", new String("settings.Admins"));
     request.setAttribute("agnNavigationKey", new String("admins"));
     request.setAttribute("agnHighlightKey", new String("settings.New_Admin"));
   } 
%>


<% request.setAttribute("agnSubtitleValue", tmpUsername); %>
<% request.setAttribute("agnNavHrefAppend", new String("&adminID="+tmpAdminID)); %>
<% request.setAttribute("agnHelpKey", new String("newUser")); %>