<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.beans.Admin"
         buffer="32kb" %>

<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript" src="${emmLayoutBase.jsURL}/option_title.js"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'logs';
    var columnindex = 0;
    var dragging = false;
    var minWidthLast = 200;

    document.onmousemove = drag;
    document.onmouseup = dragstop;

    Event.observe(window, 'load', function() {
        createPickers();
        onPageLoad();
    });

    function createPickers() {
        $(document.body).select('input.datepicker').each(
            function(e) {
                new Control.DatePicker(e, { 'icon': 'images/datepicker/calendar.png' ,
                    timePicker: false,
                    timePickerAdjacent: false,
                    dateFormat: '${localDatePattern}',
                    locale:'<bean:write name="emm.locale" property="language" scope="session"/>'

                });
            }
        );
    }

    function parametersChanged() {
        document.getElementsByName('userActivityLogForm')[0].numberOfRowsChanged.value = true;
    }
</script>
<agn:ShowColumnInfo id="colsel" table="<%=AgnUtils.getCompanyID(request)%>"/>


<html:form action="/useractivitylog.do?action=${ACTION_LIST}" styleId="filterForm">
    <html:hidden property="numberOfRowsChanged"/>
    <div id="filterbox_container">
        <div id="filterbox_label"></div>
        <div class="filterbox_form_container">
            <div id="filterbox_top"></div>
            <div id="filterbox_content">
                <div style="float:left;  width:15%;margin-left:11px;">
                    <b><bean:message key="Actions"/>:</b>
                    <br>
                    <html:select property="userActivityLogAction" size="1" style="width: 120px;" >
                        <c:forEach var="action"
                                   items="${userActivityLogForm.actions}">
                            <html:option  value="${action.intValue}">
                                <bean:message key="${action.publicValue}"/>
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>
                <div style="float:left;  width:45%;">
                    <b><bean:message key="UserActivitylog.Users"/>:</b>
                    <br>
                    <html:select property="username" size="1" style="width:330px;">
                        <%
                            if (AgnUtils.allowed("masterlog.show", request)) {
                        %>
                        <html:option value="0"><bean:message key="UserActivitylog.All_Users"/></html:option>

                        <c:forEach var="user" items="${userActivityLogForm.adminList}">
                            <html:option value="${user.username}">
                                 ${user.username}
                            </html:option>
                        </c:forEach>

                        <%
                        } else if (AgnUtils.allowed("adminlog.show", request)) {
                        %>
                        <html:option value="0"><bean:message key="UserActivitylog.All_Users"/></html:option>

                        <c:forEach var="comUser" items="${userActivityLogForm.adminByCompanyList}">
                            <html:option value="${comUser.username}">
                                 ${comUser.username}
                            </html:option>
                        </c:forEach>

                        <%
                        } else {
                        %>
                        <html:option
                                value='<%= \"\"+AgnUtils.getAdmin(request).getUsername() %>'><%= AgnUtils.getAdmin(request).getUsername() %>
                        </html:option>

                        <%
                            }
                        %>
                    </html:select>
                </div>
                <div style="float:left;  width:18%;">
                    <div style="float:left;">
                        <b><bean:message key="UserActivitylog.FromDate"/>:</b>
                        <br>

                        <input type="text" id="fromDateInput" name="fromDate" class="datepicker"
                               value="${userActivityLogForm.fromDate}" style="width:85px;"/>
                    </div>

                </div>
                <div style="float:left; width:18%;">
                    <div style="float:left;">
                        <b><bean:message key="UserActivitylog.ToDate"/>:</b>
                        <br>
                        <input type="text" id="toDateInput" name="toDate" value="${userActivityLogForm.toDate}" class="datepicker"
                               style="width:85px;"/>
                    </div>

                </div>
                <div class="filter_button_wrapper">
                    <div class="filterbox_form_button filterbox_form_button_right_corner">
                        <a href="#" onclick="parametersChanged();document.userActivityLogForm.submit(); return false;">
                            <span><bean:message key="button.OK"/></span></a>
                    </div>
                </div>
            </div>
            <div id="filterbox_bottom"></div>
        </div>
    </div>
    <div class="list_settings_container">
        <div class="filterbox_form_button">
            <a href="#" onclick="parametersChanged(); document.userActivityLogForm.submit(); return false;"><span><bean:message
                    key="button.Show"/></span></a>
        </div>        
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${userActivityLogForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

</html:form>

<display:table class="list_table"
               pagesize="${userActivityLogForm.numberofRows}" id="logs" htmlId="logs"
               name="userActivitylogList" sort="external" excludedParams="*"
               requestURI="/useractivitylog.do?action=${ACTION_LIST}&__fromdisplaytag=true"
               partialList="true" size="${userActivitylogList.fullListSize}">

    <display:column headerClass="head_name" class="senddate" titleKey="UserActivitylog.date"
                    format="{0,date,${localeTablePattern}}" property="date" sortable="false"/>
    <display:column class="username" headerClass="head_name"
                    property="username" titleKey="UserActivitylog.username" sortable="false"
                    sortProperty="username"/>
    <display:column class="action" headerClass="head_name"
                    property="action" titleKey="UserActivitylog.action" sortable="false"/>
    <display:column class="description" titleKey="UserActivitylog.description" sortable="false">
        <span class="ie7hack">
                ${logs.description}
        </span>
    </display:column>

</display:table>
<script type="text/javascript">
    table = document.getElementById('logs');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);
</script>
<script language="javascript" type="text/javascript">
    addTitleToOptions();
</script>

