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
<%@ page import="org.agnitas.cms.web.CmsMailingContentAction" %>
<%@ page import="org.agnitas.cms.web.forms.CmsMailingContentForm" %>
<%@ page import="org.agnitas.cms.webservices.generated.ContentModule" %>
<%@ page import="org.agnitas.cms.webservices.generated.ContentModuleLocation" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<% CmsMailingContentForm form = (CmsMailingContentForm) session.getAttribute("mailingContentForm"); %>
<% List<ContentModule> cmList = form.getAvailableContentModules(); %>
<% int MAX_CM_COUNT = CmsMailingContentAction.CM_LIST_SIZE; %>
<% pageContext.setAttribute("MAX_CM_COUNT", MAX_CM_COUNT); %>

<script type="text/javascript"
        src="<%=request.getContextPath()%>/js/prototype.js"></script>
<script type="text/javascript">
    var maxCmCount = parseInt(${MAX_CM_COUNT});
    function moveCMListUp() {
        var firstVisibleIndex = parseInt($('cm_list_begin').value);
        var cmToShow = $('cm_' + (firstVisibleIndex - 1));
        if(cmToShow != null) {
            $('cm_list').insertBefore(cmToShow, $('cm_' + firstVisibleIndex));
            $('hidden_cm_container').appendChild($('cm_' + (firstVisibleIndex + maxCmCount - 1)));
            $('cm_list_begin').value = firstVisibleIndex - 1;
        }
    }
    function moveCMListDown() {
        var firstVisibleIndex = parseInt($('cm_list_begin').value);
        var cmToShow = $('cm_' + (firstVisibleIndex + maxCmCount));
        if(cmToShow != null) {
            $('cm_list').insertBefore(cmToShow, $('move_down_button'));
            $('hidden_cm_container').appendChild($('cm_' + firstVisibleIndex));
            $('cm_list_begin').value = firstVisibleIndex + 1;
        }
    }
</script>

<input type="hidden" id="cm_list_begin" value="0">

<table bgcolor="#EEEEEE">
    <% int cmCount = 0; %>
    <tbody id="cm_list">

    <c:if test="${availableCmCount > MAX_CM_COUNT}">
        <tr id="move_up_dutton">
            <td style="text-align:center">
                <img src="${emmLayoutBase.imagesURL}/cm_list_up.gif"
                     alt="<bean:message key="moveUp" bundle="cmsbundle"/>"
                     onclick="moveCMListUp();"
                     style="cursor: pointer;">
            </td>
        </tr>
    </c:if>

    <% for(int i = 0; i < MAX_CM_COUNT && i < cmList.size(); i++) { %>
    <% ContentModule cm = cmList.get(i); %>
    <tr id="cm_<%= i %>" class="cmRow">
        <td>
            <controls:cmPreviewPanel cmId="<%= String.valueOf(cm.getId()) %>"
                                     cmName="<%= cm.getName() %>"/>
        </td>
    </tr>
    <% cmCount++; %>
    <% } %>

    <c:if test="${availableCmCount > MAX_CM_COUNT}">
        <tr id="move_down_button">
            <td style="text-align:center">
                <img src="${emmLayoutBase.imagesURL}/cm_list_down.gif"
                     alt="<bean:message key="moveDown" bundle="cmsbundle"/>"
                     onclick="moveCMListDown();"
                     style="cursor: pointer;">
            </td>
        </tr>
    </c:if>

    </tbody>
</table>

<div style="visibility:hidden; width:1px; height:1px;">
    <table style="visibility:hidden; width:1px; height:1px; display:none">
        <tbody id="hidden_cm_container">
        <% for(int i = cmCount; i < cmList.size(); i++) { %>
        <% ContentModule cm = cmList.get(i); %>
        <tr id="cm_<%= i %>" class="cmRow">
            <td>
                <controls:cmPreviewPanel
                        cmId="<%= String.valueOf(cm.getId()) %>"
                        cmName="<%= cm.getName() %>"/>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>




