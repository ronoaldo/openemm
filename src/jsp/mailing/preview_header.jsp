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
 --%><%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<agn:CheckLogon/>

<bean:message key="ecs.From"/>:&nbsp;<b><bean:write name="mailingSendForm" property="senderPreview"/></b><br>
<bean:message key="mailing.Subject"/>:&nbsp;<b><bean:write name="mailingSendForm" property="subjectPreview"/></b>

<c:forEach var="component" items="${components}" varStatus="status">
    <agn:CustomerMatchTarget customerID="${mailingSendForm.previewCustomerID}"
                             targetID="${component.targetID}">
        <c:if test="${status.first}">
            <br><br>
            <b><bean:message key="mailing.Attachments"/>:</b><br>
        </c:if>
        <html:link
                page="/sc?compID=${component.id}&mailingID=${mailingSendForm.mailingID}&customerID=${mailingSendForm.previewCustomerID}">${component.componentName}&nbsp;&nbsp;<img src="${emmLayoutBase.imagesURL}/download.gif" border="0" alt="<bean:message
                key='button.Download'/>"></html:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    </agn:CustomerMatchTarget>
</c:forEach>
<br><br>