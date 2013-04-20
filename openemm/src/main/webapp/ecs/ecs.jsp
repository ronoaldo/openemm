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
<%@ page import="org.agnitas.ecs.web.forms.EcsMailingStatForm" %>
<%@ page import="org.agnitas.ecs.backend.service.EmbeddedClickStatService" %>
<%@ page import="org.agnitas.ecs.EcsGlobals" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% int tmpMailingID=0;
   String tmpShortname=new String("");
    EcsMailingStatForm aForm = (EcsMailingStatForm) session.getAttribute("ecsForm");

   if(aForm != null) {
	  tmpMailingID = aForm.getMailingId();
      tmpShortname = aForm.getShortname();
   }
%>

<% pageContext.setAttribute("sidemenu_active", "Mailings"); %>
<% pageContext.setAttribute("sidemenu_sub_active", "none"); %>
<% pageContext.setAttribute("agnTitleKey", "Mailing"); %>
<% pageContext.setAttribute("agnSubtitleKey", "Mailing"); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", "mailingView"); %>
<% pageContext.setAttribute("agnHighlightKey", "Statistics"); %>
<% pageContext.setAttribute("agnNavHrefAppend", "&mailingID=" + tmpMailingID); %>

<% pageContext.setAttribute("GROSS_CLICKS", EcsGlobals.MODE_GROSS_CLICKS); %>
<% pageContext.setAttribute("NET_CLICKS", EcsGlobals.MODE_NET_CLICKS); %>
<% pageContext.setAttribute("PURE_MAILING", EcsGlobals.MODE_PURE_MAILING); %>

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:form action="/ecs_stat">
    <html:hidden property="mailingId"/>

    <table>
        <tr>

            <%-- View mode selector --%>
            <td valign="middle">
                <bean:message key="ecs.ViewMode"/>:
            </td>
            <td valign="middle">
                <html:select property="viewMode">
                    <html:option value="${GROSS_CLICKS}"><bean:message key="statistic.GrossClicks"/></html:option>
                    <html:option value="${NET_CLICKS}"><bean:message key="statistic.NetClicks"/></html:option>
                    <html:option value="${PURE_MAILING}"><bean:message key="ecs.PureMailing"/></html:option>
                </html:select>
            </td>

            <%-- Recipient selector --%>
            <td valign="middle">
                &nbsp;&nbsp;
                <bean:message key="Recipient"/>:
            </td>
            <td valign="middle">
                <html:select property="selectedRecipient">
                    <c:forEach var="recipient" items="${ecsForm.testRecipients}">
                        <html:option value="${recipient.key}">
                            ${recipient.value}
                        </html:option>
                    </c:forEach>
                </html:select>
            </td>

            <%-- Size selector --%>
            <td valign="middle">
                &nbsp;&nbsp;
                <bean:message key="default.Size"/>:
                <html:select property="frameSize" size="1">
                    <html:option value="4">640x480</html:option>
                    <html:option value="1">800x600</html:option>
                    <html:option value="2">1024x768</html:option>
                    <html:option value="3">1280x1024</html:option>
                </html:select>
            </td>

            <%-- Submit button --%>
            <td valign="middle">
                &nbsp;&nbsp;
                <html:image align="bottom" src="button?msg=Show" border="0" property="refresh_view"
                            value="refresh_view"/>
            </td>
        </tr>
    </table>
    <br>
    <table border="0" cellpadding="0" cellspacing="3" width="800px">
<tr><td class="content"><bean:message key="ecs.ColorCoding"/>:</td>
	<c:forEach var="color" items="${ecsForm.rangeColors}">
    <td bgcolor="#${color.color}" width="15px" style="border:1px solid #000;">&nbsp;</td><td class="content"><bean:message key="ecs.Heatmap.max"/>&nbsp;${color.rangeEnd}%</td>
	</c:forEach>
	</tr>
</table>
<br>
    <%-- Embedded click statistics view --%>
    <logic:empty name="ecsForm" property="heatmapErrors">
        <iframe src="${ecsForm.statServerUrl}/ecs_view?mailingId=${ecsForm.mailingId}&recipientId=${ecsForm.selectedRecipient}&viewMode=${ecsForm.viewMode}&companyId=${ecsForm.companyId}"
            id="ecs_frame" width="${ecsForm.frameWidth}" height="${ecsForm.frameHeight}"></iframe>
    </logic:empty>
    <logic:notEmpty name="ecsForm" property="heatmapErrors">
        <iframe src="?show_errors=1"
            id="ecs_frame" width="${ecsForm.frameWidth}" height="${ecsForm.frameHeight}"></iframe>
    </logic:notEmpty>
</html:form>
<br>
<bean:message key="ecs.Heatmap.description"/>
<%@include file="/footer.jsp" %>