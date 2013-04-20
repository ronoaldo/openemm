<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleTypeForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<% ContentModuleTypeForm aForm = (ContentModuleTypeForm) session.getAttribute("contentModuleTypeForm"); %>

    <div class="grey_box_container">
        <div class="grey_box_top" style="background:none;"></div>
        <div class="grey_box_content" style="background:none;">
            <bean:message key="SelectBaseCMT" bundle="cmsbundle"/>:
            <html:select property="cmtId" styleClass="cms_new_element_select">
                <% for(int i = 0; i < aForm.getAllCMT().size(); i++) { %>
                <html:option value="<%= String.valueOf(aForm.getAllCMT().get(i).getId()) %>">
                    <%= aForm.getAllCMT().get(i).getName() %>
                </html:option>
                <% } %>
            </html:select>
            <div class="grey_box_center_column"></div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom" style="background:none;"></div>
    </div>
    <div class="export_wizard_content">

    </div>

    <div class="filter_button_wrapper">

        <input type="hidden" id="create" name="create" value=""/>
        <div class="filterbox_form_button filterbox_form_button_right_corner">
            <html:link page="#" onclick="document.getElementById('action').value='11'; document.getElementById('create').value='true'; document.contentModuleTypeForm.submit(); return false;">
                <span><bean:message key="button.Create"/></span>
            </html:link>
        </div>

    
        <div class="action_button"><bean:message key="cms.ContentModuleType"/>:</div>
    </div>
   

