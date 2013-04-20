<%-- checked --%>
<%@ page language="java" import="org.agnitas.beans.DynamicTagContent, org.agnitas.beans.Mailing, org.agnitas.util.AgnUtils, org.agnitas.web.MailingContentAction, org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailingWizardForm" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

 <%
    Mailing mailing = (Mailing) request.getAttribute("mailing");
     MailingWizardForm aForm= (MailingWizardForm) request.getAttribute("aForm");
     DynamicTagContent tagContent=null;
    String index=null;
    Integer len= (Integer)request.getAttribute("len");
    int i=1;

  %>

<script type="text/javascript" src="${FCKEDITOR_PATH}/fckeditor.js"></script>

<script type="text/javascript">
    <!--
    var baseUrl=window.location.pathname;
    pos=baseUrl.lastIndexOf('/');
    baseUrl=baseUrl.substring(0, pos);
    -->
</script>


<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span>4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span class="step_active">8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>

    <p><bean:message key="mailing.Text_Module"/>:&nbsp;<%= aForm.getDynName() %></p><br>
<html:form action="/mwTextmodule">
    <html:hidden property="action"/>
    <html:hidden property="contentID"/>
        <logic:iterate id="dyncontent" name="mailingWizardForm" property='<%= \"mailing.dynTags(\"+aForm.getDynName()+\").dynContent\" %>'>
            <% Map.Entry ent2=(Map.Entry)pageContext.getAttribute("dyncontent");
                tagContent=(DynamicTagContent)ent2.getValue();
                index=(String)ent2.getKey(); %>
           <div class="assistant_step7_form_item ">
           <div style="margin-top:150px; float: left; width:130px;">
                    <bean:message key="mailing.Content"/>:
                   <logic:equal value="true" name="mailingWizardForm" property="showHTMLEditorForDynTag">
                       &nbsp;<img src="${emmLayoutBase.imagesURL}/edit.gif" border="0" onclick="Toggle<%= index %>();" alt="<bean:message key='htmled.title'/>">
                   </logic:equal>
                </div>
                <html:hidden property='<%= \"content[\"+index+\"].dynOrder\" %>'/>
                <div id="Textarea<%= index %>" class="mailing_wizard_form_item">
                    <html:textarea styleId="<%= \"content[\"+index+\"].dynContent\" %>" property='<%= \"content[\"+index+\"].dynContent\" %>' rows="20" cols="85"/>&nbsp;
                </div>
               <div id="FCKeditor<%= index %>" style="display: none" class="mailing_wizard_form_item">
                   <textarea id="DataFCKeditor<%= index %>" rows="20" cols="85"></textarea>
               </div>

                <div style="margin-top:150px;">
                <% if (len > 1 && i != 1) { %>
                <input type="image"
                       src="${emmLayoutBase.imagesURL}/button_up.gif" border="0"
                       name="order"
                       onclick="<%= "document.mailingWizardForm.contentID.value=" + tagContent.getDynOrder() + "; document.mailingWizardForm.action.value='textmodule_move_up'" %>">
                <br>
                <% } %>
                <% if (len > 1 && i != len) { %>
                <input type="image"
                       src="${emmLayoutBase.imagesURL}/button_down.gif"
                       border="0" name="order"
                       onclick="<%= "document.mailingWizardForm.contentID.value=" + tagContent.getDynOrder() + "; document.mailingWizardForm.action.value='textmodule_move_down'" %>">
                <% }
                    i++; %>
                    </div>
            </div>
           <div class="assistant_step7_form_item">
            <label><bean:message key="target.Target"/>:&nbsp;</label>
            
            <html:select property='<%= \"content[\"+index+\"].targetID\" %>' size="1">
                <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
		<logic:notEmpty name="targets" scope="request">
			<c:forEach var="dbTarget" items="${targets}">
				<html:option value="${dbTarget.id}">${dbTarget.targetName}</html:option>
			</c:forEach>
		</logic:notEmpty>
            </html:select></div>
    
           <div class="button_container">

                    <%--<input type="hidden" id="save" name="save" value=""/>--%>
                <div class="action_button mailingwizard_add_button">
                    <a href="#"
                       onclick="saveAllHtmlEditors();document.mailingWizardForm.action.value='${ACTION_TEXTMODULE_SAVE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                            key="button.Save"/></span></a>
                </div>
            </div>

            <script type="text/javascript">
                var isFCKEditorActive<%= index %> = false;

                function Toggle<%= index %>() {
                    // Try to get the FCKeditor instance, if available.
                    var oEditor;
                    if (typeof( FCKeditorAPI ) != 'undefined')
                        oEditor = FCKeditorAPI.GetInstance('DataFCKeditor<%= index %>');

                    // Get the _Textarea and _FCKeditor DIVs.
                    var eTextareaDiv = document.getElementById('Textarea<%= index %>');
                    var eFCKeditorDiv = document.getElementById('FCKeditor<%= index %>');

                    // If the _Textarea DIV is visible, switch to FCKeditor.
                    if (eTextareaDiv.style.display != 'none') {
                        // If it is the first time, create the editor.
                        if (!oEditor) {
                            CreateEditor<%= index %>();
                        }
                        else {
                            // Set the current text in the textarea to the editor.
                            oEditor.SetData(document.getElementById('content[<%= index %>].dynContent').value);
                        }

                        // Switch the DIVs display.
                        eTextareaDiv.style.display = 'none';
                        eFCKeditorDiv.style.display = '';

                        // This is a hack for Gecko 1.0.x ... it stops editing when the editor is hidden.
                        if (oEditor && !document.all) {
                            if (oEditor.EditMode == FCK_EDITMODE_WYSIWYG)
                                oEditor.MakeEditable();
                        }

                        isFCKEditorActive<%= index %> = true;
                    }
                    else {
                        // Set the textarea value to the editor value.
                        document.getElementById('content[<%= index %>].dynContent').value = oEditor.GetXHTML();

                        // Switch the DIVs display.
                        eTextareaDiv.style.display = '';
                        eFCKeditorDiv.style.display = 'none';
                        isFCKEditorActive<%= index %> = false;
                    }
                }

                function CreateEditor<%= index %>() {
                    // Copy the value of the current textarea, to the textarea that will be used by the editor.
                    document.getElementById('DataFCKeditor<%= index %>').value = document.getElementById('content[<%= index %>].dynContent').value;

                    // Automatically calculates the editor base path based on the _samples directory.
                    // This is usefull only for these samples. A real application should use something like this:
                    // oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.

                    // Create an instance of FCKeditor (using the target textarea as the name).

                    oFCKeditorNew = new FCKeditor('DataFCKeditor<%= index %>');
                    oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false;
                    oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>";
                    oFCKeditorNew.Config[ "BaseHref" ] = baseUrl + "/${FCKEDITOR_PATH}/";
                    oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page='<%= new String(\"/\"+AgnUtils.getEMMProperty(\"fckpath\")+\"/emmconfig.jsp?mailingID=\"+mailing.getId()) %>'/>";
                    oFCKeditorNew.ToolbarSet = "emm";
                    oFCKeditorNew.BasePath = baseUrl + "/${FCKEDITOR_PATH}/";
                    oFCKeditorNew.Height = "400"; // 400 pixels
                    oFCKeditorNew.Width = "650";
                    oFCKeditorNew.ReplaceTextarea();

                }

                // The FCKeditor_OnComplete function is a special function called everytime an
                // editor instance is completely loaded and available for API interactions.
                function FCKeditor_OnComplete(editorInstance) {
                }

                function save<%= index %>() {
                    if (isFCKEditorActive<%= index %> == true || document.getElementById('Textarea<%= index %>').style.display == 'none') {
                        var oEditor = FCKeditorAPI.GetInstance('DataFCKeditor<%= index %>');
                        document.getElementById('content[<%= index %>].dynContent').value = oEditor.GetXHTML();
                    }
                }

            </script>

        </logic:iterate>

    <script type="text/javascript">
        function saveAllHtmlEditors() {
        <logic:iterate id="dyncontent" name="mailingWizardForm" property='<%= \"mailing.dynTags(\"+aForm.getDynName()+\").dynContent\" %>'>
        <% Map.Entry ent2 = (Map.Entry) pageContext.getAttribute("dyncontent");
            tagContent = (DynamicTagContent) ent2.getValue();
            index = (String) ent2.getKey(); %>
            save<%= index %>();
        </logic:iterate>
        }
    </script>


      <script type="text/javascript">
       var isFCKEditorActive=false;
       function Toggle()
		{
				// Try to get the FCKeditor instance, if available.
			var oEditor ;
			if ( typeof( FCKeditorAPI ) != 'undefined' )
				oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor' ) ;

			// Get the _Textarea and _FCKeditor DIVs.
			var eTextareaDiv	= document.getElementById( 'Textarea' ) ;
			var eFCKeditorDiv	= document.getElementById( 'FCKeditor' ) ;

			// If the _Textarea DIV is visible, switch to FCKeditor.
			if ( eTextareaDiv.style.display != 'none' )
			{
			// If it is the first time, create the editor.
			if ( !oEditor )
			{
				CreateEditor() ;
			}
			else
			{
				// Set the current text in the textarea to the editor.
				oEditor.SetData( document.getElementById('newContent').value ) ;
			}

			// Switch the DIVs display.
			eTextareaDiv.style.display = 'none' ;
			eFCKeditorDiv.style.display = '' ;

			// This is a hack for Gecko 1.0.x ... it stops editing when the editor is hidden.
			if ( oEditor && !document.all )
			{
				if ( oEditor.EditMode == FCK_EDITMODE_WYSIWYG )
				oEditor.MakeEditable() ;
			}
			isFCKEditorActive=true;
		}
		else
		{
			// Set the textarea value to the editor value.
			document.getElementById('newContent').value = oEditor.GetXHTML() ;

			// Switch the DIVs display.
			eTextareaDiv.style.display = '' ;
			eFCKeditorDiv.style.display = 'none' ;
			isFCKEditorActive=false;
		}
	}

	function CreateEditor()
	{
		// Copy the value of the current textarea, to the textarea that will be used by the editor.
		document.getElementById('DataFCKeditor').value = document.getElementById('newContent').value ;

		// Automatically calculates the editor base path based on the _samples directory.
		// This is usefull only for these samples. A real application should use something like this:
		// oFCKeditor.BasePath = '/fckeditor/' ;	// '/fckeditor/' is the default value.
	
		// Create an instance of FCKeditor (using the target textarea as the name).
		
		oFCKeditorNew = new FCKeditor( 'DataFCKeditor' ) ;
        oFCKeditorNew.Config[ "AutoDetectLanguage" ] = false ;
        oFCKeditorNew.Config[ "DefaultLanguage" ] = "<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage() %>" ;
        oFCKeditorNew.Config[ "BaseHref" ] = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Config[ "CustomConfigurationsPath" ] = "<html:rewrite page='<%= new String("/"+AgnUtils.getEMMProperty("fckpath")+"/emmconfig.jsp?mailingID="+mailing.getId()) %>'/>" ;
        oFCKeditorNew.ToolbarSet = "emm" ;
        oFCKeditorNew.BasePath = baseUrl+"/${FCKEDITOR_PATH}/" ;
        oFCKeditorNew.Height = "400" ; // 400 pixels
        oFCKeditorNew.Width = "650" ;
        oFCKeditorNew.ReplaceTextarea();
	}

	// The FCKeditor_OnComplete function is a special function called everytime an
	// editor instance is completely loaded and available for API interactions.
	function FCKeditor_OnComplete( editorInstance )
	{
		// Switch Image ??
	}

	function PrepareSave()
	{
		// If the textarea isn't visible update the content from the editor.
		if ( document.getElementById( 'Textarea' ).style.display == 'none' )
		{
			var oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor' ) ;
			document.getElementById( 'newContent' ).value = oEditor.GetXHTML() ;
		}
		
	}
	function save() {
		if(isFCKEditorActive == true)  {
			var oEditor = FCKeditorAPI.GetInstance( 'DataFCKeditor' ) ;
			document.getElementById('newContent').value = oEditor.GetXHTML() ;
		}
	}
        </script>
       
      <div class="assistant_step7_form_item ">
    <span class="head3"><bean:message key="mailing.New_Content"/></span>
        </div>
          <div class="assistant_step7_form_item " >
             <div class="tooltiphelp mailing_wizard_form_item" style="margin-top: 150px; width:130px;" id="content"><bean:message key="mailing.Content"/>:
                 <logic:equal value="true" name="mailingWizardForm" property="showHTMLEditorForDynTag">
                    &nbsp;<img src="${emmLayoutBase.imagesURL}/edit.gif" border="0" onclick="Toggle();" alt="<bean:message key='htmled.title'/>">
                 </logic:equal>
            </div>
                <script type="text/javascript">
					var hb1 = new HelpBalloon({
						dataURL: 'help_${helplanguage}/mailingwizard/step_08/Content.xml'
						});
						$('content').appendChild(hb1.icon);
				</script>

            <div id="Textarea" class="mailing_wizard_form_item">
        		<html:textarea  property="newContent" styleId="newContent" rows="20" cols="85"/>&nbsp;
        	</div>
        	<div id="FCKeditor" style="display: none" class="mailing_wizard_form_item">
        		<textarea  id="DataFCKeditor" rows="20" cols="85"></textarea>
        	</div>
           </div>
       <div class="assistant_step7_form_item">
           <div class="tooltiphelp mailing_wizard_form_item" style="width: 130px;" id="target"><bean:message key="target.Target"/>:</div>
                <script type="text/javascript">
					var hb2 = new HelpBalloon({
						dataURL: 'help_${helplanguage}/mailingwizard/step_08/Target.xml'
						});
						$('target').appendChild(hb2.icon);
				</script>

            <html:select property="targetID" size="1" styleId="targetID">
            <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
			<logic:notEmpty name="targets" scope="request">
	            <c:forEach var="dbTarget" items="${targets}">
    	            <html:option value="${dbTarget.id}">${dbTarget.targetName}</html:option>
        	    </c:forEach>
			</logic:notEmpty>
        </html:select>

        </div>
     <div class="button_container">

        <%--<input type="hidden" id="save" name="save" value=""/>--%>
        <div class="action_button mailingwizard_add_button">
            <a href="#" onclick="save();document.mailingWizardForm.action.value='${ACTION_TEXTMODULE_ADD}'; document.mailingWizardForm.submit(); return false;"><span><bean:message key="button.Add"/></span></a>
        </div>

    </div>

    <div class="assistant_step7_button_container">
             <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Finish"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='skip'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Skip"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TEXTMODULE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>

</html:form>
</div>