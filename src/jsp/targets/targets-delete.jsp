<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/target">
<html:hidden property="targetID"/>
<html:hidden property="action"/>

<div class="new_mailing_start_description delete_targetgroup">
    <bean:message key="target.Target"/>:</br>
    ${targetForm.shortname}</br>
    <bean:message key="target.delete.question"/>
</div>
<div class="remove_element_button_container">
    <div class="greybox_small_top"></div>
    <div class="greybox_small_content">
        <div class="new_mailing_step1_left_column">
            <input type="hidden" id="kill" name="kill" value=""/>
            <agn:ShowByPermission token="targets.delete">
            	<div class="big_button"><a href="#" onclick="document.getElementById('kill').value='true'; document.targetForm.submit(); return false;"><span><bean:message key="button.Delete"/></span></a></div>
            </agn:ShowByPermission>
        </div>
        <div class="new_mailing_step1_right_column">
            <div class="big_button"><a href="<html:rewrite page="/target.do?action=${targetForm.previousAction}&targetID=${targetForm.targetID}"/>"><span><bean:message key="button.Cancel"/></span></a></div>
        </div>
    </div>
    <div class="greybox_small_bottom"></div>
</div>

</html:form>


