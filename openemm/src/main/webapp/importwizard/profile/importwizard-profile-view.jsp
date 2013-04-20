<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html:form action="/importprofile" focus="profile.name">
    <html:hidden property="profileId"/>
    <html:hidden property="action"/>

    <div class="grey_box_container">
        <div class="grey_box_left_column">
            <label for="mailing_name"><bean:message key="default.Name"/>:</label>
            <html:text styleId="mailing_name" property="profile.name" maxlength="99" size="52"/>
        </div>
    </div>

    <br>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="import.profile.file.settings"/></h2>
            <%@include file="/importwizard/profile/file_settings.jsp" %>
        </div>
        <div class="blue_box_bottom"></div>
    </div>
    <br>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="import.profile.process.settings"/></h2>
            <%@include file="/importwizard/profile/action_settings.jsp" %>
        </div>
        <div class="blue_box_bottom"></div>
    </div>
    <br>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="import.profile.gender.settings"/></h2>
            <%@include file="/importwizard/profile/gender_settings.jsp" %>
        </div>
        <div class="blue_box_bottom"></div>
    </div>
    <br>

    <div class="button_container">
        <input type="hidden" id="save" name="save" value=""/>

        <div class="action_button">
            <a href="#"
               onclick="document.importProfileForm.save.value='save'; document.importProfileForm.submit();return false;">
                <span><bean:message key="button.Save"/></span>
            </a>
        </div>

        <c:if test="${importProfileForm.profileId != 0}">
            <div class="action_button">
                <html:link
                        page="/importprofile.do?action=${ACTION_CONFIRM_DELETE}&profileId=${importProfileForm.profileId}&fromListPage=false">
                    <span><bean:message key="button.Delete"/></span>
                </html:link>
            </div>
        </c:if>
        <div class="action_button"><html:link page="/importprofile.do?action=1"><span><bean:message
                key="button.Cancel"/></span></html:link></div>
        <div class="action_button"><bean:message key="import.ImportProfile"/>:</div>
    </div>


</html:form>