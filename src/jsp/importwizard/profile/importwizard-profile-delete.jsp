<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.ImportProfileAction, org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    ImportProfileForm aForm = (ImportProfileForm) session.getAttribute("importProfileForm");
    int cancelAction = ImportProfileAction.ACTION_VIEW;
    if (aForm.getFromListPage()) {
        cancelAction = ImportProfileAction.ACTION_LIST;
    }
%>

<html:form action="/importprofile.do">
    <html:hidden property="profileId"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="import.ImportProfile"/>:&nbsp;${importProfileForm.profile.name}<br><bean:message key="import.DeleteImportProfileQuestion"/></div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='kill'; document.importProfileForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page='<%= new String("/importprofile.do?action=" + cancelAction + "&cmTemplateId=" + aForm.getProfileId()) %>'/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>