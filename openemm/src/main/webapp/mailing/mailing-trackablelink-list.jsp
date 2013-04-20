<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.beans.TrackableLink, org.agnitas.util.SafeString" %>
<%@ page import="org.agnitas.web.TrackableLinkForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>


<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'trackable_link';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;

    Event.observe(window, 'load', function() {
        <agn:ShowByPermission token="settings.open">
         $$('.toggle_closed').each(function(item){
            item.addClassName('toggle_open');
            item.next().show();
            item.removeClassName('toggle_closed');
         });
        </agn:ShowByPermission>
        <agn:HideByPermission token="settings.open">
            <c:if test="${trackableLinkForm.trackableContainerVisible}">
                $$('.trackablelinks_trackable_container').invoke('show');
            </c:if>

            <c:if test="${ trackableLinkForm.actionContainerVisible}">
                $$('.trackablelinks_defaultaction_container').invoke('show');
            </c:if>
        </agn:HideByPermission>
       onPageLoad();
    });

    function toggleContainer(container, name) {
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        if (document.trackableLinkForm[name].value == 'true') {
            document.trackableLinkForm[name].value = 'false';
        }
        else {
            document.trackableLinkForm[name].value = 'true';
        }

        $(container).next().toggle();
    }


</script>
<%
    TrackableLinkForm aForm = null;
    if (request.getAttribute("trackableLinkForm") != null) {
        aForm = (TrackableLinkForm) request.getAttribute("trackableLinkForm");
    }
%>

<html:form action="/tracklink">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <html:hidden property="trackableContainerVisible"/>
    <html:hidden property="actionContainerVisible"/>
    <html:hidden property="defaultActionType"/>
 <div id="filterbox_container_for_name">
    <div class="filterbox_form_container">
        <div id="filterbox_top"></div>
        <div id="searchbox_content" class="filterbox_form_container">
            <div id="filterbox_small_label" class="filterbox_small_label_tab">
                    ${trackableLinkForm.shortname}&nbsp;&nbsp;<%if (aForm != null && aForm.getDescription() != null && !aForm.getDescription().isEmpty()) {%>
                |&nbsp;&nbsp;${trackableLinkForm.description}<% } %>
            </div>
        </div>
        <div id="filterbox_bottom"></div>
    </div>
</div>
    <h3 class="header_coloured" style="padding-top: 5px; margin-top:5px;">
        <bean:message key="mailing.trackablelinks.table"/>:
    </h3>
    <table class="list_table" id="trackable_link">
    <tr>
        <th class="trackable_link_url_long"><bean:message key="mailing.URL"/>&nbsp;</th>
        <th class="trackable_link_description_head"><bean:message key="default.description"/>&nbsp;</th>
        <th class="recipient_title_head"><bean:message key="mailing.Trackable"/>&nbsp;</th>
        <th class="recipient_title_head"><bean:message key="action.Action"/></th>
        <th>&nbsp;</th>
    </tr>

    <% TrackableLink aLink = null; String className=""; %>
    <logic:iterate id="link" name="trackableLinkForm" property="links">
        <% aLink = (TrackableLink) pageContext.getAttribute("link");
           if(aLink.getUsage() == 0)
               className = "recipient_title action-disabled";
           else
               className = "recipient_title";
%>
        <tr>
            <td class="action_name">
                <span class="ie7hack">
                    <a href="${link.fullUrl}" target="_blank"><img border="0" alt="${link.fullUrl}" src="${emmLayoutBase.imagesURL}/extlink.gif"></a>&nbsp;
                    <html:link
                        page="/tracklink.do?action=${ACTION_VIEW}&linkID=${link.id}&mailingID=${link.mailingID}"
                        title="${link.fullUrl}"><%= SafeString.getHTMLSafeString(aLink.getFullUrl(), 30) %>
                    </html:link>
                </span>
            </td>
            <td class="recipient_title">
                <span class="ie7hack">
                    <html:link page="/tracklink.do?action=${ACTION_VIEW}&linkID=${link.id}&mailingID=${link.mailingID}">
                        <%= SafeString.getHTMLSafeString(aLink.getShortname()) %>
                    </html:link>
                </span>
            </td>
            <td class="recipient_title">
                <span class="ie7hack">
                    <c:if test="${link.usage == 0}"><bean:message key="mailing.Not_Trackable"/></c:if>
                    <c:if test="${link.usage == 1}"><bean:message key="mailing.Only_Text_Version"/></c:if>
                    <c:if test="${link.usage == 2}"><bean:message key="mailing.Only_HTML_Version"/></c:if>
                    <c:if test="${link.usage == 3}"><bean:message key="mailing.Text_and_HTML_Version"/></c:if>
                </span>
            </td>
            <td class="<%= className %>">
                <span class="ie7hack">
                    <c:if test="${link.actionID == 0}">
                        <bean:message key="settings.No_Action"/>
                    </c:if>
                    <logic:iterate id="action" name="actions" scope="request">
                        <c:if test="${link.actionID == action.id}">
                            ${action.shortname}
                        </c:if>
                    </logic:iterate>
                </span>
            </td>
            <td>
                <span class="ie7hack">
                    <html:link page="/tracklink.do?action=${ACTION_VIEW}&linkID=${link.id}&mailingID=${link.mailingID}">
                        <img src="${emmLayoutBase.imagesURL}/table_edit_icon.png" alt="<bean:message key="button.Edit"/>" border="0">
                    </html:link>
                </span>
            </td>
        </tr>
    </logic:iterate>
    </table>
    <agn:ShowByPermission token="mailing.default_action">

        <h3 class="header_coloured"><bean:message key="TrackableLink.edit.all"/>:</h3>

        <div class="expand_grey_box_container">
            <div class="expand_grey_box_top toggle_closed" onclick="toggleContainer(this, 'trackableContainerVisible' );">
                <div class="expand_grey_box_top_subcontainer">
                    <a href="#Trackable"><bean:message key="mailing.Trackable"/>:</a>
                </div>
            </div>
            <div style="display:none;">
                <div class="expand_grey_box_content">
                    <div class="assistant_trackablelinks_form_item">
                        <label for="globalUsage"><bean:message key="mailing.Trackable"/>:</label>
                        <html:select property="globalUsage" styleId="globalUsage">
                            <html:option value="0"><bean:message key="mailing.Not_Trackable"/></html:option>
                            <html:option value="1"><bean:message key="mailing.Only_Text_Version"/></html:option>
                            <html:option value="2"><bean:message key="mailing.Only_HTML_Version"/></html:option>
                            <html:option value="3"><bean:message key="mailing.Text_and_HTML_Version"/></html:option>
                        </html:select>
                    </div>
                    <div class="action_button mailingwizard_add_button">
                        <a href="#"
                           onclick="document.trackableLinkForm.action.value=${ACTION_GLOBAL_USAGE}; document.trackableLinkForm.submit(); return false;"><span><bean:message
                                key="button.Save"/></span></a>
                    </div>
                </div>
                <div class="expand_grey_box_bottom"></div>
            </div>
        </div>

        <div class="expand_grey_box_container">
            <div class="expand_grey_box_top toggle_closed" onclick="toggleContainer(this, 'actionContainerVisible');">
                <div class="expand_grey_box_top_subcontainer">
                    <a href="#DefaultAction"><bean:message key="mailing.DefaultAction"/>:</a>
                </div>
            </div>
            <div style="display:none;">
                <div class="expand_grey_box_content">
                    <div class="assistant_trackablelinks_form_item">
                        <label for="linkAction"><bean:message key="mailing.DefaultAction"/>:</label>
                        <html:select property="linkAction" size="1" styleId="linkAction">
                            <html:option value="0"><bean:message key="settings.No_Action"/></html:option>
                            <c:forEach var="action" items="${notFormActions}">
                            <html:option value="${action.id}">
                                ${action.shortname}
                            </html:option>
                            </c:forEach>
                        </html:select>
                        <div class="action_button mailingwizard_add_button button_left15">
                        <a href="#"
                           onclick="document.trackableLinkForm.defaultActionType.value = 'link'; document.trackableLinkForm.action.value=${ACTION_SET_STANDARD_ACTION}; document.trackableLinkForm.submit(); return false;"><span><bean:message
                                key="button.Save"/></span></a>
                        </div>
                    </div>

                    <div class="assistant_trackablelinks_form_item">
                        <label for="linkAction"><bean:message key="mailing.OpenAction"/>:</label>
                        <html:select property="openActionID" size="1" styleId="linkAction">
                            <html:option value="0"><bean:message key="settings.No_Action"/></html:option>
                            <c:forEach var="action" items="${notFormActions}">
                            <html:option value="${action.id}">
                                ${action.shortname}
                            </html:option>
                            </c:forEach>
                        </html:select>
                        <div class="action_button mailingwizard_add_button button_left15">
                        <a href="#"
                           onclick="document.trackableLinkForm.defaultActionType.value = 'open'; document.trackableLinkForm.action.value=${ACTION_SET_STANDARD_ACTION}; document.trackableLinkForm.submit(); return false;"><span><bean:message
                                key="button.Save"/></span></a>
                        </div>
                    </div>

                    <div class="assistant_trackablelinks_form_item">
                        <label for="linkAction"><bean:message key="mailing.ClickAction"/>:</label>
                        <html:select property="clickActionID" size="1" styleId="linkAction">
                            <html:option value="0"><bean:message key="settings.No_Action"/></html:option>
                            <c:forEach var="action" items="${notFormActions}">
                            <html:option value="${action.id}">
                                ${action.shortname}
                            </html:option>
                            </c:forEach>
                        </html:select>
                        <div class="action_button mailingwizard_add_button button_left15">
                        <a href="#"
                           onclick="document.trackableLinkForm.defaultActionType.value = 'click'; document.trackableLinkForm.action.value=${ACTION_SET_STANDARD_ACTION}; document.trackableLinkForm.submit(); return false;"><span><bean:message
                                key="button.Save"/></span></a>
                        </div>
                    </div>

                </div>
                <div class="expand_grey_box_bottom"></div>
            </div>
        </div>
    </agn:ShowByPermission>

</html:form>
<script type="text/javascript">
    table = document.getElementById('trackable_link');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#trackable_link tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>