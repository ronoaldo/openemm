<%--checked --%>
<%@ page language="java" import="org.agnitas.util.AgnUtils, org.agnitas.web.MailingWizardAction, org.agnitas.web.MailingWizardForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    MailingWizardForm aForm= (MailingWizardForm) request.getAttribute("aForm");
   String permToken=null;
    Integer tmpMailingID= (Integer) request.getAttribute("tmpMailingID");
%>

<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span>4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span>8</span></li>
        <li><span class="step_active">9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>

    <p><bean:message key="mailing.wizard.ChooseThenPressSave"/>.</p><br>
<html:form action="/mwLink" focus="default.description" enctype="application/x-www-form-urlencoded">
    <html:hidden property="action"/>



      <div class="assistant_step7_form_item">
          <label for="linkUrl" ><bean:message key="mailing.URL"/>:&nbsp;</label>
          <input type="text" readonly="true" value="<bean:write name="mailingWizardForm" property="linkUrl"/>" id="linkUrl" style="width:190px;"/>
         </div>
        <div class="assistant_step7_form_item">
          <label for="linkName" ><bean:message key="default.description"/>:&nbsp;</label>
          <html:text property="linkName" size="52" maxlength="99" styleId="linkName" style="width: 190px;"/>
        </div>
    <div class="assistant_step7_form_item">
          <label for="trackable" ><bean:message key="mailing.Trackable"/>:&nbsp;</label>
          <html:select property="trackable" styleId="trackable">
                                <html:option value="0"><bean:message key="mailing.Not_Trackable"/></html:option>
                                <html:option value="1"><bean:message key="mailing.Only_Text_Version"/></html:option>
                                <html:option value="2"><bean:message key="mailing.Only_HTML_Version"/></html:option>
                                <html:option value="3"><bean:message key="mailing.Text_and_HTML_Version"/></html:option>
                            </html:select>
        </div>
    <div class="assistant_step7_form_item">
          <label for="linkAction" ><bean:message key="action.Action"/>:&nbsp;</label>
          <html:select property="linkAction" size="1" styleId="linkAction">
                                <html:option value="0"><bean:message key="settings.No_Action"/></html:option>
                                <c:forEach var="action" items="${linkActions}">
                                <html:option value="${action.id}">
                                   ${action.shortname}
                                </html:option>
                                </c:forEach>
          </html:select>

      </div>
    <div class="action_button mailingwizard_add_button">
            <a href="#" onclick="document.mailingWizardForm.action.value='link_save_only'; document.mailingWizardForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        </div>


     <div class="assistant_step7_button_container">
             <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Finish"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TO_ATTACHMENT}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Skip"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='link'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>

</html:form>
    </div>
