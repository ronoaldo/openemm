<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.MailloopAction, java.util.Locale" %>
<%@ page import="org.agnitas.web.MailloopForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% pageContext.setAttribute("FCKEDITOR_PATH", AgnUtils.getEMMProperty("fckpath")); %>
<% int tmpLoopID = 0;

    if (request.getAttribute("mailloopForm") != null) {
        tmpLoopID = ((MailloopForm) request.getAttribute("mailloopForm")).getMailloopID();
    }
%>

<script type="text/javascript" src="${FCKEDITOR_PATH}/fckeditor.js"></script>

<script type="text/javascript">
    <!--
       var baseUrl=window.location.pathname;
       pos=baseUrl.lastIndexOf('/');
       baseUrl=baseUrl.substring(0, pos);
    -->
</script>

<html:form action="/mailloop">
<html:hidden property="action"/>
<html:hidden property="mailloopID"/>
<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">
        <div class="grey_box_left_column">
            <label for="mailing_name"><bean:message key="default.Name"/>:&nbsp;</label>
            <html:text property="shortname" maxlength="99" size="42" styleId="mailing_name"/>
        </div>

        <div class="grey_box_center_column">
            <label for="mailing_name"><bean:message key="default.description"/>:&nbsp;</label>

            <html:textarea property="description" rows="5" cols="32" styleId="mailing_description"/>

        </div>
        <div class="grey_box_right_column"></div>
    </div>
    <div class="grey_box_bottom"></div>
</div>

<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.forward"/></label>

            <div class="mailloop_checkbox">
                <html:checkbox property="doForward"/>
            </div>
        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.forward_adr"/>:&nbsp;</label>

            <html:text property="forwardEmail" maxlength="99" size="42"/>

        </div>
    </div>
    <div class="blue_box_bottom"></div>
</div>
<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.subscribe"/></label>

            <div class="mailloop_checkbox">
                <html:checkbox property="doSubscribe"/>
            </div>
        </div>
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="Mailinglist"/>:&nbsp;</label>
            <html:select property="mailinglistID" size="1">
                <c:forEach var="mailinglist" items="${mailloopForm.mailinglists}">
                    <html:option value="${mailinglist.id}">
                        ${mailinglist.shortname}
                    </html:option>
                </c:forEach>
            </html:select>

        </div>
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.userform"/>:&nbsp;</label>
            <html:select property="userformID" size="1">
                <c:forEach var="userform" items="${mailloopForm.userforms}">
                    <html:option value="${userform.id}">
                        ${userform.formName}
                    </html:option>
                </c:forEach>
            </html:select>

        </div>
    </div>
    <div class="blue_box_bottom"></div>
</div>

<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.autoresponder"/></label>

            <div class="mailloop_checkbox">
                <html:checkbox property="doAutoresponder"/>
            </div>
        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.ar_sender"/>:&nbsp;</label>

            <html:text property="arSender" maxlength="99" size="42"/>

        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="settings.mailloop.ar_subject"/>:&nbsp;</label>

            <html:text property="arSubject" maxlength="99" size="42"/>

        </div>

        <div class="admin_filed_detail_form_item">
            <label><bean:message key="mailing.Text_Version"/>:&nbsp;</label>

            <html:textarea property="arText" rows="14" cols="75"/>

        </div>

        <script type="text/javascript">
            var isFCKEditorActive = false;
            function Toggle() {
                // Try to get the FCKeditor instance, if available.
                var oEditor;
                if (typeof( FCKeditorAPI ) != 'undefined')
                    oEditor = FCKeditorAPI.GetInstance('DataFCKeditor');

                // Get the _Textarea and _FCKeditor DIVs.
                var eTextareaDiv = document.getElementById('Textarea');
                var eFCKeditorDiv = document.getElementById('FCKeditor');

                // If the _Textarea DIV is visible, switch to FCKeditor.
                if (eTextareaDiv.style.display != 'none') {
                    // If it is the first time, create the editor.
                    if (!oEditor) {
                        CreateEditor();
                    }
                    else {
                        // Set the current text in the textarea to the editor.
                        oEditor.SetData(document.getElementById('newContent').value);
                    }

                    // Switch the DIVs display.
                    eTextareaDiv.style.display = 'none';
                    eFCKeditorDiv.style.display = '';

                    // This is a hack for Gecko 1.0.x ... it stops editing when the editor is hidden.
                    if (oEditor && !document.all) {
                        if (oEditor.EditMode == FCK_EDITMODE_WYSIWYG)
                            oEditor.MakeEditable();
                    }
                    isFCKEditorActive = true;
                }
                else {
                    // Set the textarea value to the editor value.
                    document.getElementById('newContent').value = oEditor.GetXHTML();

                    // Switch the DIVs display.
                    eTextareaDiv.style.display = '';
                    eFCKeditorDiv.style.display = 'none';
                    isFCKEditorActive = false;
                }
            }

            function CreateEditor() {
                // Copy the value of the current textarea, to the textarea that will be used by the editor.
                document.getElementById('DataFCKeditor').value = document.getElementById('newContent').value;

                // Automatically calculates the editor base path based on the _samples directory.
                // This is usefull only for these samples. A real application should use something like this:
                // oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.

                // Create an instance of FCKeditor (using the target textarea as the name).

                oFCKeditorNew = new FCKeditor('DataFCKeditor');
                oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false;
                oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>";
                oFCKeditorNew.Config[ "BaseHref" ] = baseUrl + "\"${FCKEDITOR_PATH}\"";
                oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page='<%= new String("/"+ AgnUtils.getEMMProperty("fckpath") +"/emmconfig.jsp?mailingID=0") %>'/>";
                oFCKeditorNew.ToolbarSet = "emm";
                oFCKeditorNew.BasePath = baseUrl + "/${FCKEDITOR_PATH}/";
                oFCKeditorNew.Height = "400"; // 400 pixels
                oFCKeditorNew.Width = "650";
                oFCKeditorNew.ReplaceTextarea();


            }

            // The FCKeditor_OnComplete function is a special function called everytime an
            // editor instance is completely loaded and available for API interactions.
            function FCKeditor_OnComplete(editorInstance) {
                // Switch Image ??
            }

            function PrepareSave() {
                // If the textarea isn't visible update the content from the editor.
                if (document.getElementById('Textarea').style.display == 'none') {
                    var oEditor = FCKeditorAPI.GetInstance('DataFCKeditor');
                    document.getElementById('newContent').value = oEditor.GetXHTML();
                }

            }
            function save() {
                if (isFCKEditorActive == true) {
                    var oEditor = FCKeditorAPI.GetInstance('DataFCKeditor');
                    document.getElementById('newContent').value = oEditor.GetXHTML();
                }
            }


        </script>
        <div class="admin_filed_detail_form_item">
            <label><bean:message key="mailing.HTML_Version"/>:&nbsp;

                <img src="${emmLayoutBase.imagesURL}/edit.gif" border="0"
                     onclick="Toggle();" alt="<bean:message key="htmled.title"/>"></label>

            <div id="Textarea">
                <html:textarea property="arHtml" styleId="newContent" rows="14" cols="75"/>&nbsp;
            </div>
            <div id="FCKeditor" style="display: none">
                <textarea id="DataFCKeditor" rows="14" cols="75"></textarea>
            </div>
        </div>

    </div>
    <div class="blue_box_bottom"></div>

</div>

<div class="button_container" style="padding-top:5px;">
    <agn:ShowByPermission token="mailloop.change">
    	<input type="hidden" name="saveMailloop" value="" id="saveMailloop"/>
	    <div class="action_button" id="save_button"><a href="#"
              onclick="save(); document.getElementById('saveMailloop').value='save'; document.mailloopForm.submit();return false;"><span><bean:message
            key="button.Save"/></span></a></div>
    </agn:ShowByPermission>
    <agn:ShowByPermission token="mailloop.delete">
    	<logic:notEqual name="mailloopForm" property="mailloopID" value="0">
        	<div class="action_button"><html:link
            	    page='<%= new String("/mailloop.do?action=" + MailloopAction.ACTION_CONFIRM_DELETE) + "&mailloopID=" + tmpLoopID + "&fromListPage=false"%>'><span><bean:message
                	key="button.Delete"/></span></html:link>
      		</div>
  		</logic:notEqual>
  	</agn:ShowByPermission>

</div>
</html:form>