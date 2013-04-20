<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%>
 
 <%@ page language="java" import="java.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.apache.struts.action.*, org.agnitas.util.*, org.springframework.context.*, org.springframework.orm.hibernate3.*, org.springframework.web.context.support.WebApplicationContextUtils" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:html>

<head>
<title><bean:message key="logon.title"/></title>
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/style.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/structure.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/displaytag.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/ie7.css">
    <style>
        html, body {
          height: 100%;
          margin: 0 auto;
          padding: 0;
        }
        body {
          position: relative;
        }
    </style>
</head>
<body>

    <div class="login_page_root_container">
        <div class="login_page_top_spacer"></div>
        <div class="loginbox_container">

            <div class="loginbox_top"></div>

            <div class="loginbox_content">
                <img src="${emmLayoutBase.imagesURL}/logo.png" border="0" class="logon_image">
                <br>
                <span class="logon_page_emm_title"><bean:message key="logon.title"/></span>
            	
            	<div class="loginbox_row"><br/></div>
            	
            	<div class="loginbox_row">
 					<bean:message key="logout.successful" />
 				</div>
 				
            	<div class="loginbox_row">
            		<a href="logon.do"><bean:message key="logout.relogin" /></a>
 				</div>
            </div>

            <div class="loginbox_bottom"></div>
        </div>
    </div>

</body>
</html:html>
