
<%@ page language="java" import="org.agnitas.beans.MailingComponent, org.agnitas.util.AgnUtils, org.agnitas.web.MailingWizardForm, java.util.Iterator, java.util.Map" contentType="text/html; charset=utf-8" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.agnitas.dao.TargetDao" %>
<%@ page import="org.agnitas.target.Target" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Integer tmpMailingID=(Integer)request.getAttribute("tmpMailingID");
	MailingWizardForm aForm= (MailingWizardForm) request.getAttribute("aForm");
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
        <li><span>9</span></li>
        <li><span class="step_active">10</span></li>
        <li><span>11</span></li>
    </ul>

    <p><bean:message key="mailing.Attachments"/>.</p><br>
<html:form action="/mwAttachment" enctype="multipart/form-data">
	<html:hidden property="action"/>

	<agn:ShowByPermission token="mailing.attachments.show">

    <div class="assistant_step7_form_item">
        <label><bean:message key="mailing.New_Attachment"/>:</label>
    </div>
	<agn:ShowByPermission token="mailing.attachment.personalize">

    <div class="assistant_step7_form_item">
         <label for="newAttachmentType" ><bean:message key="mailing.attachment.type"/>:&nbsp;</label>
          <html:select property="newAttachmentType" onchange="changeVisible()" styleId="newAttachmentType">
				<html:option value="0"><bean:message key="mailing.attachment.type.normal"/></html:option>
				<html:option value="1"><bean:message key="mailing.attachment.type.personalized"/></html:option>
				</html:select>
        </div>
       </agn:ShowByPermission>
    <div class="assistant_step7_form_item">
     <label for="newAttachment" ><bean:message key="mailing.Attachment"/>:&nbsp;</label>
        <html:file property="newAttachment" styleId="newAttachment" onchange="getFilename()" style="width:190px;"/>
     </div>
    <div class="assistant_step7_form_item">
     <label for="newAttachmentName" ><bean:message key="mailing.attachment.name"/>:&nbsp;</label>
        <html:text property="newAttachmentName" styleId="newAttachmentName" style="width:185px;"/>
    </div>
                <agn:ShowByPermission token="mailing.attachment.personalize">
                    <div class="assistant_step7_form_item" id="divAttachmentBackground">
                     <div id="attachmentBackground"><label for="newAttachmentBackground" ><bean:message key="mailing.attachment.background"/>:&nbsp;</label></div>
                     <html:file property="newAttachmentBackground" styleId="newAttachmentBackground" style="width:190px;"/>
                    </div>
		        </agn:ShowByPermission>

                      <div class="assistant_step7_form_item">
                <label for="attachmentTargetID" ><bean:message key="target.Target"/>:&nbsp;</label>
                    <html:select property="attachmentTargetID" size="1" styleId="attachmentTargetID">
                        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <c:forEach var="target" items="${targets}">
                        <html:option value="${target.id}">
                          ${target.targetName}
                        </html:option>
                        </c:forEach>
                    </html:select>
                </div>
    <div class="action_button mailingwizard_add_button">
            <a href="#" onclick="document.mailingWizardForm.action.value='attachment'; document.mailingWizardForm.submit(); return false;"><span><bean:message key="button.Add"/></span></a>
        </div>

                <% int i=1; boolean isFirst=true; %>
                <% MailingComponent comp=null; %>
    <%
        Map	componentMap=aForm.getMailing().getComponents();
        Iterator	it=componentMap.keySet().iterator();

        while(it.hasNext()) {
            String key=(String) it.next();
            comp=(MailingComponent) componentMap.get(key);


            if(comp.getType() == MailingComponent.TYPE_ATTACHMENT ||
               comp.getType() == MailingComponent.TYPE_PERSONALIZED_ATTACHMENT) {
    %>

                    <% if (isFirst) {
                        isFirst = false; %>
                    <div class="assistant_step7_form_item">
                        <label><bean:message key="mailing.Attachments"/>:</label>
                    </div>

                    <% } %>
                <div class="assistant_step7_form_item">
                    <b><bean:message key="mailing.Attachment"/>:&nbsp;<html:link page='<%= new String("/sc?compID=" + comp.getId()) %>'><%= comp.getComponentName() %>&nbsp;&nbsp;<img src="${emmLayoutBase.imagesURL}/download.gif" border="0" alt="<bean:message key='button.Download'/>"></html:link></b><br><br>
                        <input type="hidden" name="compid<%= i++ %>" value='<%= comp.getId() %>'>
                        <% if(comp.getType() == 3) { %>
                        <bean:message key="mailing.Mime_Type"/>:&nbsp;<%= comp.getMimeType() %>&nbsp;<br>
                        <bean:message key="mailing.Original_Size"/>:&nbsp;<%= comp.getBinaryBlock().length / 1024 %>&nbsp;<bean:message key="default.KByte"/><br>
		                <bean:message key="default.Size_Mail"/>:&nbsp;<%= comp.getBinaryBlock().length / 1024 * 4/3 %>&nbsp;<bean:message key="default.KByte"/><br>
                        <% } else { %>
                        <bean:message key="mailing.attachment.type.personalized"/><br><br>
                        <% } %>
                    <%  ApplicationContext aContext = WebApplicationContextUtils.getWebApplicationContext(application);
                        TargetDao dao = (TargetDao) aContext.getBean("TargetDao");
                        Target aTarget = dao.getTarget(comp.getTargetID(), AgnUtils.getCompanyID(request));
                        String targetShortname = null;
                        if (aTarget != null) {
                            targetShortname = aTarget.getTargetName(); %>
                        <bean:message key="target.Target"/>:&nbsp;<%= targetShortname %>
                    <% } else {%>
                        <bean:message key="target.Target"/>:&nbsp;<bean:message key="statistic.All_Subscribers"/>
                    <% } %>
                </div>
    <% }
    }%>

</agn:ShowByPermission>

  <div class="assistant_step7_button_container">
             <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Finish"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Skip"/></span></a></div>
      <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
 </div>
</div>
</html:form>
<script language="JavaScript">
    Event.observe(window, 'load', function() {
        document.getElementById("newAttachmentName").value = "";
        document.getElementById("attachmentTargetID").selectedIndex = 0;
    });
    <!--
    function getFilename() {
    document.getElementById("newAttachmentName").value=document.getElementById("newAttachment").value.match(/[^\\\/]+$/);
    }
    <agn:ShowByPermission token="mailing.attachment.personalize">
    function changeVisible()
    {
    if(document.getElementById("newAttachmentType").value=="0") {
    document.getElementById("newAttachmentBackground").style.visibility = "hidden";
    document.getElementById("attachmentBackground").style.visibility = "hidden";
    } else {
    document.getElementById("newAttachmentBackground").style.visibility = "visible";
    document.getElementById("attachmentBackground").style.visibility = "visible";
    }
    }
    changeVisible();
    </agn:ShowByPermission>
    //-->
</script>
