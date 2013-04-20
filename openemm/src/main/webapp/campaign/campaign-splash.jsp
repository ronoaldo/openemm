<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>

<!-- The following part is for reloading the page and as soon as possible move
on to the Result-Page of the Database request. -->
<script>
    /* calls the submit-procedure */
    function go() {
        document.getElementsByName('campaignForm')[0].submit();
    }

    /* this method returns the zones which shall be reloaded. */
    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    /* this method is our starting-point */
    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${campaignForm.error }) {
            window.setTimeout("go();", ${campaignForm.refreshMillis});
        } else {
            System.err.println("Fehler: " + campaignForm.error);
        }
    }

    /* We do not have any loading message */
    ajaxAnywhere.showLoadingMessage = function() {
    };

    /* call it.*/
    ajaxAnywhere.onAfterResponseProcessing();
</script>

<aa:zone name="loading"> <!-- all within these tags will be reloaded, nothing more -->
    <html:form action="/campaign">
        <html:hidden property="action"/>
        <html:hidden property="campaignID"/>
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
</aa:zone>      
                     
