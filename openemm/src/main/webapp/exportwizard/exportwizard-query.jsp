<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.AgnUtils, org.agnitas.web.ExportWizardAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="NO_MAILINGLIST" value="<%= ExportWizardAction.NO_MAILINGLIST %>" scope="page" />

<script type="text/javascript">

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
            <c:if test="${not exportWizardForm.recipientFilterVisible}">
                hideFilter('export_recipient_filter');
            </c:if>

            <c:if test="${not exportWizardForm.columnsPanelVisible}">
                hideFilter('export_columns');
            </c:if>

            <c:if test="${not exportWizardForm.mlistsPanelVisible}">
                hideFilter('export_mailinglists');
            </c:if>

            <c:if test="${not exportWizardForm.fileFormatPanelVisible}">
                hideFilter('export_file_format');
            </c:if>

            <c:if test="${not exportWizardForm.datesPanelVisible}">
                hideFilter('export_dates_container');
            </c:if>

        </agn:HideByPermission>
        createPickers();
    });

    function hideFilter(filterClassName){
        $$('.'+filterClassName).invoke('hide');
        var element = document.getElementsByClassName(filterClassName)[0];
        element.previous().addClassName('toggle_closed');
        element.previous().removeClassName('toggle_open');
    }

    function toggleContainer(container, name){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        if( document.exportWizardForm[name].value == 'true') {
            document.exportWizardForm[name].value = 'false';
        }
        else {
            document.exportWizardForm[name].value = 'true';
        }
        $(container).next().toggle();
    }

    function createPickers() {
        $(document.body).select('input.datepicker').each(
                function(e) {
                    new Control.DatePicker(e, { 'icon': 'images/datepicker/calendar.png' ,
                        timePicker: false,
                        timePickerAdjacent: false ,
                        dateFormat: 'dd.MM.yyyy',
                        locale:'<bean:write name="emm.locale" property="language" scope="session"/>'

                    });
                }
                );
    }

</script>


<html:form action="/exportwizard">
    <html:hidden property="action"/>
    <html:hidden property="exportPredefID"/>

    <html:hidden property="recipientFilterVisible"/>
    <html:hidden property="columnsPanelVisible"/>
    <html:hidden property="mlistsPanelVisible"/>
    <html:hidden property="datesPanelVisible"/>
    <html:hidden property="fileFormatPanelVisible"/>

    <div class="export_wizard_content">
        <ul class="new_mailing_step_display">
            <li class="step_display_first"><span>1</span></li>
            <li><span class="step_active">2</span></li>
            <li><span>3</span></li>
        </ul>

        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="export_step2_toggle toggle_open" onclick="toggleContainer(this, 'recipientFilterVisible');"><a href="#"><bean:message key="export.selection"/></a></div>
            <div class="export_recipient_filter">

                <div class="export_select_div">
                    <label for="search_mailinglist"><bean:message key="Mailinglist"/>:</label>
                    <html:select styleClass="export_step2_select" property="mailinglistID" onchange="parametersChanged()">
                        <html:option value="0" key="default.All" />
                        <html:option value="${NO_MAILINGLIST}" key="export.No_Mailinglist" />
                        <c:forEach var="mailinglist" items="${exportWizardForm.mailinglistObjects}">
                            <html:option value="${mailinglist.id}">
                                ${mailinglist.shortname}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>

                <div class="export_select_div">
                    <label for="search_targetgroup"><bean:message key="target.Target"/>:</label>
                    <html:select styleClass="export_step2_select" property="targetID" onchange="parametersChanged()">
                        <html:option value="0" key="default.All" />
                        <c:forEach var="target" items="${exportWizardForm.targetGroups}">
                            <html:option value="${target.id}">
                                ${target.targetName}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>

                <div class="export_select_div">
                    <label for="search_recipient_type"><bean:message key="RecipientType"/>:</label>
                    <html:select styleClass="export_step2_select" property="userType" onchange="parametersChanged()">
                        <html:option value="E" key="default.All"/>
                        <html:option value="A" key="recipient.Administrator" />
                        <html:option value="T" key="recipient.TestSubscriber" />
                        <html:option value="W" key="recipient.NormalSubscriber"/>
                    </html:select>
                </div>

                <div class="export_select_div">
                    <label for="search_recipient_state"><bean:message key="recipient.RecipientStatus"/>:</label>
                    <html:select styleClass="export_step2_select" property="userStatus" onchange="parametersChanged()">
                        <html:option value="0" key="default.All" />
							<html:option value="1" key="recipient.Active" />
							<html:option value="2" key="recipient.Bounced" />
							<html:option value="3" key="recipient.OptOutAdmin" />
							<html:option value="4" key="recipient.OptOutUser" />
							<html:option value="5" key="recipient.MailingState5"/>
							<agn:ShowByPermission token="blacklist">
								<html:option value="6" key="recipient.MailingState6"/>
							</agn:ShowByPermission>
							<html:option value="7" key="recipient.MailingState7"/>
                    </html:select>
                </div>
            </div>
        </div>
        <div id="advanced_search_bottom"></div>


        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="export_step2_toggle toggle_open" onclick="toggleContainer(this, 'columnsPanelVisible');"><a href="#"><bean:message key="export.columns"/></a></div>
            <div class="export_columns">

                <table border="0" cellspacing="0" cellpadding="0" >

                  <tr>
                      <td><b><bean:message key="export.Column_Name"/>&nbsp;</b></td>
                      <td><b><bean:message key="default.Type"/>&nbsp;</b></td>
                  </tr>

                  <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>">
                      <%
                      String colName=(String) pageContext.getAttribute("_agnTbl_column_name");
                      String colType=(String) pageContext.getAttribute("_agnTbl_data_type");
                      String shortName=(String) pageContext.getAttribute("_agnTbl_shortname");
                      %>
                      <tr>
                          <td><html:multibox property="columns" value="<%= colName %>"/>&nbsp;<%= shortName %>&nbsp;&nbsp;&nbsp;</td>
                          <% if( colType.toUpperCase().indexOf("CHAR") != -1 ) {%>
                          <td><bean:message key="statistic.alphanumeric"/>&nbsp;</td>
                          <% } else if( colType.toUpperCase().indexOf("NUMBER") != -1 ) { %>
                          <td><bean:message key="statistic.numeric"/>&nbsp;</td>
                          <% } else if( colType.toUpperCase().indexOf("DOUBLE") != -1 ) { %>
                          <td><bean:message key="statistic.numeric"/>&nbsp;</td>
                          <% } else if( colType.toUpperCase().indexOf("TIME") != -1 ) { %>
                          <td><bean:message key="statistic.Date"/>&nbsp;</td>
                          <% } else if( colType.toUpperCase().indexOf("DATE") != -1 ) { %>
                          <td><bean:message key="statistic.Date"/>&nbsp;</td>
                          <% } else { %>
                          <td><%= colType %>&nbsp;</td>
                          <% } %>
                      </tr>
                  </agn:ShowColumnInfo>

                </table>

            </div>
        </div>
        <div id="advanced_search_bottom"></div>


        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="export_step2_toggle toggle_open" onclick="toggleContainer(this, 'mlistsPanelVisible');"><a href="#"><bean:message key="export.add_mailinglist_information"/></a></div>
            <div class="export_mailinglists">
                <c:forEach var="mailinglist" items="${exportWizardForm.mailinglistObjects}">
                    <html:multibox property="mailinglists"
                                   value='${mailinglist.id}'/>&nbsp;${mailinglist.shortname}
                    <br>
                </c:forEach>
            </div>
        </div>
        <div id="advanced_search_bottom"></div>


        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="export_step2_toggle toggle_open" onclick="toggleContainer(this, 'fileFormatPanelVisible');"><a href="#"><bean:message key="export.file_format"/></a></div>
            <div class="export_file_format">

                <div class="export_select_div">
                <bean:message key="import.Separator"/>:
                <html:select property="separator" size="1" styleClass="export_step2_select">
                    <html:option value=";"><bean:message key="import.separator.semicolon"/></html:option>
                    <html:option value=","><bean:message key="import.separator.comma"/></html:option>
                    <html:option value="|"><bean:message key="import.separator.pipe"/></html:option>
                    <html:option value="t"><bean:message key="import.separator.tab"/></html:option>
                </html:select>
                </div>

                <div class="export_select_div">
                <bean:message key="import.Delimiter"/>:
                <html:select property="delimiter" size="1" styleClass="export_step2_select">
                    <html:option value="&#34;"><bean:message key="export.delimiter.doublequote"/></html:option>
                    <html:option value="'"><bean:message key="export.delimiter.singlequote"/></html:option>
                </html:select>
                </div>

                <div class="export_select_div">
                <agn:ShowByPermission token="mailing.show.charsets">
                    <bean:message key="mailing.Charset"/>:
                    <html:select property="charset" size="1" styleClass="export_step2_select">
                        <agn:ShowNavigation navigation="charsets" highlightKey="">
                            <agn:ShowByPermission token="<%= _navigation_token %>">
                                <html:option value="<%= _navigation_href %>"><bean:message key="<%= _navigation_navMsg %>"/></html:option>
                            </agn:ShowByPermission>
                        </agn:ShowNavigation>
                    </html:select>
                </agn:ShowByPermission>
                </div>

            </div>
        </div>
        <div id="advanced_search_bottom"></div>

        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="export_step2_toggle toggle_open" onclick="toggleContainer(this, 'datesPanelVisible');"><a
                    href="#"><bean:message key="export.dates.limits"/></a></div>
            <div class="export_dates_container">
                <div class="export_dates_item">
                    <label><bean:message key="export.dates.timestamp"/>:</label>
                    <html:text styleClass="datepicker" maxlength="10" property="timestampStart" style="width:133px;"/>
                    -
                    <html:text styleClass="datepicker" maxlength="10" property="timestampEnd" style="width:133px;"/>&nbsp;
                </div>
                <div class="export_dates_item">
                    <label><bean:message key="export.dates.creation_date"/>:</label>
                    <html:text styleClass="datepicker" maxlength="10" property="creationDateStart" style="width:133px;"/>
                    -
                    <html:text styleClass="datepicker" maxlength="10" property="creationDateEnd" style="width:133px;"/>&nbsp;
                </div>
                <div class="export_dates_item">
                    <label><bean:message key="export.dates.mailinglists"/>*:</label>
                    <html:text styleClass="datepicker" maxlength="10" property="mailinglistBindStart" style="width:133px;"/>
                    -
                    <html:text styleClass="datepicker" maxlength="10" property="mailinglistBindEnd" style="width:133px;"/>&nbsp;
                </div>
                <div class="export_dates_item">
                    <label>&nbsp;</label>
                	*&nbsp;<bean:message key="export.dates.mailinglists.hint"/>
                </div>
            </div>
        </div>
        <div id="advanced_search_bottom"></div>

    </div>


<div class="button_container export_step2_buttons_panel">
    <div class="action_button"><a href="#" onclick="document.exportWizardForm.submit();"><span><bean:message key="button.Proceed"/></span></a></div>
    <div class="action_button"><html:link page='<%= \"/exportwizard.do?action=\" + ExportWizardAction.ACTION_LIST + \"&exportPredefID=\" + request.getParameter(\"exportPredefID\")%>'><span><bean:message key="button.Back"/></span></html:link></div>
</div>


</html:form>
