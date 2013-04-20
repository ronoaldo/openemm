<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/domain_stats">
    <html:hidden property="action"/>
    <div class="loading_container">
        <table border="0" cellspacing="0" cellpadding="0" width="400">
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <img border="0" width="44" height="48"
                         src="${emmLayoutBase.imagesURL}/wait.gif"/>
                </td>
            </tr>
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <b><bean:message key="statistic.StatSplashMessage"/><b>
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

