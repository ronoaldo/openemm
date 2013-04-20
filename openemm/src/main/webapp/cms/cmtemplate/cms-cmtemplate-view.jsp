<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.forms.CMTemplateForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<% CMTemplateForm aForm = (CMTemplateForm) session.getAttribute("cmTemplateForm"); %>

<html:form action="/cms_cmtemplate" focus="name">
    <html:hidden property="cmTemplateId"/>
    <input type="hidden" name="action" id="action">

    <div class="grey_box_container">
          <div class="grey_box_top"></div>
          <div class="grey_box_content">
              <div class="grey_box_left_column">
                  <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                  <html:text styleId="mailing_name" property="name" maxlength="99" size="42"/>
              </div>
              <div class="grey_box_center_column">
                  <label for="mailing_name"><bean:message key="default.description"/>:</label>
                  <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
              </div>
              <div class="grey_box_right_column"></div>
          </div>
          <div class="grey_box_bottom"></div>
      </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="mailing.Preview"/></h2>
            <div class="cmtemplate_preview_controls">
                <div class="cm_template_preview_size_selector">
                    <bean:message key="default.Size"/>:
                    <html:select property="previewSize" size="1">
                        <% for (int i = 0; i < aForm.getPreviewValues().length; i++) {%>
                        <html:option
                                value="<%= String.valueOf(aForm.getPreviewValues()[i]) %>">
                            <%= aForm.getPreviewSizes()[i] %>
                        </html:option>
                        <% } %>
                    </html:select>
                </div>
                <div class="action_button cm_template_preview_button">
                    <input type="hidden" name="changePreviewSize" id="changePreviewSize" value="">
                    <a href="#" onclick="document.getElementById('changePreviewSize').value='changePreviewSize'; document.cmTemplateForm.submit();">
                        <span><bean:message key="mailing.Preview"/></span>
                    </a>
                </div>
            </div>
            <div class="cm_template_preview_container">
                 <iframe width="${cmTemplateForm.previewWidth}" scrolling="auto"
                        height="${cmTemplateForm.previewHeight}" border="0"
                        src="<html:rewrite page="${PREVIEW_URL}"/>"
                        style="background-color : #FFFFFF; border: 1px solid #D3D3D3; ">
                    "Your Browser does not support IFRAMEs, please
                    update!
                </iframe>
            </div>
        </div>
    <div class="blue_box_bottom"></div>
    </div>

    <div class="button_container">

        <div class="action_button">
             <html:link page="/cms_cmtemplate.do?action=${ACTION_CONFIRM_DELETE}&cmTemplateId=${cmTemplateForm.cmTemplateId}&fromListPage=false"><span><bean:message key="button.Delete"/></span></html:link>
        </div>

        <div class="action_button">
             <html:link page="/cms_cmtemplate.do?action=${ACTION_ASSIGN_LIST}&cmTemplateId=${cmTemplateForm.cmTemplateId}"><span><bean:message key="cms.AssignToMailing"/></span></html:link>
        </div>

        <div class="action_button">
             <html:link page="/cms_cmtemplate.do?action=${ACTION_EDIT_TEMPLATE}&cmTemplateId=${cmTemplateForm.cmTemplateId}"><span><bean:message key="button.Edit"/></span></html:link>
        </div>

        <input type="hidden" id="save" name="save" value=""/>
        <div class="action_button">
            <a href="#" onclick="document.getElementById('action').value=${ACTION_SAVE}; document.cmTemplateForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        </div>

        <div class="action_button"><bean:message key="cms.CMTemplate"/>:</div>
    </div>

</html:form>