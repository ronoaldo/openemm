<%@ page language="java" import="org.agnitas.util.AgnUtils, org.agnitas.dao.TargetDao, org.springframework.context.ApplicationContext, org.springframework.web.context.support.WebApplicationContextUtils, org.agnitas.target.Target, org.agnitas.web.TargetForm" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.TargetAction" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<% int tmpTargetID=0;
   String tmpShortname=new String("");

    if(request.getParameter("targetID")!=null) {
    	tmpTargetID = Integer.parseInt( request.getParameter("targetID"));
    	request.setAttribute("tmpTargetID", tmpTargetID);
    	ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(application);
        TargetDao dao = (TargetDao) aContext.getBean("TargetDao");
        Target aTarget= dao.getTarget(Integer.parseInt(request.getParameter("targetID")), AgnUtils.getCompanyID(request));
        if(aTarget != null) {
            tmpShortname = aTarget.getTargetName();
   		 }
    }
    if(request.getAttribute("targetForm")!=null) {
		tmpTargetID=((TargetForm)request.getAttribute("targetForm")).getTargetID();
        request.setAttribute("tmpTargetID", tmpTargetID);
	    tmpShortname=((TargetForm)request.getAttribute("targetForm")).getShortname();
	}
%>


<% request.setAttribute("sidemenu_active", new String("Targetgroups")); %>
<% if(tmpTargetID!=0) {
     request.setAttribute("sidemenu_sub_active", new String("none"));
   } else {
     request.setAttribute("sidemenu_sub_active", new String("target.NewTarget"));
   }
%>
<% request.setAttribute("agnTitleKey", new String("target.Target")); %>
<% request.setAttribute("agnSubtitleKey", new String("target.Target")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("targetView")); %>
<% request.setAttribute("agnHighlightKey", new String("target.NewTarget")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&targetID="+tmpTargetID)); %>
<% request.setAttribute("agnHelpKey", new String("targetGroupView")); %>
<% request.setAttribute("ACTION_VIEW", TargetAction.ACTION_VIEW); %>
