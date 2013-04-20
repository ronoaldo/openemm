<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.ExportWizardAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<div class="export_wizard_content">
    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span class="step_active">3</span></li>
    </ul>

    <div id="advanced_search_top"></div>
    <div id="advanced_search_content">
        <iframe name="ins_status" src="<html:rewrite page='<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_VIEW_STATUS_WINDOW) %>'/>" ALLOWTRANSPARENCY="true" width="888" height="140" bgcolor="#73A2D0" scrolling="no" frameborder="0">
            <bean:message key="import.csv_no_iframe"/>
        </iframe>
    </div>
    <div id="advanced_search_bottom"></div>
</div>

<div style="float:left">
<html:form action="/exportwizard">
    <html:hidden styleId="action" property="action" value="2"/>
    <html:hidden property="exportPredefID"/>


    <div class="button_container export_step2_buttons_panel">
        <input type="hidden" id="exp_back" name="exp_back" value=""/>

        <div class="save_toggle toggle_closed">
            <html:link target="_parent"
                       page='<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_SAVE_QUESTION) %>'>
                <span><bean:message key="button.Save"/></span>
            </html:link>
        </div>
                <script type="text/javascript">
            var hb1 = new HelpBalloon({
                dataURL: 'help_${helplanguage}/exportwizard/step_3/ExportDefSave.xml'
            });
            $('exportDefSave').appendChild(hb1.icon);
        </script>
        <div class="action_button"><a href="#"
                                          onclick="document.getElementById('exp_back').value='exp_back'; document.getElementById('action').value=${ACTION_QUERY}; document.exportWizardForm.submit();"><span><bean:message
                key="button.Back"/></span></a></div>
    </div>

</html:form>
    </div>
              
<% out.flush(); %>
