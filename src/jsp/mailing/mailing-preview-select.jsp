<%-- checked --%>
<%@ page language="java" import="org.agnitas.util.AgnUtils, org.agnitas.web.MailingSendAction, org.agnitas.web.MailingSendForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% pageContext.setAttribute("COMPANY_ID",AgnUtils.getCompanyID(request)); %>
<% pageContext.setAttribute("ACTION_PREVIEW",MailingSendAction.ACTION_PREVIEW); %>

<%
    int prevX=800;
    int prevY=600;
    MailingSendForm aForm=null;
    if(request.getAttribute("mailingSendForm")!=null) {
        aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
    }

    switch(aForm.getPreviewSize()) {
        case 1:
            prevX=800;
            prevY=600;
            break;
        case 2:
            prevX=1024;
            prevY=768;
            break;
        case 3:
            prevX=1280;
            prevY=1024;
            break;
        case 4:
            prevX=640;
            prevY=480;
            break;
        case 5:
            prevX=320;
            prevY=480;
            break;
        default:
            aForm.setPreviewSize(1);
            prevX=800;
            prevY=600;
            break;
    }
%>
<!-- c:if test="$ {mailingSendForm.hasPreviewRecipient}" -->
<div class="blue_box_container">
               <html:form action="/mailingsend">
                   <html:hidden property="mailingID"/>
                   <html:hidden property="action"/>
                   <div class="blue_box_container">
                   <bean:message key="recipient.Recipient"/>:&nbsp;
                   <html:select property="previewCustomerID" size="1" >
                   		<c:forEach var="recipient" items="${previewRecipients}">
                    	    <html:option value="${recipient.key}">${recipient.value}</html:option>
                	    </c:forEach>
                   	</html:select>
                   &nbsp;&nbsp;
                   <bean:message key="action.Format"/>:&nbsp;
                   <html:select property="previewFormat" size="1">
                       <html:option value="0"><bean:message key="mailing.Text"/></html:option>
                       <logic:greaterThan name="mailingSendForm" property="emailFormat" value="0">
                           <html:option value="1"><bean:message key="mailing.HTML"/></html:option>
                       </logic:greaterThan>
                   </html:select>
                   &nbsp;&nbsp;
                   <bean:message key="default.Size"/>:&nbsp;
                   <html:select property="previewSize" size="1">
                       <html:option value="4">640x480</html:option>
                       <html:option value="1">800x600</html:option>
                       <html:option value="2">1024x768</html:option>
                       <html:option value="3">1280x1024</html:option>
                   </html:select>
                   </div>
                       <div class="button_container" style="margin-left:0px;">
                    <div class="action_button " style="margin-bottom:5px;">

                        <a href="#"
                           onclick="document.mailingSendForm.submit(); return false;"><span><bean:message
                                key="mailing.Preview"/></span></a>
                    </div>
                </div>

       <% if(aForm.getPreviewFormat()==0 || aForm.getPreviewFormat()==1 || aForm.getPreviewFormat()==2) { %>
        <div>
               <jsp:include page="/mailingsend.do" flush="true">
                   <jsp:param name="action" value="<%= MailingSendAction.ACTION_PREVIEW_HEADER %>"/>
                   <jsp:param name="previewCustomerID" value="<%= aForm.getPreviewCustomerID() %>"/>
               </jsp:include>
          </div>
       <% } %>
       <iframe name="previewFrame" src="<html:rewrite page="/mailingsend.do?action=${ACTION_PREVIEW}&mailingID=${mailingSendForm.mailingID}&previewFormat=${mailingSendForm.previewFormat}&previewCustomerID=${mailingSendForm.previewCustomerID }" />"
                    onload="if(this.width > this.parentNode.offsetWidth) this.width='100%';"
                    width="<%= prevX %>" height="<%= prevY %>" border="0" scrolling="auto">
                Your Browser does not support IFRAMEs, please update!
       </iframe>
                   </html:form>
                   </div>
<!-- /c:if -->



