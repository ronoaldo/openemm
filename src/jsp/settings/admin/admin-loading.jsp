<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.SalutationForm" %>
<%@ page import="org.agnitas.web.SalutationAction" %>
<%@ page import="org.agnitas.web.MailloopAction" %>
<%@ page import="org.agnitas.web.AdminAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>

<% pageContext.setAttribute("ACTION_LIST", AdminAction.ACTION_LIST); %>

<script>

    function go() {
        document.getElementsByName('adminForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${adminForm.error })
            window.setTimeout("go();", ${adminForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>


<aa:zone name="loading">

    <html:form action="/admin">
        <html:hidden property="action" value="${ACTION_LIST}"/>
        <html:hidden property="error"/>
        <html:hidden property="numberofRows" value="${adminForm.numberofRows}"/>
        <logic:iterate collection="${adminForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
        <div class="loading_container">
            <table border="0" cellspacing="0" cellpadding="0" width="400">
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <logic:equal value="false" name="adminForm" property="error">
                            <img border="0" width="44" height="48"
                                 src="${emmLayoutBase.imagesURL}/wait.gif"/>
                        </logic:equal>
                        <logic:equal value="true" name="adminForm" property="error">
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
                            <logic:equal value="false" name="adminForm" property="error">
                                <bean:message key="default.loading"/>
                            </logic:equal>
                            <logic:equal value="true" name="adminForm" property="error">
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
