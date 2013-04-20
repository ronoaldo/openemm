<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.NewImportWizardAction, org.agnitas.web.forms.NewImportWizardForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/ajaxanywhere.tld" prefix="aa" %>
<script>

    function go() {
        document.getElementsByName('newImportWizardForm')[0].submit();
    }

    ajaxAnywhere.getZonesToReload = function () {
        return "loading"
    };

    ajaxAnywhere.onAfterResponseProcessing = function () {
        if (! ${newImportWizardForm.error })
            window.setTimeout("go();", ${newImportWizardForm.refreshMillis});
    }
    ajaxAnywhere.showLoadingMessage = function() {
    };

    ajaxAnywhere.onAfterResponseProcessing();
</script>
<%
    NewImportWizardForm recipient = (NewImportWizardForm) session.getAttribute("newImportWizardForm");
    //recipient.setAction(NewImportWizardAction.ACTION_PROCEED);


    int barNetto = recipient.getImportWizardHelper().getCompletedPercent();
    int barFree = 101 - barNetto;
    if (barFree < 0) {
        barFree = 0;
    }
%>

<aa:zone name="loading">
    <html:form action="/newimportwizard">
        <html:hidden property="action"/>
        <html:hidden property="error"/>
        <div class="content_element_container">
        <table border="0" cellspacing="0" cellpadding="0" width="300">
            <tr>
                <td>
                    <b>&nbsp;<b>
                </td>
            </tr>
            <tr>
                <td>
                    <logic:equal value="false" name="newImportWizardForm"
                                 property="error">
                        <img border="0" width="44" height="48"
                             src="${emmLayoutBase.imagesURL}/wait.gif"/>
                    </logic:equal>
                    <logic:equal value="true" name="newImportWizardForm"
                                 property="error">
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
                        <logic:equal value="false" name="newImportWizardForm"
                                     property="error">
                        <table cellspacing="0" cellpadding="0" border="0" style="with: 101px;">
                            <tr style="width:101px;">
                                <td align="left" style="border:1px solid #444444; width:98px;" bgcolor="#ffffff">
                                    <img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                                         width="<%=barNetto%>" height="10" border="0"><%--<img border="0"
                                                                                           width="<%=barFree%>"
                                                                                           height="10"
                                                                                           src="${emmLayoutBase.imagesURL}/one_pixel.gif"/>--%>
                                </td>
                                <td align="right"> 
                                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                                         width="3" height="3" border="0">
                                </td>
                            </tr>
                        </table>
                            <bean:message
                                    key="import.csv_importing_data"/> ${newImportWizardForm.importWizardHelper.completedPercent}%
                        </logic:equal>
                        <logic:equal value="true" name="newImportWizardForm"
                                     property="error">
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