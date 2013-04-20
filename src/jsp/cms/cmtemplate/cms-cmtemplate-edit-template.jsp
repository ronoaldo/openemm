<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.forms.CMTemplateForm" %>
<%@ page import="org.agnitas.cms.webservices.generated.MediaFile" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% CMTemplateForm aForm = (CMTemplateForm) session.getAttribute("cmTemplateForm");%>

<html:form action="/cms_cmtemplate" focus="name" method="post" enctype="multipart/form-data" >
    <html:hidden property="cmTemplateId"/>
    <html:hidden property="action" value='<%= String.valueOf(request.getAttribute("ACTION_SAVE_TEMPLATE"))%>'/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div>
                <bean:message key="cms.TemplateContent"/>:
            </div>
            <div>
                <html:textarea property="contentTemplate" cols="100" rows="30"/>
            </div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="export_wizard_content" style="margin-top:10px;">
        <bean:message key="mailing.Graphics_Component"/>:
    </div>

    <% for (MediaFile mediaFile : aForm.getLMediaFile()) { %>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
        <div>
            <%
                int begin = mediaFile.getName().indexOf('/') + 1;
                String title = mediaFile.getName().substring(begin);
            %>
            <b><%=title%></b>
        </div>
        <div style="float: left; text-align: center;">
            <div style="padding-bottom: 10px;">
                <iframe src="<html:rewrite page='<%= new String("/cms_image?fid=" + mediaFile.getId()) %>'/>" width="150" height="150">
                    Your Browser does not support IFRAMEs, please update!
                </iframe>
            </div>
                <div class="action_button" style="float:none">
                    <html:link page='<%= new String("/cms_cmtemplate.do?action=" + request.getAttribute("ACTION_DELETE_IMAGE_TEMPLATE") + "&cmTemplateMediaFileId=" + mediaFile.getId())%>'>
                        <span><bean:message key="button.Delete"/></span>
                    </html:link>
                </div>
         </div>
        <div id='<%= "div." + mediaFile.getId()%>' style="float: inherit; padding-right: 20px;">
            <div style="margin-bottom: 6px; padding-bottom: 10px; padding-right: 10px; padding-top:10px;">
                <input type="checkbox" onclick="isChange('<%= "changeImage." + mediaFile.getId() + ".select"%>');"
                       id="<%= "changeImage." + mediaFile.getId() + ".select"%>"
                       name="<%= "changeImage." + mediaFile.getId() + ".select"%>">
                <bean:message key="button.Edit"/>&nbsp;
            </div>
            <div>
                <input type="radio" disabled="false"
                       id='<%= "imageUploadOrExternal." + mediaFile.getId() + ".upload"%>'
                       name="<%= "imageUploadOrExternal." + mediaFile.getId() + ".select"%>"
                       value="upload"
                       checked="1">
                <bean:message key="button.Upload"/>:&nbsp;
            </div>
            <html:errors property='<%="url_img_upload_"+mediaFile.getId()%>'/>
            <div style="margin-bottom: 8px;">
                &nbsp;&nbsp;&nbsp;&nbsp;
                <input type="file" disabled="false"
                       id='<%= "imageUploadOrExternal." + mediaFile.getId() + ".file" %>'
                       name="<%= "imageUploadOrExternal." + mediaFile.getId() + ".file" %>"
                       value="<%= request.getAttribute("save_ok") != null ? request.getParameter("imageUploadOrExternal." + mediaFile.getId() + ".file")== null ? "" :request.getParameter("imageUploadOrExternal." + mediaFile.getId() + ".file") :""%>"
                       style="width:250px;">
            </div>
            <div>
                <input type="radio" disabled="false"
                       id='<%= "imageUploadOrExternal." + mediaFile.getId() + ".external"%>'
                       name="<%= "imageUploadOrExternal." + mediaFile.getId() + ".select"%>"
                       value="external">
                <bean:message key="mailing.Graphics_Component.external"/>:&nbsp;
            </div>
            <html:errors property='<%="url_img_"+mediaFile.getId()%>'/>
            <div>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <% String imageUploadOrExternale = String.format("imageUploadOrExternal.%s.url", mediaFile.getId());%>
                <input type="text" disabled="false"
                       id="<%= imageUploadOrExternale %>"
                       name="<%= imageUploadOrExternale %>"
                       value="<%= request.getAttribute("save_ok") != null ? request.getParameter(imageUploadOrExternale)== null ? "" :request.getParameter(imageUploadOrExternale) :""%>"
                       class="extenal_picture_input">
            </div>
        </div>
    </div>
    <div class="blue_box_bottom"></div>
    </div>
    <% }%>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">

    <div id="newMediaFileContent" name="newMediaFileContent" style="float: left;">
        <div>
            <b><bean:message key="button.New"/>&nbsp;<bean:message key="mailing.Graphics_Component"/></b>
        </div>
        <div id="newMediaFileField"
             style="width:270px; float:inherit;border: 2px; padding-bottom: 10px;
             padding-left: 10px; padding-right: 20px; ">
            <div style="margin-bottom:6px;padding-bottom: 10px; padding-right: 10px; padding-top:10px;">
                <input type="checkbox" onclick="isChange('changeImage.new.select');"
                       id="changeImage.new.select"
                       name="changeImage.new.select">
                    <bean:message key="button.Add"/>:&nbsp;
            </div>
            <html:errors property="url_img_name"/>
            <div style="margin-bottom:6px;">
                <bean:message key="default.Name"/>:&nbsp;&nbsp;
                <div class="cm_template_image_name">
                <input type="text" disabled="false"
                       id="name.new.text"
                       name="name.new.text"
                      value='<%= request.getAttribute("save_ok") != null ? request.getParameter("name.new.text")== null ? "" :request.getParameter("name.new.text") :""%>'
                       style="width:218px;">
                </div>
            </div>
            <div>
                <input type="radio" disabled="false"
                       id="imageUploadOrExternal.new.upload"
                       name="imageUploadOrExternal.new.select"
                       value="upload"
                       checked="1">
                <bean:message key="button.Upload"/>:&nbsp;
            </div>
            <html:errors property="url_img_new_upload"/>
            <div style="margin-bottom:6px; margin-left:20px;">

                <input type="file" disabled="false"
                       id="imageUploadOrExternal.new.file"
                       name="imageUploadOrExternal.new.file"
                       value='<%= request.getAttribute("save_ok") != null ? request.getParameter("imageUploadOrExternal.new.file")== null ? "" :request.getParameter("imageUploadOrExternal.new.file") :""%>'
                       style="width:250px;">
            </div>
            <div>
                <input type="radio" disabled="false"
                       id="imageUploadOrExternal.new.external"
                       name="imageUploadOrExternal.new.select"
                       value="external">
                <bean:message key="mailing.Graphics_Component.external"/>:&nbsp;
            </div>
            <html:errors property="url_img_new"/>
            <div style="margin-left:20px;">
                <input type="text" disabled="false"
                       id="imageUploadOrExternal.new.url"
                       name="imageUploadOrExternal.new.url"
                       value='<%= request.getAttribute("save_ok") != null ? request.getParameter("imageUploadOrExternal.new.url") == null ? "" :request.getParameter("imageUploadOrExternal.new.url") :""%>'
                       class="extenal_picture_input">
            </div>
        </div>
    </div>
    </div>
    <div class="blue_box_bottom"></div>
    </div>

    <div class="button_container">
        <div class="action_button">
            <html:link page='<%= new String("/cms_cmtemplate.do?action=2&cmTemplateId="+ aForm.getCmTemplateId())%>'>
                <span><bean:message key="button.Cancel"/></span>
            </html:link>
        </div>

        <input type="hidden" id="save" name="save" value=""/>
        <div class="action_button">
            <a href="#" onclick="document.getElementById('save').value='true'; document.cmTemplateForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        </div>

        <div class="action_button"><bean:message key="cms.CMTemplate"/>:</div>
    </div>
</html:form>