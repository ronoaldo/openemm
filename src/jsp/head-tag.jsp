<%-- checked --%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<head>
    <% if(request.getAttribute("agnRefresh") != null) { %>
    <meta http-equiv="refresh" content='<%= (String)(request.getAttribute("agnRefresh")) %>'>
    <meta http-equiv="Page-Exit" content="RevealTrans(Duration=1,Transition=1)">
    <% } %>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>

    <title>
    	<c:if test="${not empty agnPluginId}">
	    	<agn:message key="${agnTitleKey}" plugin="${agnPluginId}" /> - <agn:message key="${agnHighlightKey}" plugin="${agnPluginId}" /> 			    	
    	</c:if>
    	<c:if test="${empty agnPluginId}">
	        <bean:message key='<%= (String)(request.getAttribute("agnTitleKey")) %>'/> - <bean:message key='<%= (String)(request.getAttribute("agnHighlightKey")) %>'/>
    	</c:if>
    </title>

    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/style.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/structure.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/displaytag.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/ie7.css">

    <link type="text/css" rel="stylesheet" href="styles/tooltiphelp.css">
    <link type="text/css" rel="stylesheet" href="styles/reportstyles.css">
    <link type="text/css" rel="stylesheet" href="styles/datepicker.css">
    <link type="text/css" rel="stylesheet" href="styles/lightbox.css">
    <link type="text/css" rel="stylesheet" href="styles/cms_displaytag.css">
    <link rel="shortcut icon" href="favicon.ico"/>
</head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_crossframe.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_iframe.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_hide.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/overlibmws/overlibmws_shadow.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax/ajaxtags.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax/ajaxtags_controls.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/ajax/ajaxtags_parser.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/aa.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/helpballoon/HelpBalloon.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calendar/CalendarPopup.js"></script>
<script type="text/javascript" src="js/prototype-extensions/prototype-base-extensions.js"></script>
<script type="text/javascript" src="js/prototype-extensions/prototype-date-extensions.js"></script>
<script type="text/javascript" src="js/datepicker/datepicker.js" ></script>
