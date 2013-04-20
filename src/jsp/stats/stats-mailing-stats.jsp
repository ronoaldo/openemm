<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'mailingStat';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
    minWidthLast = 150;
</script>

<html:form action="/mailing_stat">
    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.mailingStatForm.submit(); return false;"><span><bean:message
                key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${mailingStatForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

</html:form>

<display:table class="list_table" id="mailingStat" name="mailingStatlist" excludedParams="*"
               pagesize="${mailingStatForm.numberofRows}"
               requestURI="/mailing_stat.do?action=${ACTION_LIST}&__fromdisplaytag=true">
    <display:column headerClass="mailing_stat_mailing" class="name" titleKey="Mailing" sortable="true">
        <span class="ie7hack">
            <html:link page="/mailing_stat.do?action=${ACTION_MAILINGSTAT}&mailingID=${mailingStat.mailingid}">
                ${mailingStat.shortname}
            </html:link>
        </span>
    </display:column>
    <display:column headerClass="mailing_stat_desc" class="description" titleKey="default.description">
        <span class="ie7hack">
            <html:link page="/mailing_stat.do?action=${ACTION_MAILINGSTAT}&mailingID=${mailingStat.mailingid}">
                ${mailingStat.description}
            </html:link>
        </span>
    </display:column>
    <display:column titleKey="Mailinglist" property="listname" sortable="true"/>
</display:table>

<script type="text/javascript">
    table = document.getElementById('mailingStat');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#mailingStat tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>

