<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="grey_box_container">
        <div class="grey_box_top" style="background:none;"></div>
        <div class="grey_box_content" style="background:none;">
            <div class="grey_box_left_column">
                <label><bean:message key="ChooseCMTemplate" bundle="cmsbundle"/>:</label>
                <html:file property="templateFile"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="mailing.Charset"/>:</label>
                <html:select property="charset" size="1">
                    <c:forEach var="currentCharset" items="${cmTemplateForm.availableCharsets}">
                        <html:option value="${currentCharset}"><bean:message key="mailing.${currentCharset}"/>
                        </html:option>
                    </c:forEach>
                </html:select>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom" style="background:none;"></div>
    </div>

    <div class="filter_button_wrapper">

        <input type="hidden" id="upload" name="upload" value=""/>
        <div class="filterbox_form_button filterbox_form_button_right_corner">
            <a href="#" onclick="document.getElementById('upload').value='true'; document.getElementById('action').value='9'; document.cmTemplateForm.submit(); return false;"><span><bean:message key="Upload"/></span></a>
        </div>

        <div class="action_button"><bean:message key="cms.CMTemplate"/>:</div>
    </div>