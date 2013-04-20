<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.util.*, org.agnitas.web.*,org.agnitas.web.forms.*, java.util.*, org.agnitas.beans.*, org.agnitas.cms.utils.CmsUtils"
         contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    pageContext.setAttribute("FCKEDITOR_PATH", AgnUtils.getEMMProperty("fckpath"));
    MailingBaseForm aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm");
    int tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    String permToken = null;
%>

<script type="text/javascript" src="${FCKEDITOR_PATH}/fckeditor.js"></script>

<script type="text/javascript">

    var baseUrl = window.location.pathname;
    pos = baseUrl.lastIndexOf('/');
    baseUrl = baseUrl.substring(0, pos);

    // initialisation
    Event.observe(window, 'load', function() {
    <agn:ShowByPermission token="settings.open">
                var closed = document.getElementsByClassName('toggle_closed');
                if(closed)
                for(var i=0;i<closed.length;i++){
                    closed[i].addClassName('toggle_open');
                    closed[i].next().show();
                    closed[i].removeClassName('toggle_closed');
                }
    </agn:ShowByPermission>
    <agn:HideByPermission token="settings.open">
    <agn:ShowByPermission token="template.show">
    <c:if test="${not mailingBaseForm.templateContainerVisible}">
        toggleContainerOnly(document.getElementById('schablonen_container_button'));
        toggleContainerStyles(document.getElementById('schablonen_container_button'), 'expand_blue_box_top_bordered', 'expand_blue_box_top');
    </c:if>

    </agn:ShowByPermission>
    <c:if test="${not mailingBaseForm.generalContainerVisible}">
        toggleContainerOnly(document.getElementById('settings_general_container_button'));
    </c:if>
    <c:if test="${not mailingBaseForm.targetgroupsContainerVisible}">
        toggleContainerOnly(document.getElementById('settings_targetgroups_container_button'));
    </c:if>
    </agn:HideByPermission>
    });

    function toggleContainerOnly(container) {
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        $(container).next().toggle();
    }

    function toggleContainer(container, name) {
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        if (document.mailingBaseForm[name].value == 'true') {
            document.mailingBaseForm[name].value = 'false';
        }
        else {
            document.mailingBaseForm[name].value = 'true';
        }
        $(container).next().toggle();
    }

    function toggleContainerStyles(container, style1, style2) {
        $(container).toggleClassName(style1);
        $(container).toggleClassName(style2);
    }

</script>

<html:form action="/mailingbase" focus="shortname">
    <html:hidden property="templateContainerVisible"/>
    <html:hidden property="otherMediaContainerVisible"/>
    <html:hidden property="generalContainerVisible"/>
    <html:hidden property="targetgroupsContainerVisible"/>

    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <html:hidden property="isTemplate"/>
    <html:hidden property="oldMailingID"/>
    <html:hidden property="copyFlag"/>


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

    <jsp:include page="/mailing/media/email.jsp"/>

    <jsp:include page="/mailing/view_base_settings.jsp"/>

    <div class="button_container">
        <% if (aForm.isIsTemplate()) {
            permToken = "template.change";
        } else {
            permToken = "mailing.change";
        } %>
        <agn:ShowByPermission token="<%= permToken %>">
            <logic:equal name="mailingBaseForm" property="isTemplate" value="true">
                <input type="hidden" name="save" value=""/>

                <div class="action_button"><a href="#"
                                                  onclick="saveEditor(); document.mailingBaseForm.save.value='save'; document.mailingBaseForm.submit();return false;"><span>
                    <bean:message key="button.Save"/></span></a></div>
            </logic:equal>
            <logic:equal name="mailingBaseForm" property="isTemplate" value="false">
                <input type="hidden" name="save" value=""/>

                <div class="action_button"><a href="#"
                                                  onclick="saveEditor(); document.mailingBaseForm.save.value='save'; document.mailingBaseForm.submit();return false;"><span>
                    <bean:message key="button.Save"/></span></a></div>
            </logic:equal>
        </agn:ShowByPermission>
        <% if (tmpMailingID != 0) { %>

        <agn:ShowByPermission token="mailing.copy">
            <div class="action_button"><html:link
                    page='<%= "/mailingbase.do?action=" + MailingBaseAction.ACTION_CLONE_AS_MAILING + "&mailingID=" + tmpMailingID %>'>
                <span><bean:message key="button.Copy"/></span>
            </html:link>
            </div>
        </agn:ShowByPermission>

        <% if (aForm.isIsTemplate()) {
            permToken = "template.delete";
        } else {
            permToken = "mailing.delete";
        } %>
        <agn:ShowByPermission token="<%= permToken %>">
            <div class="action_button"><html:link
                    page='<%= "/mailingbase.do?action=" + MailingBaseAction.ACTION_CONFIRM_DELETE + "&previousAction=" + MailingBaseAction.ACTION_VIEW + "&mailingID=" + tmpMailingID %>'>
                <span><bean:message key="button.Delete"/></span>
            </html:link>
            </div>
        </agn:ShowByPermission>

        <% } %>

        <div class="action_button"><bean:message key="Mailing"/>:</div>
    </div>
</html:form>
