<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.forms.ContentModuleTypeForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<% ContentModuleTypeForm aForm = (ContentModuleTypeForm) session.getAttribute("contentModuleTypeForm"); %>

<html:form action="/cms_cmt" focus="name">
    <html:hidden property="cmtId"/>
    <html:hidden property="action"/>


    <table border="0" cellspacing="4" cellpadding="0">
        <tr align="left">
            <td>
                <table>
                    <tr>
                        <td><bean:message key="default.Size"/>:</td>
                        <td>
                            <html:select property="previewSize" size="1">
                            <% for(int i=0; i < aForm.getPreviewValues().length; i++) {%>
                                <html:option value="<%= String.valueOf(aForm.getPreviewValues()[i]) %>">
                                    <%= aForm.getPreviewSizes()[i] %>
                                </html:option>
                            <% } %>
                            </html:select>
                        </td>
                        <td>
                            <html:image src="button?msg=mailing.Preview"
                                        border="0"
                                        property="changePreviewSize"
                                        value="true"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <iframe width="${contentModuleTypeForm.previewWidth}" scrolling="auto"
                        height="${contentModuleTypeForm.previewHeight}" border="0"
                        src="<html:rewrite page="${previewUrl}" />"
                        style="background-color : #FFFFFF;">
                    "Your Browser does not support IFRAMEs, please
                    update!
                </iframe>
            </td>
        </tr>
    </table>
    </td>
    

</html:form>