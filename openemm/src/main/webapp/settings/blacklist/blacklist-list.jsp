<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.beans.BlackListEntry, org.agnitas.web.BlacklistAction, java.net.URLEncoder" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<script type="text/javascript">
<!--
	function parametersChanged(){
		document.getElementsByName('blacklistForm')[0].numberOfRowsChanged.value = true;
	}
//-->
</script>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;
   document.onmousemove = drag;
   document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>


  <html:form action="/blacklist" >
    <html:hidden property="action" value="${ACTION_SAVE}" />
<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">
        <div class="blacklist_controls_panel">
            <div class="blacklist_controls_panel_row">
                <label><bean:message key="blacklist.add"/></label>
                <html:text property="newemail" styleClass="blacklist_input"/>
            </div>
            <div class="action_button blacklist_button">
                <a href="#" onclick="document.forms[0].action.value=${ACTION_SAVE}; document.blacklistForm.submit(); return false;">
                    <span><bean:message key="button.Add"/></span>
                </a>
            </div>

        </div>
    </div>
    <div class="grey_box_bottom"></div>
</div>

<div class="list_settings_container">
    <div class="filterbox_form_button"><a href="#" onclick="document.forms[0].action.value=${ACTION_LIST}; document.blacklistForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
    <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
    <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
            for="list_settings_length_0">20</label></div>
    <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
            for="list_settings_length_1">50</label></div>
    <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
            for="list_settings_length_2">100</label></div>
    <logic:iterate collection="${blacklistForm.columnwidthsList}" indexId="i" id="width">
       <html:hidden property="columnwidthsList[${i}]"/>
    </logic:iterate>
</div>
<display:table class="list_table" pagesize="${blacklistForm.numberofRows}" id="recipient" name="blackListEntries" sort="external" requestURI="/blacklist.do?action=${ACTION_LIST}&__fromdisplaytag=true" excludedParams="*" size="${blackListEntries.fullListSize}"  partialList="true" >
    		<display:column class="email" headerClass="head_email" property="email"  titleKey="mailing.E-Mail" sortable="true" />
    		<display:column class="senddate" headerClass="head_senddate" property="date" sortName="creation_date" titleKey="statistic.Date" sortable="true" format="{0,date,yyyy-MM-dd}" />

    		<agn:ShowByPermission token="recipient.delete">
    			 <display:column title="&nbsp;">
            <html:link styleClass="mailing_delete" titleKey="blacklist.BlacklistDelete"
                page='<%= "/blacklist.do?action="+BlacklistAction.ACTION_CONFIRM_DELETE+"&delete=" + URLEncoder.encode(((BlackListEntry)pageContext.getAttribute("recipient")).getEmail() , "UTF-8") %>'/>
                 </display:column>
            </agn:ShowByPermission>
    	</display:table>
      <script type="text/javascript">
          table = document.getElementById('recipient');
          rewriteTableHeader(table);
          writeWidthFromHiddenFields(table);

          $$('#recipient tbody tr').each(function(item) {
              item.observe('mouseover', function() {
                  item.addClassName('list_highlight');
              });
              item.observe('mouseout', function() {
                  item.removeClassName('list_highlight');
              });
          });
      </script>

</html:form>
