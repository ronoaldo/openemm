<%@ page language="java" import="org.agnitas.util.AgnUtils, org.agnitas.web.forms.AdminForm" contentType="text/html; charset=utf-8" buffer="64kb" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="admin.show"/>

<%
   int tmpAdminID = 0;
   int tmpCompID = 0;
   String tmpUser = "";

   tmpCompID=AgnUtils.getCompanyID(request);
   if(request.getAttribute("adminForm")!=null) {
      tmpUser=((AdminForm)request.getAttribute("adminForm")).getUsername();   
      tmpAdminID=((AdminForm)request.getAttribute("adminForm")).getAdminID();
      request.setAttribute("userrights", ((AdminForm)request.getAttribute("adminForm")).getUserRights());
      request.setAttribute("grouprights", ((AdminForm)request.getAttribute("adminForm")).getGroupRights());
   }
%>

<% request.setAttribute("agnSubtitleKey", new String("settings.Admin")); %>              <!-- ueber rechte Seite -->
<% request.setAttribute("sidemenu_active", new String("Administration")); %>          <!-- links Button -->
<% request.setAttribute("sidemenu_sub_active", new String("settings.Admins")); %>        <!-- links unter Button -->
<% request.setAttribute("agnTitleKey", new String("settings.Admins")); %>                <!-- Titelleiste -->
<% request.setAttribute("agnNavigationKey", new String("admin")); %>            <!-- Karteileiste -->
<% request.setAttribute("agnHighlightKey", new String("UserRights")); %>        <!-- markiertes Element -->


<% request.setAttribute("agnSubtitleValue", tmpUser); %>
<% request.setAttribute("agnNavHrefAppend", new String("&adminID="+tmpAdminID)); %>
<% request.setAttribute("agnHelpKey", new String("userRights")); %>
