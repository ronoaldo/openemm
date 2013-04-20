<%@ page import="org.agnitas.cms.web.*" %>
<%@ page import="org.agnitas.cms.web.forms.*" %>
<%@ page import="org.agnitas.cms.webservices.generated.*" %>
<%@ page import="java.util.*" %>
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
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<% CmsMailingContentForm aForm = (CmsMailingContentForm) session
        .getAttribute("mailingContentForm"); %>

<head>
    <script type="text/javascript"
            src="<%= request.getContextPath() %>/js/cms/editor.js"></script>
    <link type="text/css" rel="stylesheet" href="styles/cms_editor.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/style.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/structure.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/displaytag.css">
    <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/ie7.css">
    <%= aForm.getTemplateHead() %>
</head>

<body onload="initCmPositions();" style="background: none;">

<script type="text/javascript">
    function submitTheForm(){
        document.mailingContentForm.submit();
    }
</script>

<agn:CheckLogon/>
<agn:Permission token="cms.mailing_content_management"/>

<html:form action="/mailingcontent">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>

    <input name="cmToEdit" id="cmToEdit" value="0" type="hidden"/>
    <input name="phForNewCM" id="phForNewCM" value="" type="hidden"/>

    <% List<ContentModuleLocation> locations = aForm.getContentModuleLocations();
        for (ContentModuleLocation location : locations) { %>
    <input name="cm.<%=location.getContentModuleId()%>.ph_name"
           id="cm.<%=location.getContentModuleId()%>.ph_name" type="hidden"
           value="<%=location.getDynName()%>"/>
    <input name="cm.<%=location.getContentModuleId()%>.order_in_ph"
           id="cm.<%=location.getContentModuleId()%>.order_in_ph" type="hidden"
           value="<%=location.getOrder()%>"/>
    <% }
        String[] placeholders = aForm.getPlaceholders();
        for (int i = 0; i < placeholders.length; i++) {
            String placeholderName = placeholders[i];
    %>
    <input id="ph_name.<%= i %>" type="hidden" value="<%= placeholderName %>"/>
    <input id="ph.<%= placeholderName %>" type="hidden" value="<%= i %>"/>
    <% } %>

    <input id="ph_max" type="hidden"
           value="<%= aForm.getPlaceholders().length - 1%>"/>

    <div style="visibility:hidden; width:1px; height:1px;">
        <table>
            <tbody>
            <% for (ContentModuleLocation location : locations) { %>
            <controls:cmPanel cmId="<%= String.valueOf(location.getContentModuleId()) %>"
                              cmContent="<%= aForm.getContentModules().get(location.getContentModuleId())%>"
                              phName="<%= location.getDynName() %>"
                              phOrder="<%= String.valueOf(location.getOrder()) %>"
                              targetId="<%= String.valueOf(location.getTargetGroupId()) %>"/>
            <% } %>
            </tbody>
        </table>
    </div>

    <table cellspacing="0" width="100%">
        <tr>
            <td valign="top" width="100%">
                <%= aForm.getTemplateBody() %>
            </td>
            <td valign="top" align="center" class="simple-text cms_editor_right_panel" >
                <div class="category_select_box" >
                    <div class="category_label">
                        <bean:message key="cms.Category"/>:
                    </div>
                    <html:select property="categoryToShow" size="1" styleClass="category_dropdown">
                        <html:option value="0">
                            &lt;<bean:message key="none"/>&gt;
                        </html:option>
                        <c:forEach var="cmCategory" items="${mailingContentForm.allCategories}">
                            <html:option value="${cmCategory.id}">
                                ${cmCategory.name}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>
                <div class="button_container category_show_button_panel">
                    <input type="hidden" id="show" name="show" value=""/>
                    <div class="action_button button_ie">
                        <a href="#" onclick="document.mailingContentForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a>
                    </div>
                    <div class="action_button"><bean:message key="mailing.Content"/>:</div>
                </div>


                <%@ include file="/cms/mailing/cm_list.jsp" %>
            </td>
        </tr>
    </table>

</html:form>
</body>
