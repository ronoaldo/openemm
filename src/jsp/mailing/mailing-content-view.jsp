<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.beans.DynamicTagContent, org.agnitas.util.AgnUtils, org.agnitas.web.MailingContentAction, org.agnitas.web.MailingContentForm, org.agnitas.web.forms.MailingBaseForm, java.util.Locale"
         contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% pageContext.setAttribute("FCKEDITOR_PATH", AgnUtils.getEMMProperty("fckpath")); %>

<script type="text/javascript" src="${FCKEDITOR_PATH}/fckeditor.js"></script>

<script type="text/javascript">
    <!--
    var baseUrl=window.location.pathname;
    pos=baseUrl.lastIndexOf('/');
    baseUrl=baseUrl.substring(0, pos);
    -->
</script>

<% int tmpMailingID = 0;
    MailingContentForm aForm = null;
    if (session.getAttribute("mailingContentForm") != null) {
        aForm = (MailingContentForm) session.getAttribute("mailingContentForm");
        tmpMailingID = aForm.getMailingID();
    }
    MailingBaseForm baseForm = (MailingBaseForm) session.getAttribute("mailingBaseForm");
    DynamicTagContent tagContent = null;
    String index = null;
    int i = 1;
    int len = aForm.getContent().size();
    aForm.setNewContent("");
%>

<%
    DynamicTagContent tagContent1=null;
    String index1=null;
%>

<html:form action="/mailingcontent" styleId="contentform">
<html:hidden property="dynNameID"/>
<html:hidden property="action"/>
<html:hidden property="mailingID"/>
<html:hidden property="contentID"/>

<div class="text_module_name_container">
    <div class="filterbox_form_container">
        <div id="filterbox_top"></div>
        <div class="grey_box_content grey_box_content_ie_fix">
            <label class="text_module_name_label"><bean:message key="mailing.Text_Module"/>:</label>${mailingContentForm.dynName}
        </div>
        <div id="filterbox_bottom"></div>
    </div>
</div>

<div class="blue_box_container">

<c:set var="contentHTMLCodeTitle" value="mailingContentText" scope="request" />
<logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
    <c:set var="contentHTMLCodeTitle" value="mailingContentHTMLCode" scope="request"/>
</logic:equal>

<logic:iterate id="dyncontent" name="mailingContentForm" property="content">
<% Map.Entry ent2 = (Map.Entry) pageContext.getAttribute("dyncontent");
    tagContent = (DynamicTagContent) ent2.getValue();
    index = (String) ent2.getKey();
    pageContext.setAttribute("index", index);
%>
<script type="text/javascript">
    // have a look @ sample13.html from the fckeditor docs
    var isFCKEditorActive<%= tagContent.getId() %> = false;

    function Toggle<%= tagContent.getId() %>(showFCKEditor)
    {
        // Try to get the FCKeditor instance, if available.
        var oEditor;
        if (typeof( FCKeditorAPI ) != 'undefined')
            oEditor = FCKeditorAPI.GetInstance('DataFCKeditor<%= tagContent.getId() %>');

        // Get the _Textarea and _FCKeditor DIVs.
        var eTextareaDiv = document.getElementById('Textarea<%= tagContent.getId() %>') ;
        var eFCKeditorDiv = document.getElementById('FCKeditor<%= tagContent.getId() %>') ;

        // If the _Textarea DIV is visible, switch to FCKeditor.
        if (showFCKEditor)
        {
            // If it is the first time, create the editor.
            if (!oEditor)
            {
                CreateEditor<%= tagContent.getId() %>();
            }
            else
            {
                // Set the current text in the textarea to the editor.
                oEditor.SetData(document.getElementById('content_<%= index %>_.dynContent').value);
            }

            // Switch the DIVs display.
            eTextareaDiv.style.display = 'none';
            eFCKeditorDiv.style.display = '';

            document.getElementById('LIHTMLEditor<%= tagContent.getId() %>').className = 'stat_tab_right';
            document.getElementById('AHTMLEditor<%= tagContent.getId() %>').className = 'stat_tab_left';
            document.getElementById('LIHTMLCode<%= tagContent.getId() %>').className = '';
            document.getElementById('AHTMLCode<%= tagContent.getId() %>').className = '';

            // This is a hack for Gecko 1.0.x ... it stops editing when the editor is hidden.
            if (oEditor && !document.all)
            {
                if (oEditor.EditMode == FCK_EDITMODE_WYSIWYG)
                    oEditor.MakeEditable();
            }

            isFCKEditorActive<%= tagContent.getId() %> = true;
        }
        else
        {
            // Set the textarea value to the editor value.
            if(oEditor)
                document.getElementById('content_<%= index %>_.dynContent').value = oEditor.GetXHTML();

            // Switch the DIVs display.
            <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
                eFCKeditorDiv.style.display = 'none';
                document.getElementById('LIHTMLEditor<%= tagContent.getId() %>').className = '';
                document.getElementById('AHTMLEditor<%= tagContent.getId() %>').className = '';
            </logic:equal>
            eTextareaDiv.style.display = '';

            document.getElementById('LIHTMLCode<%= tagContent.getId() %>').className = 'stat_tab_right';
            document.getElementById('AHTMLCode<%= tagContent.getId() %>').className = 'stat_tab_left';

            isFCKEditorActive<%= tagContent.getId() %> = false;
        }
    }

    function CreateEditor<%= tagContent.getId() %>()
    {
        // Copy the value of the current textarea, to the textarea that will be used by the editor.
        document.getElementById('DataFCKeditor<%= tagContent.getId() %>').value = document.getElementById('content_<%= index %>_.dynContent').value;

        // Automatically calculates the editor base path based on the _samples directory.
        // This is usefull only for these samples. A real application should use something like this:
        // oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.

        // Create an instance of FCKeditor (using the target textarea as the name).

        oFCKeditorNew = new FCKeditor('DataFCKeditor<%= tagContent.getId() %>');
        oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false;
        oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>";
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl + "/${FCKEDITOR_PATH}/";
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page='<%= new String(\"/\"+AgnUtils.getEMMProperty(\"fckpath\")+\"/emmconfig.jsp?mailingID=\"+tmpMailingID) %>'/>";
        oFCKeditorNew.ToolbarSet = "emm";
        oFCKeditorNew.BasePath = baseUrl + "/${FCKEDITOR_PATH}/";
        if ("${mailingContentForm.dynName}" == "HTML-Version") {
            oFCKeditorNew.Config[ "FullPage" ] = "true";
        }
        oFCKeditorNew.Height = "400"; // 400 pixels
        oFCKeditorNew.Width = "650";
        oFCKeditorNew.ReplaceTextarea();


    }

    // The FCKeditor_OnComplete function is a special function called everytime an
    // editor instance is completely loaded and available for API interactions.
    function FCKeditor_OnComplete(editorInstance)
    {
    }

    function save<%= tagContent.getId() %>() {
        if (isFCKEditorActive<%= tagContent.getId() %> == true || document.getElementById('Textarea<%= tagContent.getId() %>').style.display == 'none') {
            var oEditor = FCKeditorAPI.GetInstance('DataFCKeditor<%= tagContent.getId() %>') ;
            document.getElementById('content_<%= index %>_.dynContent').value = oEditor.GetXHTML();
        }
    }

</script>

<div class="assistant_step7_form_item ">
    <div class="switch_content_tabs" style="width:705px;">
        <ul>
            <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
                <li id="LIHTMLEditor<%= tagContent.getId() %>"><a
                        id="AHTMLEditor<%= tagContent.getId() %>" href="#"
                        onclick="Toggle<%= tagContent.getId() %>(true);">
                    <img src="${emmLayoutBase.imagesURL}/icon_html_version.png"
                         border="0"/><bean:message key="mailingContentHTMLEditor"/></a>
                </li>
            </logic:equal>

            <li id="LIHTMLCode<%= tagContent.getId() %>" class="stat_tab_right">
                <a id="AHTMLCode<%= tagContent.getId() %>" href="#" class="stat_tab_left"
                   onclick="Toggle<%= tagContent.getId() %>(false);"><img
                        src="${emmLayoutBase.imagesURL}/icon_profis.png" border="0"/>&nbsp;<bean:message
                        key="${contentHTMLCodeTitle}"/></a>
            </li>
        </ul>

    </div>
    <div id="Textarea<%= tagContent.getId() %>" style="float:left;">
        <html:hidden property='<%= new String(\"content(\"+index+\").dynOrder\") %>'/>
        <html:textarea property='<%= \"content(\"+index+\").dynContent\" %>'
                       styleId="<%= \"content_\"+index+\"_.dynContent\" %>" rows="20" cols="85"
                       readonly="<%= aForm.isWorldMailingSend() %>"/>&nbsp;
    </div>
    <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
        <div id="FCKeditor<%= tagContent.getId() %>" style="display: none" class="mailing_wizard_form_item">
            <textarea id="DataFCKeditor<%= tagContent.getId() %>" rows="20" cols="85"
                      readonly="<%= aForm.isWorldMailingSend() %>"></textarea>
        </div>
    </logic:equal>
    <c:if test="<%= len > 1%>">
        <div style="margin-top:150px;">
            <% if (len > 1 && i != 1) { %>
            <input type="image" src="${emmLayoutBase.imagesURL}/button_top.gif"
                   border="0" name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_TOP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <br>
            <input type="image" src="${emmLayoutBase.imagesURL}/button_up.gif"
                   border="0" name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_UP +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <br>
            <% } %>
            <% if (len > 1 && i != len) { %>
            <input type="image" src="${emmLayoutBase.imagesURL}/button_down.gif"
                   border="0" name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_DOWN +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <br>
            <input type="image"
                   src="${emmLayoutBase.imagesURL}/button_bottom.gif" border="0"
                   name="order"
                   onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_CHANGE_ORDER_BOTTOM +";document.getElementById('contentform').contentID.value="+tagContent.getId() %>">
            <% }
                i++; %>
        </div>
    </c:if>
</div>
<div class="assistant_step7_form_item">
    <label><bean:message key="target.Target"/>:&nbsp;</label>
    <c:set var="targetGroupDeleted" value="0" scope="page"/>
    <bean:define id="targetGroupSelected" name="mailingContentForm" property='<%= "content("+index+").targetID" %>'
                 toScope="page"/>

    <html:select property='<%= \"content(\"+index+\").targetID\" %>' size="1"
                 disabled="<%= aForm.isWorldMailingSend() %>">
        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
        <logic:iterate id="target" name="targetGroups" scope="request">
            <c:if test="${(target.deleted == 0) || (target.id == targetGroupSelected)}">
                <html:option value="${target.id}">${target.targetName}
                    <c:if test="${target.deleted != 0}">
                        &nbsp;(<bean:message key="target.Deleted"/>)
                        <c:set var="targetGroupDeleted" value="1" scope="page"/>
                    </c:if>
                </html:option>
            </c:if>
        </logic:iterate>
    </html:select>

    <c:if test="${targetGroupDeleted != 0}">
        <span class="warning">(<bean:message key="target.Deleted"/>)</span>
    </c:if>
</div>

<div class="button_container text_module_buttons">

    <% if (!aForm.isWorldMailingSend()) {%>
    <input type="hidden" id="save" name="save" value=""/>

    <div class="action_button mailingwizard_add_button">
        <a href="#"
           onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_SAVE_TEXTBLOCK +"; document.getElementById('contentform').save.value='save'; saveAllHtmlEditors();document.getElementById('contentform').submit(); return false;" %>"><span><bean:message
                key="button.Save"/></span></a>
    </div>
    <div class="action_button mailingwizard_add_button">
        <a href="#"
           onclick="<%= " document.getElementById('contentform').action.value="+MailingContentAction.ACTION_SAVE_TEXTBLOCK_AND_BACK +";document.getElementById('contentform').save.value='save'; saveAllHtmlEditors();document.getElementById('contentform').submit(); return false;" %>"><span><bean:message
                key="button.SaveAndBack"/></span></a>
    </div>
    <div class="action_button mailingwizard_add_button">
        <a href="#"
           onclick="<%= "document.getElementById('contentform').action.value="+MailingContentAction.ACTION_DELETE_TEXTBLOCK +";document.getElementById('contentform').contentID.value="+tagContent.getId()+"; ;document.getElementById('contentform').submit(); return false;" %>"><span><bean:message
                key="button.Delete"/></span></a>
    </div>


    <%}%>
    <div class="action_button mailingwizard_add_button">
        <html:link
                page='<%= new String(\"/mailingcontent.do?action=\" + MailingContentAction.ACTION_VIEW_CONTENT + \"&mailingID=\" + tmpMailingID) %>'><span><bean:message
                key="button.Back"/></span></html:link>
    </div>
</div>

</logic:iterate>

<script type="text/javascript">
    function saveAllHtmlEditors() {
    <logic:iterate id="dyncontent" name="mailingContentForm" property="content">
    <% Map.Entry ent2 = (Map.Entry) pageContext.getAttribute("dyncontent");
        tagContent = (DynamicTagContent) ent2.getValue();%>
        save<%= tagContent.getId() %>();
    </logic:iterate>
    }
</script>

<div class="dotted_line new_text_module_separator"></div>

<div class="assistant_step7_form_item new_text_module_label">
    <span class="head3"><bean:message key="mailing.New_Content"/></span>
</div>
<script type="text/javascript">
    var isFCKEditorActive = false;

    Event.observe(window, 'load', function() {
        Toggle(false);
        <logic:iterate id="dyncontent" name="mailingContentForm" property="content">
            <% Map.Entry ent2=(Map.Entry)pageContext.getAttribute("dyncontent");
            tagContent1=(DynamicTagContent)ent2.getValue();
            index1=(String)ent2.getKey();
            pageContext.setAttribute("index",index1);
            %>
            Toggle<%= tagContent1.getId() %>(false);
        </logic:iterate>
    });

    function Toggle(showFCKEditor)
    {
        // Try to get the FCKeditor instance, if available.
        var oEditor ;
        if (typeof( FCKeditorAPI ) != 'undefined')
            oEditor = FCKeditorAPI.GetInstance('DataFCKeditor');

        // Get the _Textarea and _FCKeditor DIVs.
        var eTextareaDiv = document.getElementById('Textarea') ;
        var eFCKeditorDiv = document.getElementById('FCKeditor') ;

        // If the _Textarea DIV is visible, switch to FCKeditor.
        if (showFCKEditor)
        {
            // If it is the first time, create the editor.
            if (!oEditor)
            {
                CreateEditor();
            }
            else
            {
                // Set the current text in the textarea to the editor.
                oEditor.SetData(document.getElementById('newContent').value);
            }

            // Switch the DIVs display.
            eTextareaDiv.style.display = 'none';
            eFCKeditorDiv.style.display = '';

            document.getElementById('LIHTMLEditor').className = 'stat_tab_right';
            document.getElementById('AHTMLEditor').className = 'stat_tab_left';
            document.getElementById('LIHTMLCode').className = '';
            document.getElementById('AHTMLCode').className = '';

            // This is a hack for Gecko 1.0.x ... it stops editing when the editor is hidden.
            if (oEditor && !document.all)
            {
                if (oEditor.EditMode == FCK_EDITMODE_WYSIWYG)
                    oEditor.MakeEditable();
            }
            isFCKEditorActive = true;
        }
        else
        {
            // Set the textarea value to the editor value.
            if(oEditor)
                document.getElementById('newContent').value = oEditor.GetXHTML();

            // Switch the DIVs display.
            <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
                eFCKeditorDiv.style.display = 'none';
                document.getElementById('LIHTMLEditor').className = '';
                document.getElementById('AHTMLEditor').className = '';
            </logic:equal>

            eTextareaDiv.style.display = '';
            document.getElementById('LIHTMLCode').className = 'stat_tab_right';
            document.getElementById('AHTMLCode').className = 'stat_tab_left';

            isFCKEditorActive = false;
        }
    }

    function CreateEditor()
    {
        // Copy the value of the current textarea, to the textarea that will be used by the editor.
        document.getElementById('DataFCKeditor').value = document.getElementById('newContent').value;

        // Automatically calculates the editor base path based on the _samples directory.
        // This is usefull only for these samples. A real application should use something like this:
        // oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.

        // Create an instance of FCKeditor (using the target textarea as the name).

        oFCKeditorNew = new FCKeditor('DataFCKeditor');
        oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false;
        oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>";
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl + "/${FCKEDITOR_PATH}/";
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page='<%= new String(\"/\"+AgnUtils.getEMMProperty(\"fckpath\") +\"/emmconfig.jsp?mailingID=\"+tmpMailingID) %>'/>";
        oFCKeditorNew.ToolbarSet = "emm";
        oFCKeditorNew.BasePath = baseUrl + "/${FCKEDITOR_PATH}/";
        if ("${mailingContentForm.dynName}" == "HTML-Version") {
            oFCKeditorNew.Config[ "FullPage" ] = "true";
        }
        oFCKeditorNew.Height = "400"; // 400 pixels
        oFCKeditorNew.Width = "650";
        oFCKeditorNew.ReplaceTextarea();


    }
    function saveMe() {
        if (isFCKEditorActive == true || document.getElementById('Textarea').style.display == 'none') {
            var oEditor = FCKeditorAPI.GetInstance('DataFCKeditor') ;
            document.getElementById('newContent').value = oEditor.GetXHTML();
        }
    }


</script>
<div class="assistant_step7_form_item ">
    <div class="switch_content_tabs">
        <ul>
            <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
                <li id="LIHTMLEditor"><a id="AHTMLEditor" href="#" onclick="Toggle(true);">
                    <img src="${emmLayoutBase.imagesURL}/icon_html_version.png" border="0"/><bean:message
                        key="mailingContentHTMLEditor"/></a></li>
            </logic:equal>
            <li id="LIHTMLCode"><a id="AHTMLCode" href="#" onclick="Toggle(false);"><img
                    src="${emmLayoutBase.imagesURL}/icon_profis.png" border="0"/>&nbsp;<bean:message
                    key="${contentHTMLCodeTitle}"/></a></li>
        </ul>

    </div>
    <div id="Textarea">
        <html:textarea property="newContent" styleId="newContent" rows="20" styleClass="new_text_module_textarea"/>&nbsp;
    </div>
    <logic:equal name="mailingContentForm" property="showHTMLEditor" value="true">
        <div id="FCKeditor" style="display: none">
            <textarea id="DataFCKeditor" rows="20" styleClass="new_text_module_textarea"></textarea>
        </div>
    </logic:equal>
</div>
<div class="assistant_step7_form_item">
    <label for="newTargetID"><bean:message key="target.Target"/>:&nbsp;</label>
    <html:select property="newTargetID" size="1" disabled="<%= aForm.isWorldMailingSend() %>">
        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
        <logic:iterate id="target" name="targetGroups" scope="request">
        	<c:if test="${target.deleted == 0}">
	            <html:option value="${target.id}">${target.targetName}</html:option>
	        </c:if>
        </logic:iterate>
    </html:select>
</div>
<div class="button_container">

    <div class="action_button mailingwizard_add_button">
        <a href="#" id="dummy" onclick="saveMe(); document.getElementById('contentform').action.value=${ACTION_ADD_TEXTBLOCK}; document.getElementById('contentform').submit(); return false;">
            <span><bean:message key="button.Add"/></span>
        </a>
    </div>

    <div class="action_button mailingwizard_add_button">
        <html:link
                page='<%= new String("/mailingcontent.do?action=" + MailingContentAction.ACTION_VIEW_CONTENT + "&mailingID=" + tmpMailingID) %>'><span><bean:message
                key="button.Back"/></span></html:link>
    </div>

</div>

</html:form>
</div>

