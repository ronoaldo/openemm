<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.ProfileFieldAction, org.agnitas.web.ProfileFieldForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    ProfileFieldForm aForm = (ProfileFieldForm) request.getAttribute("profileFieldForm");
    int cancelAction = ProfileFieldAction.ACTION_VIEW;
    if(aForm.getFromListPage()){
        cancelAction = ProfileFieldAction.ACTION_LIST;
    }
%>

<html:form action="/profiledb">
    <html:hidden property="fieldname"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="settings.FieldName"/>:&nbsp;<%= ((ProfileFieldForm) request.getAttribute("profileFieldForm")).getFieldname() %> <br>
        <bean:message key="settings.DeleteProfileFieldQuestion"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>
                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='true';document.profileFieldForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page='<%= new String("/profiledb.do?action=" + cancelAction + "&fieldname=" + ((ProfileFieldForm)request.getAttribute("profileFieldForm")).getFieldname()) %>'/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>