<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.AdminAction, java.util.Locale, java.util.TimeZone"
         contentType="text/html; charset=utf-8" errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    int tmpAdminID = (Integer) request.getAttribute("tmpAdminID");
%>

<c:set var="ACTION_VIEW" value="<%= AdminAction.ACTION_VIEW %>"/>

<script src="settings/admin/PasswordRanking.js" type="text/javascript"></script>
<script type="text/javascript">
    var rank = new PasswordRanking();
</script>
<script type="text/javascript">
    function checkPasswordMatch() {
        var matching = rank.checkMatch('password', 'repeat');

        if (matching) {
            document.getElementById('different_passwords').style.display = '';
        } else {
            document.getElementById('different_passwords').style.display = 'none';
        }

        veDiv = document.getElementById('validation_errors');
        if (veDiv != null) {
            veDiv.style.display = 'none';
        }

        rank.enableButton('save_btn', 'button.Save', matching);
    }

    Event.observe(window, 'load', function() {
     var passwordValue = document.getElementById('password').value;
    <% if (tmpAdminID != 0) { %>
        if (!passwordValue) {
            rank.enableButton('save_btn', 'button.Save', false);
        } else {
            rank.enableButton('save_btn', 'button.Save', true);
        }
    <% } else {%>
        if (passwordValue) {
            rank.enableButton('save_btn', 'button.Create', false);
        } else {
            rank.enableButton('save_btn', 'button.Create', true);
        }
    <% } %>
    });

</script>
<div class="statusbox_containter" id="different_passwords" style="display: none;">
    <div class="statusbox_top"></div>
    <div class="statusbox_content">
        <div class="status_error"><bean:message key="error.admin.different_passwords"/></div>
    </div>
    <div class="statusbox_bottom"/>
</div>
</div>

<html:form action="admin" focus="username" onsubmit="return !rank.checkMatch('password', 'repeat');">

    <html:hidden property="action"/>
    <html:hidden property="adminID"/>
    <html:hidden property="previousAction" value="${ACTION_VIEW}"/>


    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:&nbsp;</label>
                <html:text styleId="mailing_name" property="fullname" maxlength="99" size="32"/>
            </div>
            <div class="grey_box_center_column">
                <label for="companyName"><bean:message key="settings.User_Name"/>:&nbsp;</label>
                <html:text styleId="companyName" property="username" maxlength="99" size="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

	<agn:JspExtensionPoint plugin="core" point="admin.view.pos2" />

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <div class="admin_filed_detail_form_item">
                <label for="password"><bean:message key="logon.password"/>:&nbsp;</label>

                <html:password property="password" styleId="password"
                               onkeyup="rank.securityCheck('bar', this.id); checkPasswordMatch();" size="52"
                               maxlength="99"/>

            </div>


            <div class="admin_filed_detail_form_item">
                <script type="text/javascript">rank.showBar("bar", "<bean:message key='settings.secure'/>", "<bean:message key='settings.insecure'/>");</script>
            </div>

            <div class="admin_filed_detail_form_item">
                <label for="repeat"><bean:message key="settings.admin.Confirm"/>:&nbsp;</label>

                <html:password property="passwordConfirm" styleId="repeat" onkeyup="checkPasswordMatch();" size="52"
                               maxlength="99"/>

            </div>

            <agn:ShowByPermission token="admin.setgroup">
                <div class="admin_filed_detail_form_item">
                    <label for="groupID"><bean:message key="settings.Usergroup"/>:&nbsp;</label>

                    <html:select property="groupID" size="1" styleId="groupID">
                        <html:option value="0"><bean:message key="settings.Usergroup.none"/></html:option>
                        <c:forEach var="adminGroup" items="${adminGroups}">
                            <html:option value="${adminGroup.groupID}">
                                ${adminGroup.shortname}
                            </html:option>
                        </c:forEach>
                    </html:select>

                </div>
            </agn:ShowByPermission>
            <% if (!AgnUtils.allowed("admin.setgroup", request)) { %>
            <html:hidden property="groupID"/>
            <% } %>

            <div class="admin_filed_detail_form_item">
                <label for="language"><bean:message key="settings.Language"/>:&nbsp;</label>

                <html:select property="language" size="1" styleId="language">
                    <html:option value="<%= Locale.US.toString() %>"><bean:message key="settings.English"/></html:option>
                    <html:option value="NL_nl"><bean:message key="settings.Dutch"/></html:option>
                    <html:option value="<%= Locale.FRANCE.toString() %>"><bean:message key="settings.French"/></html:option>
                    <html:option value="<%= Locale.GERMANY.toString() %>"><bean:message key="settings.German"/></html:option>
                    <html:option value="<%= Locale.ITALY.toString() %>"><bean:message key="settings.Italian" /></html:option>
                    <html:option value="PT_pt"><bean:message key="settings.Portuguese"/></html:option>
                    <html:option value="ES_es"><bean:message key="settings.Spanish"/></html:option>
                    <html:option value="<%= Locale.CHINA.toString() %>"><bean:message key="settings.Chinese"/></html:option>
                </html:select>

            </div>

            <div class="admin_filed_detail_form_item">
                <label for="adminTimezone"><bean:message key="settings.Timezone"/>:&nbsp;</label>

                <html:select property="adminTimezone" size="1" styleId="adminTimezone">
                    <% String allZones[] = TimeZone.getAvailableIDs();
                        int len = allZones.length;
                        TimeZone tmpZone = TimeZone.getDefault();
                        Locale aLoc = (Locale) session.getAttribute("messages_lang");
                        for (int i = 0; i < len; i++) {
                            tmpZone.setID(allZones[i]);
                    %>
                    <html:option value="<%= allZones[i] %>"><%= /* tmpZone.getDisplayName(aLoc) */ allZones[i] %>
                    </html:option>
                    <% } %>
                </html:select>

            </div>
            <div class="admin_filed_detail_form_item">
                <label for="numberofRows"><bean:message key="settings.Admin.numberofrows"/>:&nbsp;</label>

                <html:select property="numberofRows" styleId="numberofRows">
                    <%
                        String[] sizes = {"20", "50", "100"};
                        for (int i = 0; i < sizes.length; i++) {
                    %>
                    <html:option value="<%= sizes[i] %>"><%= sizes[i] %>
                    </html:option>
                    <%
                        }
                    %>
                </html:select>

            </div>
            <html:hidden property="companyID" value="1"/>
        </div>
        <div class="blue_box_bottom"></div>
    </div>
    <div class="button_container" style="padding-top:5px;">
        <input type="hidden" name="save" value=""/>

        <% if (tmpAdminID != 0) { %>
        <agn:ShowByPermission token="admin.change">

            <div class="action_button" id='save_btn'><a href="#"
                                                            onclick=" document.adminForm.save.value='save'; document.adminForm.submit();return false;"><span><bean:message
                    key="button.Save"/></span></a></div>
        </agn:ShowByPermission>
        <% } else {%>
        <agn:ShowByPermission token="admin.new">
            <div class="action_button" id='save_btn'><a href="#"
                                                            onclick=" document.adminForm.save.value='save'; document.adminForm.submit();return false;"><span><bean:message
                    key="button.Create"/></span></a></div>
        </agn:ShowByPermission>
        <% } %>

        <agn:ShowByPermission token="admin.delete">
            <% if (tmpAdminID != 0) { %>
            <logic:notEqual name="adminID" scope="session" value="<%= Integer.toString(tmpAdminID) %>">
                <input type="hidden" name="delete" id="delete_hidden" value=""/>

                <div class="action_button"><a href="#"
                                                  onclick="document.getElementById('delete_hidden').value='delete'; document.adminForm.submit();return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </logic:notEqual>
            <% } %>
        </agn:ShowByPermission>
    </div>

</html:form>