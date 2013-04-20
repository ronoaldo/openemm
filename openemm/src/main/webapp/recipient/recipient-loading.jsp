<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.agnitas.web.forms.StrutsFormBase" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="WIDTH_STATE_WIDE" value="<%= StrutsFormBase.WIDTH_STATE_WIDE%>" scope="page"/>


<script>

    function go() {
        document.getElementsByName('recipientForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${recipientForm.error })
            window.setTimeout("go();", ${recipientForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>


<%
    RecipientForm recipient = (RecipientForm) session.getAttribute("recipientForm");
    recipient.setAction(RecipientAction.ACTION_LIST);
%>

<aa:zone name="loading">

    <html:form action="/recipient">
        <html:hidden property="action"/>
        <html:hidden property="error"/>

        <c:if test="${recipientForm.extendedWidthState == WIDTH_STATE_WIDE}">
            <link type="text/css" rel="stylesheet" href="${emmLayoutBase.baseURL}/styles/style_1760.css">
            <link type="text/css" rel="stylesheet" href="${emmLayoutBase.baseURL}/styles/structure_1760.css">
        </c:if>

        <div class="loading_container">
            <table border="0" cellspacing="0" cellpadding="0" width="400">
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <logic:equal value="false" name="recipientForm" property="error">
                            <img border="0" width="44" height="48"
                                 src="${emmLayoutBase.imagesURL}/wait.gif"/>
                        </logic:equal>
                        <logic:equal value="true" name="recipientForm" property="error">
                            <img border="0" width="29" height="30"
                                 src="${emmLayoutBase.imagesURL}/warning.gif"/>
                        </logic:equal>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>
                            <logic:equal value="false" name="recipientForm" property="error">
                                <bean:message key="default.loading"/>
                            </logic:equal>
                            <logic:equal value="true" name="recipientForm" property="error">
                                <bean:message key="default.loading.stopped"/>
                            </logic:equal>

                            <b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>

            </table>
        </div>
    </html:form>
</aa:zone>
