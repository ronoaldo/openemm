<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div class="grey_box_container">
    <div class="grey_box_top" style="background:none;"></div>
    <div class="grey_box_content" style="background:none;">
        <logic:equal name="contentModuleForm" property="moduleTypeNumber" value="0">
            <bean:message key="cms.NoModuleTypeExists"/>
        </logic:equal>
        <logic:notEqual name="contentModuleForm" property="moduleTypeNumber" value="0">
            <bean:message key="SelectCMT" bundle="cmsbundle"/>:
            <html:select property="cmtId" styleClass="cms_new_element_select">
                <logic:iterate collection="${contentModuleForm.allCMT}" id="cmt">
                    <html:option value="${cmt.id}">
                        ${cmt.name}
                    </html:option>
                </logic:iterate>
            </html:select>
        </logic:notEqual>
        <div class="grey_box_center_column"></div>
        <div class="grey_box_right_column"></div>
    </div>
    <div class="grey_box_bottom" style="background:none;"></div>
</div>

<div class="filter_button_wrapper">
    <logic:notEqual name="contentModuleForm" property="moduleTypeNumber" value="0">
        <div class="filterbox_form_button filterbox_form_button_right_corner">
            <html:link page="#" onclick="document.getElementById('action').value='2'; document.contentModuleForm.submit(); return false;">
                <span><bean:message key="button.Create"/></span>
            </html:link>
        </div>

        <div class="action_button"><bean:message key="cms.ContentModule"/>:</div>
    </logic:notEqual>
</div>

    