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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
 --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ page import="org.agnitas.cms.web.ContentModuleTypeAction" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.cms.web.ContentModuleCategoryAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ include file="/cms/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% pageContext.setAttribute("sidemenu_active", "ContentManagement"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "CMCategories"); %>
<% pageContext.setAttribute("agnTitleKey", "ContentManagement"); %>
<% pageContext.setAttribute("agnSubtitleKey", "CMCategories"); %>
<% pageContext.setAttribute("agnNavigationKey", "CMCOverview"); %>
<% pageContext.setAttribute("agnHighlightKey", "Overview"); %>

<% pageContext.setAttribute("ACTION_VIEW", ContentModuleCategoryAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", ContentModuleCategoryAction.ACTION_CONFIRM_DELETE); %>
<% pageContext.setAttribute("ACTION_LIST", ContentModuleCategoryAction.ACTION_LIST); %>


<%@include file="/header.jsp" %>

<%@include file="/messages.jsp" %>

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <html:form action="/cms_cmcategory">
                <html:hidden property="action"/>
                <controls:rowNumber/>
            </html:form>
        </td>
    </tr>
    <tr>
        <td>
            <ajax:displayTag id="cmCategoryTable" ajaxFlag="displayAjax">
                <display:table class="dataTable" id="cmCategory" name="cmCategoryList"
                               pagesize="${contentModuleCategoryForm.numberofRows}"
                               requestURI="/cms_cmcategory.do?action=${ACTION_LIST}" excludedParams="*" sort="list"
                               defaultsort="1">
                    <display:column headerClass="head_name" class="cm_template_name" titleKey="default.Name"
                                    property="name" sortable="true" paramId="cmcId" paramProperty="id"
                                    url="/cms_cmcategory.do?action=${ACTION_VIEW}"/>
                    <display:column headerClass="head_description" class="description" titleKey="Description" property="description" sortable="true" paramId="categoryId" paramProperty="id"/>
                    <display:column class="cm_template_edit">
                        <html:link
                                    page="/cms_cmcategory.do?action=${ACTION_CONFIRM_DELETE}&cmcId=${cmCategory.id}&fromListPage=true"><img
                                    src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>delete.gif"
                                    alt="<bean:message key="button.Delete"/>" border="0"></html:link>

                        <html:link page="/cms_cmcategory.do?action=${ACTION_VIEW}&cmcId=${cmCategory.id}"><img
                                src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>revise.gif"
                                alt="<bean:message key="button.Edit"/>" border="0"></html:link>

                    </display:column>
                </display:table>
            </ajax:displayTag>
        </td>
    </tr>
</table>
<%@include file="/footer.jsp" %>

