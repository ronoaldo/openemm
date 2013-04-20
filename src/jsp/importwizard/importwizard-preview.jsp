<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<html:form action="/newimportwizard" enctype="multipart/form-data">
    <div class="content_element_container">
        <span class="head3"><bean:message key="import.title.preview"/>:</span>
        <br>
        <br>

        <div class="import_preview_container">
            <table border="0" cellspacing="0" cellpadding="2" width="100%">
                <logic:iterate id="row" name="newImportWizardForm"
                               property="previewParsedContent" scope="session"
                               indexId="counter">
                    <tr>
                        <logic:iterate id="element" name="row" scope="page">
                            <c:if test="${counter==0}">
                                <td align="left"><b><bean:write name="element"/></b>
                                </td>
                            </c:if>
                            <c:if test="${counter!=0}">
                                <td>
                                    <input name="dummy" type="text" size="13"
                                           value="${element}" readonly>
                                </td>
                            </c:if>
                        </logic:iterate>
                    </tr>
                </logic:iterate>
            </table>
        </div>


        <div class="dotted_line"></div>
        <br>
        <span class="head3"><bean:message key="import.SubscribeLists"/>:</span>
        <br>
        <br>

        <div class="importwizard_mailing_column">
            <c:forEach var="mlist" items="${newImportWizardForm.allMailingLists}" begin="0" end="${size/2}">
                <div class="filterbox_checkbox_item">
                    <input type="checkbox" name="agn_mlid_${mlist.id}"/>
                        ${mlist.shortname}
                </div>
            </c:forEach>
        </div>
        <div class="importwizard_mailing_column">
            <c:forEach var="mlist" items="${newImportWizardForm.allMailingLists}" begin="${(size/2)+1}"
                       end="${size}">
                <div class="filterbox_checkbox_item">
                    <input type="checkbox" name="agn_mlid_${mlist.id}"/>
                        ${mlist.shortname}
                </div>
            </c:forEach>
        </div>

        <div class="importwizard_button_container_pre"></div>
    </div>
    <div class="button_container">
        <div class="action_button right_action_button">
            <input type="hidden" id="preview_proceed" name="preview_proceed" value=""/>
            <a href="#"
               onclick="document.getElementById('preview_proceed').value='proceed'; document.newImportWizardForm.submit(); return false;"><span><bean:message
                    key="button.Import_Start"/></span></a>
        </div>
        <div class="action_button">
            <input type="hidden" id="preview_back" name="preview_back" value=""/>
            <a href="#"
               onclick="document.getElementById('preview_back').value='back'; document.newImportWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a>
        </div>
    </div>
</html:form>