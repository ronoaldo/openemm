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
 * the code written by AGNITAS AG are Copyright (c) 2010 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.ContentModuleTypeAction, org.agnitas.cms.web.forms.ContentModuleTypeForm, org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleCategoryForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="ContentManagement" scope="page" />
<c:set var="sidemenu_sub_active" value="CMCategories" scope="page" />
<c:set var="agnTitleKey" value="ContentManagement" scope="page" />
<c:set var="agnSubtitleKey" value="CMCategories" scope="page" />
<c:set var="agnSubtitleValue" value="${contentModuleCategoryForm.name}" scope="page" />
<c:set var="agnNavigationKey" value="CMCEdit" scope="page" />
<c:set var="agnHighlightKey" value="CMCategory" scope="page" />

<c:choose>
	<c:when test="${contentModuleCategoryForm.fromListPage}">
		<c:set var="cancelAction" value="<%= ContentModuleTypeAction.ACTION_LIST %>" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="cancelAction" value="<%= ContentModuleTypeAction.ACTION_VIEW %>" scope="page" />
	</c:otherwise>
</c:choose>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<span class="head1">${contentModuleCategoryForm.name}</span><br/><br/>
<span class="head3"><bean:message key="DeleteCMCategoryQuestion" bundle="cmsbundle"/></span><br>

<p>
    <html:form action="/cms_cmcategory.do">
        <html:hidden property="cmcId"/>
        <html:hidden property="action"/>
        <html:image src="button?msg=Delete" property="kill" value="kill"/>
        <html:link
                page="/cms_cmcategory.do?action=${cancelAction}&cmcId=${contentModuleCategoryForm.cmcId}"><html:img
                src="button?msg=Cancel" border="0"/></html:link>
    </html:form>
</p>

<%@include file="/footer.jsp" %>
