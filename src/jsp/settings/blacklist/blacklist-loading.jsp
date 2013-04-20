<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.BlacklistAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>

<% pageContext.setAttribute("ACTION_LIST", BlacklistAction.ACTION_LIST); %>

<script>

    function go() {
        document.getElementsByName('blacklistForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${blacklistForm.error })
            window.setTimeout("go();", ${blacklistForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>


<aa:zone name="loading">
    <html:form action="/blacklist">
        <html:hidden property="action" value="${ACTION_LIST}"/>
        <html:hidden property="error"/>
        <div class="loading_container">
            <table border="0" cellspacing="0" cellpadding="0" width="400">
                <tr>
                    <td>
                        <b>&nbsp;<b>
                    </td>
                </tr>
                <tr>
                    <td>
                        <logic:equal value="false" name="blacklistForm" property="error">
                            <img border="0" width="44" height="48"
                                 src="${emmLayoutBase.imagesURL}/wait.gif"/>
                        </logic:equal>
                        <logic:equal value="true" name="blacklistForm" property="error">
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
                            <logic:equal value="false" name="blacklistForm" property="error">
                                <bean:message key="default.loading"/>
                            </logic:equal>
                            <logic:equal value="true" name="blacklistForm" property="error">
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
