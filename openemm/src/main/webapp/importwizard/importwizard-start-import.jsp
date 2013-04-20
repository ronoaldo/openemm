<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<html:errors property="default"/>

<html:form action="/newimportwizard" enctype="multipart/form-data">
    <html:hidden property="action"/>
    <html:hidden property="upload_file" value="1"/>

    <div class="content_element_container">
        <span class="head3"><bean:message key="import.title.start"/></span>
        <br><br>
    </div>

    <controls:filePanel currentFileName="${newImportWizardForm.currentFileName}"
                        hasFile="${newImportWizardForm.hasFile}"
                        uploadButton="false"/>

    <div class="content_element_container">
        <label for="import_profile_select"><bean:message key="import.wizard.selectImportProfile"/></label>
        <html:select styleId="import_profile_select" property="defaultProfileId">
            <c:forEach items="${newImportWizardForm.importProfiles}"
                       var="item">
                <html:option value="${item.key}">
                    ${item.value}
                </html:option>
            </c:forEach>
        </html:select>

    </div>

    <div class="button_container">
        <div class="action_button right_action_button">
            <input type="hidden" id="start_proceed" name="start_proceed" value=""/>
            <a href="#"
               onclick="document.getElementById('start_proceed').value='proceed'; document.newImportWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a>
        </div>
    </div>

</html:form>