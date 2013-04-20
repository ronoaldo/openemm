<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.AgnUtils, org.agnitas.util.SafeString, org.agnitas.web.ExportWizardAction" %>
<%@ page import="org.agnitas.web.StrutsActionBase" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>


<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'exportwizard';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;

    window.onload = onPageLoad;
</script>
  <html:form action="/exportwizard">
      <html:hidden property="action"/>

      <div class="export_wizard_content">
          <ul class="new_mailing_step_display">
              <li class="step_display_first"><span class="step_active">1</span></li>
              <li><span>2</span></li>
              <li><span>3</span></li>
          </ul>

          <div class="export_wizard_message">
              <bean:message key="export.SelectExportDef"/>:<br>&nbsp;<br>
          </div>
      </div>

      <c:set var="index" value="0" scope="request"/>

 <display:table class="list_table"
         pagesize="20" id="exportwizard" list="${exportWizardForm.exportPredefList}"
         requestURI="/exportwizard.do"
         length="${exportWizardForm.exportPredefCount}">
    <display:column headerClass="exportwizard_head_name" sortable="false" titleKey="default.Name">
      <span class="ie7hack">
         <html:link page="/exportwizard.do?action=${ACTION_QUERY}&exportPredefID=${exportwizard.id}"><b>${exportwizard.shortname}
           </b></html:link>&nbsp;&nbsp;
      </span>
    </display:column>
    <display:column headerClass="exportwizard_head_description" sortable="false" titleKey="default.description">
      <span class="ie7hack">
         <html:link page="/exportwizard.do?action=${ACTION_QUERY}&exportPredefID=${exportwizard.id}">
             ${exportwizard.description}
         </html:link>&nbsp;&nbsp;
      </span>
    </display:column>
    <display:column title="&nbsp;">
        <html:link styleClass="mailing_edit" titleKey="export.ExportEdit"
              page="/exportwizard.do?action=${ACTION_QUERY}&exportPredefID=${exportwizard.id}"></html:link>
        <html:link styleClass="mailing_delete" titleKey="export.ExportDelete"
              page="/exportwizard.do?action=${ACTION_CONFIRM_DELETE}&exportPredefID=${exportwizard.id}"></html:link>
    </display:column>
</display:table>


    <br clear="both" />
    <div class="export_wizard_content new_exp_wizard_button_container">

      <div class="action_button">
          <html:link
                  page="/exportwizard.do?action=${ACTION_QUERY}&exportPredefID=0">
              <span><bean:message key="button.New"/></span></html:link>
      </div>
      </div>
  </html:form>

<script type="text/javascript">
    table = document.getElementById('exportwizard');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#exportwizard tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>