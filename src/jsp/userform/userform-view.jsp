<%-- checked --%>
<%@ page language="java" import="org.agnitas.web.UserFormEditAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_CONFIRM_DELETE" value="<%= UserFormEditAction.ACTION_CONFIRM_DELETE %>" scope="request" />

<html:form action="/userform">
	<html:hidden property="formID" />
	<html:hidden property="action" />
    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="formName" maxlength="99" size="42"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content userform_box_content">
            <bean:message key="action.Action"/>:&nbsp;
            <html:select property="startActionID" size="1">
                <html:option value="0">
                    <bean:message key="settings.No_Action"/>
                </html:option>
                <c:forEach var="emm_action" items="${emm_actions}">
                    <html:option value="${emm_action.id}">
                        ${emm_action.shortname}
                    </html:option>
                </c:forEach>
            </html:select>
            <br>
            <br>
            <html:radio styleId="successUseForm" property="successUseUrl" value="false"/>
            <label for="successUseForm" class="userform_content_form_label"><bean:message key="settings.form.success"/>:</label>
            <br>
            <html:textarea property="successTemplate" rows="14" cols="75"/>
            <br>
            <html:radio styleId="successUseUrl" property="successUseUrl" value="true"/>
            <label for="successUseUrl" class="userform_content_form_label"><bean:message key="settings.form.success_url"/>:</label>
            <br>
            <html:text property="successUrl" styleClass="userform_content_form_input"/>
        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content userform_box_content">
            <html:radio styleId="errorUseForm" property="errorUseUrl" value="false"/>
            <label for="errorUseForm" class="userform_content_form_label"><bean:message key="settings.form.error"/>:</label>
            <br>
            <html:textarea property="errorTemplate" rows="14" cols="75"/>
            <br>
            <html:radio styleId="errorUseUrl" property="errorUseUrl" value="true"/>
            <label for="errorUseUrl" class="userform_content_form_label"><bean:message key="settings.form.error_url"/>:</label>
            <br>
            <html:text property="errorUrl" styleClass="userform_content_form_input"/>
            <br>
            <br>
            <bean:message key="action.Action"/>:&nbsp;
            <html:select property="endActionID" size="1">
                <html:option value="0">
                    <bean:message key="settings.No_Action"/>
                </html:option>
                <c:forEach var="emm_action" items="${emm_actions}">
                    <html:option value="${emm_action.id}">
                        ${emm_action.shortname}
                    </html:option>
                </c:forEach>
            </html:select>
        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <div class="button_container">
        <agn:ShowByPermission token="forms.change">
            <input type="hidden" id="save" name="save" value=""/>
            <div class="action_button">
                <a href="#"
                   onclick="document.getElementById('save').value='save'; document.userFormEditForm.submit(); return false;"><span><bean:message
                        key="button.Save"/></span></a>
            </div>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="forms.delete">
            <c:if test="${userFormEditForm.formID != 0}">
                <div class="action_button">
                    <html:link
                            page='/userform.do?action=${ACTION_CONFIRM_DELETE}&formID=${userFormEditForm.formID}&fromListPage=false'>
                        <span><bean:message key="button.Delete"/></span>
                    </html:link>
                </div>
            </c:if>
        </agn:ShowByPermission>

    </div>
</html:form>
