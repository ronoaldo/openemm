<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.actions.EmmAction, org.agnitas.util.AgnUtils, org.agnitas.web.TrackableLinkAction" %>
<%@ page import="org.agnitas.web.TrackableLinkForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>
<script type="text/javascript">
    Event.observe(window, 'load', function () {
        var trackableSelect = document.getElementById("trackable");
        var actionSelect = document.getElementById("linkAction");
        if(trackableSelect.options[trackableSelect.selectedIndex].value == 0)
            actionSelect.setAttribute("disabled","disabled");
        else
            actionSelect.removeAttribute("disabled");
    });
    function toggleDisabled(){
        var trackableSelect = document.getElementById("trackable");
        var actionSelect = document.getElementById("linkAction");
        if(trackableSelect.options[trackableSelect.selectedIndex].value == 0)
            actionSelect.setAttribute("disabled","disabled");
        else
            actionSelect.removeAttribute("disabled");
    }
</script>
<% int tmpMailingID=0;
    TrackableLinkForm aForm=null;
    if(request.getAttribute("trackableLinkForm")!=null) {
        aForm=(TrackableLinkForm)request.getAttribute("trackableLinkForm");
        tmpMailingID=aForm.getMailingID();
    }
%>
<html:form action="/tracklink">
    <html:hidden property="mailingID"/>
    <html:hidden property="linkID"/>
    <html:hidden property="action"/>
    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="assistant_trackablelinks_form_item">
                    <label><bean:message key="mailing.URL"/>:&nbsp;</label>
                    <bean:write name="trackableLinkForm" property="linkUrl"/>
            </div>

            <div class="assistant_trackablelinks_form_item">
                <label><bean:message key="default.description"/>:&nbsp;</label>
                <html:text property="linkName" size="52" maxlength="99"/>
            </div>

            <div class="assistant_trackablelinks_form_item">
                <label><bean:message key="mailing.Trackable"/>:&nbsp;</label>
                <html:select property="trackable" styleId="trackable" onchange="toggleDisabled();">
                    <html:option value="0"><bean:message key="mailing.Not_Trackable"/></html:option>
                    <html:option value="1"><bean:message key="mailing.Only_Text_Version"/></html:option>
                    <html:option value="2"><bean:message key="mailing.Only_HTML_Version"/></html:option>
                    <html:option value="3"><bean:message key="mailing.Text_and_HTML_Version"/></html:option>
                </html:select>
            </div>

            <div class="assistant_trackablelinks_form_item">
                <label><bean:message key="action.Action"/>:&nbsp;</label>
                <html:select property="linkAction" styleId="linkAction" size="1">
                    <html:option value="0"><bean:message key="settings.No_Action"/></html:option>
                    <c:forEach var="action" items="${notFormActions}">
                       <html:option value="${action.id}">
                         ${action.shortname}
                       </html:option>
                     </c:forEach>
                </html:select>

            </div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>
    <br clear="both"/>
    <div class="button_container">
        <div class="action_button mailingwizard_add_button">
            <a href="#"
               onclick="document.trackableLinkForm.submit(); return false;"><span><bean:message
                    key="button.Save"/></span></a>
        </div>
        <div class="action_button mailingwizard_add_button">
            <html:link
                    page='<%=new String("/tracklink.do?action=" + TrackableLinkAction.ACTION_LIST + "&mailingID=" + tmpMailingID)%>'>
                <span><bean:message key="button.Cancel"/></span></html:link>
        </div>
    </div>
</html:form>

