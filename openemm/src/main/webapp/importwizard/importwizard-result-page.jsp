<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.RecipientAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<div class="content_element_container">

    <span class="head3"><bean:message key="import.csv_completed"/></span>
    <br><br>

    <%-- Import result information (erorrs, updated, inserted etc.) --%>

    <c:forEach var="reportEntry" items="${newImportWizardForm.reportEntries}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide">
                <bean:message key="${reportEntry.key}"/>:
            </div>
            <div class="importwizard_result_entry_value">
                    ${reportEntry.value}
            </div>
        </div>
        <br>
    </c:forEach>

    <br>

    <%-- assigned mailing lists statistics --%>

    <c:forEach var="assignedList" items="${newImportWizardForm.assignedMailingLists}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide">
                    ${assignedList.shortname}:
            </div>
            <div class="importwizard_result_entry_value">
                    ${newImportWizardForm.mailinglistAssignStats[assignedList.id]} &nbsp; <bean:message
                    key="${newImportWizardForm.mailinglistAddMessage}"/>
            </div>
        </div>
        <br>
    </c:forEach>

    <%-- result files to download --%>
    <br>

    <c:if test="${newImportWizardForm.resultFile != null}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide_save">
                <label><bean:message key="ResultMsg"/>:</label>
            </div>
            <div class="importwizard_result_entry_value">
                <html:link styleClass="blue_link"
                           page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${RESULT}">
                    ${newImportWizardForm.resultFile.name}
                    <img src="${emmLayoutBase.imagesURL}/icon_save.gif"
                         border="0" alt="save">
                </html:link>
            </div>
        </div>
        <br>
    </c:if>

    <c:if test="${newImportWizardForm.validRecipientsFile != null}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide_save">
                <label><bean:message key="import.recipients.valid"/>:</label>
            </div>
            <div class="importwizard_result_entry_value">
                <html:link styleClass="blue_link"
                           page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${VALID}">
                    ${newImportWizardForm.validRecipientsFile.name}
                    <img src="${emmLayoutBase.imagesURL}/icon_save.gif"
                         border="0" alt="save">
                </html:link>
            </div>
        </div>
        <br>
    </c:if>

    <c:if test="${newImportWizardForm.invalidRecipientsFile != null}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide_save">
                <label><bean:message key="import.recipients.invalid"/>:</label>
            </div>
            <div class="importwizard_result_entry_value">
                <html:link styleClass="blue_link"
                           page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${INVALID}">
                    ${newImportWizardForm.invalidRecipientsFile.name}
                    <img src="${emmLayoutBase.imagesURL}/icon_save.gif"
                         border="0" alt="save">
                </html:link>
            </div>
        </div>
        <br>
    </c:if>

    <c:if test="${newImportWizardForm.duplicateRecipientsFile != null}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide_save">
                <label><bean:message key="import.recipients.duplicate"/>:</label>
            </div>
            <div class="importwizard_result_entry_value">
                <html:link styleClass="blue_link"
                           page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${DUPLICATE}">
                    ${newImportWizardForm.duplicateRecipientsFile.name}
                    <img src="${emmLayoutBase.imagesURL}/icon_save.gif"
                         border="0" alt="save">
                </html:link>
            </div>
        </div>
        <br>
    </c:if>

    <c:if test="${newImportWizardForm.fixedRecipientsFile != null}">
        <div class="importwizard_result_entry">
            <div class="importwizard_result_entry_label_wide_save">
                <label><bean:message key="import.recipients.fixed"/>:</label>
            </div>
            <div class="importwizard_result_entry_value">
                <html:link styleClass="blue_link"
                           page="/newimportwizard.do?action=${DOWNLOAD_ACTION}&downloadFileType=${FIXED}">
                    ${newImportWizardForm.fixedRecipientsFile.name}
                    <img src="${emmLayoutBase.imagesURL}/icon_save.gif"
                         border="0" alt="save">
                </html:link>
            </div>
        </div>
        <br>
    </c:if>

    <br>

    <div class="importwizard_button_container_pre"></div>

</div>

<div class="button_container">
    <div class="action_button right_action_button">
        <html:link page='<%="/recipient.do?action=" + RecipientAction.ACTION_LIST %>'>
            <span><bean:message key="button.Finish"/></span>
        </html:link>
    </div>
</div>