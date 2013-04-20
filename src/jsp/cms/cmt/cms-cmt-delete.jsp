<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/cms_cmt.do">
    <html:hidden property="cmtId"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="cms.ContentModuleType"/>:&nbsp;${contentModuleTypeForm.name}<br>
        <bean:message key="DeleteCMTQuestion" bundle="cmsbundle"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='true'; document.contentModuleTypeForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/cms_cmt.do?action=${cancelAction}&cmtId=${contentModuleTypeForm.cmtId}"/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>
