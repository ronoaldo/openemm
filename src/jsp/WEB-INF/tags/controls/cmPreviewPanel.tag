<%@ tag import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<%@ attribute name="cmId" %>
<%@ attribute name="cmName" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table cellpadding="2">
    <tr>
        <td height="4px"/>
    </tr>
    <tr>
        <td>
            <span class="simple-text">
                ${cmName}
            </span>
        </td>
        <td style="text-align:right;">
            <input type="image"
                   src="${emmLayoutBase.imagesURL}/add_cm.png"
                   name="addCM_${cmId}" value="${cmId}"/>
        </td>
    </tr>
    <tr>
        <td align="center" colspan="2" style="text-align:left">
            <table cellpadding="0" cellspacing="0">
                <tr>
                    <td style="border: 1px solid #888; background-color:#FFF; padding: 0;
                        text-align:center; vertical-align:middle;
                        width: <%= ContentModuleAction.PREVIEW_MAX_WIDTH %>px;
                        height: <%= ContentModuleAction.PREVIEW_MAX_HEIGHT + 2 %>px;">
                        <img src="<html:rewrite page="/cms_image?cmId=${cmId}&preview=true"/>"
                             alt="<bean:message key="mailing.Preview"/>"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>