<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.CampaignAction, org.agnitas.web.MailingBaseAction" %>
<%@ page import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%
    MailingBaseForm aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm");
    aForm.setIsTemplate(false);
%>
<c:set var="ACTION_CONFIRM_DELETE" value="<%= CampaignAction.ACTION_CONFIRM_DELETE %>" scope="page" />
<c:set var="ACTION_NEW" value="<%= CampaignAction.ACTION_NEW %>" scope="page" />
<c:set var="ACTION_VIEW" value="<%= CampaignAction.ACTION_VIEW %>" scope="page" />
<c:set var="ACTION_LIST" value="<%= CampaignAction.ACTION_LIST %>" scope="page" />


<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'archive_mailing';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<html:form action="/campaign.do">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>

      <div class="grey_box_container">
          <div class="grey_box_top"></div>
          <div class="grey_box_content">
              <div class="grey_box_left_column">
                  <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                  <html:text styleId="mailing_name" property="shortname" maxlength="99" size="42"/>
              </div>
              <div class="grey_box_center_column">
                  <label for="mailing_name"><bean:message key="default.description"/>:</label>
                  <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
              </div>
              <div class="grey_box_right_column"></div>
          </div>
          <div class="grey_box_bottom"></div>
      </div>

      <div class="button_container">

          <input type="hidden" id="save" name="save" value=""/>
          <logic:notEqual name="campaignForm" property="campaignID" value="0">
              <agn:ShowByPermission token="campaign.delete">
                  <div class="action_button">
                      <html:link page="/campaign.do?action=${ACTION_CONFIRM_DELETE}&campaignID=${campaignForm.campaignID}&previousAction=${ACTION_VIEW}">
                          <span><bean:message key="button.Delete"/></span>
                      </html:link>
                  </div>
              </agn:ShowByPermission>
          </logic:notEqual>
          <agn:ShowByPermission token="campaign.change">
              <div class="action_button">
                  <a href="#" onclick="document.campaignForm.submit(); return false;">
                      <span><bean:message key="button.Save"/></span>
                  </a>
              </div>
          </agn:ShowByPermission>
          <div class="action_button"><bean:message key="campaign.Campaign"/>:</div>
      </div>

<c:if test="${campaignForm.campaignID != 0}">

    <div class="export_wizard_content">
        <h2 class="blue_box_header"><bean:message key="Mailings"/>:</h2>
    </div>

    <agn:ShowByPermission token="mailing.new">
        <div class="button_container before_table_button_container">
            <div class="action_button">
      	        <html:link page="/mailingbase.do?action=${ACTION_NEW}&mailingID=0&campaignID=${campaignForm.campaignID}"><span><bean:message key="mailing.New_Mailing"/></span></html:link>
            </div>
            <div class="action_button"><bean:message key="Mailing"/>:</div>
        </div>
    </agn:ShowByPermission>

    <div class="list_settings_container">
        <logic:iterate collection="${campaignForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

    <display:table class="list_table" id="archive_mailing" name="mailinglist" pagesize="${campaignForm.numberofRows}" requestURI="/campaign.do?action=${ACTION_VIEW}&campaignID=${campaignForm.campaignID}&__fromdisplaytag=true&numberofRows=${campaignForm.numberofRows}" excludedParams="*">
        <display:column  headerClass="head_mailing_wide" class="mailing" titleKey="Mailing" sortable="false">
            <span class="ie7hack">
                <html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${archive_mailing.id}">${archive_mailing.shortname}</html:link>
            </span>
        </display:column>
        <display:column headerClass="head_description_wide" class="description" titleKey="default.description" sortable="false">
            <span class="ie7hack">
            <html:link
                    page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${archive_mailing.id}">${archive_mailing.description}</html:link>
            </span>
        </display:column>
        <display:column headerClass="head_mailinglist" class="mailinglist" titleKey="Mailinglist" sortable="false">
            <span class="ie7hack">
                ${archive_mailing.mailinglist.shortname}
            </span>
        </display:column>
        <display:column  headerClass="senddate" class="senddate" titleKey="mailing.senddate" sortable="false" format="{0,date,yyyyMMdd}" property="senddate"/>
        <display:column class="edit">
            <html:link styleClass="mailing_edit" titleKey="mailing.MailingEdit"
                       page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${archive_mailing.id}"/>
            <agn:ShowByPermission token="mailing.delete">
                <html:link styleClass="mailing_delete" titleKey="mailing.MailingDelete"
                           page="/campaign.do?action=${ACTION_CONFIRM_DELETE}&campaignID=${campaignForm.campaignID}&previousAction=${ACTION_VIEW}&mailingID=${archive_mailing.id}"/>
            </agn:ShowByPermission>
        </display:column>
    </display:table>

</c:if>
</html:form>

<script type="text/javascript">
    table = document.getElementById('archive_mailing');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#archive_mailing tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>