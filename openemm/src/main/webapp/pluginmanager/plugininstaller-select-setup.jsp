<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.dao.*, org.agnitas.target.impl.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<agn:Permission token="pluginmanager.show"/>

<c:set var="sidemenu_active" value="Administration" scope="request" />
<c:set var="agnTitleKey" value="Administration" scope="request" />
<c:set var="agnSubtitleKey" value="settings.pluginmanager" scope="request" />
<c:set var="sidemenu_sub_active" value="settings.pluginmanager" scope="request" />
<c:set var="agnNavigationKey" value="pluginmanager" scope="request" />
<c:set var="agnHighlightKey" value="pluginmanager.install" scope="request" />
<c:set var="agnHelpKey" value="pluginmanagerList" scope="request" />

