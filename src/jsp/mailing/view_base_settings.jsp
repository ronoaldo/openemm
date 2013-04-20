<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
 --%>
<%@ page language="java"
         import="org.agnitas.beans.Mailing, org.agnitas.beans.MediatypeEmail,org.agnitas.cms.utils.CmsUtils, org.agnitas.util.AgnUtils, org.agnitas.web.MailingBaseAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% String aNameBase = null;
    String aNamePart = null;
    String aName = null;
    String aktName = new String("");
%>

<% int tmpMailingID = 0;
    MailingBaseForm aForm = null;
    String tmpShortname = new String("");
    if ((aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm")) != null) {
        tmpMailingID = ((MailingBaseForm) session.getAttribute("mailingBaseForm")).getMailingID();
        tmpShortname = ((MailingBaseForm) session.getAttribute("mailingBaseForm")).getShortname();
    }
    if (aForm.isIsTemplate()) {
        aForm.setShowTemplate(true);
    }
    String permToken = null;
    boolean showTargetMode = false;
    boolean showNeedsTarget = false;
    if (aForm.getTargetGroups() != null) {
        if (aForm.getTargetGroups().size() >= 2) {
            showTargetMode = true;
        }
    }
%>


<h3 class="header_coloured"><bean:message key="Settings"/>:</h3>

<div class="expand_grey_box_container">
    <div class="expand_grey_box_top toggle_open" id="settings_general_container_button"
         onclick="toggleContainer(this, 'generalContainerVisible');">
        <div class="expand_grey_box_top_subcontainer"><a href="#General"><bean:message key="General"/>:</a></div>
    </div>
    <div>
        <div class="expand_grey_box_content">
            <div class="settings_general_left_column">

                <logic:equal name="mailingBaseForm" property="isTemplate" value="false">
                    <div class="settings_general_form_item">
                        <logic:equal name="mailingBaseForm" property="mailingID" value="0">
                            <logic:equal name="mailingBaseForm" property="copyFlag" value="false">
                                <label for="settings_general_template"><bean:message key="Template"/>:</label>
                                <html:select styleId="settings_general_template" property="templateID"
                                             onchange="document.mailingBaseForm.action.value=7; document.mailingBaseForm.submit();">
                                    <html:option value="0"><bean:message key="mailing.No_Template"/></html:option>
                                    <logic:iterate id="agntbl3" name="mailingBaseForm" property="templateMailingBases" length="500">
                                       <html:option value="${agntbl3.id}">${agntbl3.shortname}</html:option>
                                    </logic:iterate>
                                </html:select>
                            </logic:equal>

                            <logic:equal name="mailingBaseForm" property="copyFlag" value="true">
                                <label><bean:message key="Template"/>:</label>
                                <span id="agntbl44">${mailingBaseForm.templateShortname}</span>
                            </logic:equal>
                        </logic:equal>
                        <logic:notEqual name="mailingBaseForm" property="mailingID" value="0">
                            <label><bean:message key="Template"/>:</label>
                            <span id="agntbl44">${mailingBaseForm.templateShortname}</span>
                        </logic:notEqual>
                    </div>
                </logic:equal>
                <logic:equal name="mailingBaseForm" property="isTemplate" value="true">
                    <html:hidden property="templateID" value="0"/>
                </logic:equal>

                <div class="settings_general_form_item">
                    <label for="settings_general_mailingliste"><bean:message key="Mailinglist"/>:</label>
                    <html:select styleId="settings_general_mailingliste" property="mailinglistID" size="1"
                                 disabled="${mailingBaseForm.worldMailingSend}" styleClass="dropdown">
                        <logic:iterate id="agntbl2" name="mailingBaseForm" property="mailingLists">
                            <html:option value="${agntbl2.id}">${agntbl2.shortname}</html:option>
                        </logic:iterate>
                    </html:select>
                </div>
                <agn:ShowByPermission token="campaign.show">
                    <div class="settings_general_form_item">
                        <label for="settings_general_campaign"><bean:message key="campaign.Campaign"/>:</label>
                        <html:select styleId="settings_general_campaign" property="campaignID">
                            <html:option value="0"><bean:message key="mailing.NoCampaign"/></html:option>
                            <logic:iterate id="agntbl55" name="mailingBaseForm" property="campaigns" length="500">
                                <html:option value="${agntbl55.id}">${agntbl55.shortname}</html:option>
                            </logic:iterate>
                        </html:select>
                    </div>
                </agn:ShowByPermission>

                <agn:ShowByPermission token="action.op.GetArchiveList">
                    <div class="settings_general_form_item">
                        <input type="hidden" name="__STRUTS_CHECKBOX_archived" value="0"/>
                        <html:checkbox styleId="settings_general_in_archiv" property="archived"/>
                        <label for="settings_general_in_archiv" id="settings_general_in_archiv_label"><bean:message
                                key="mailing.archived"/></label>
                    </div>
                </agn:ShowByPermission>
            </div>
            <div class="settings_general_right_column">
                <agn:ShowByPermission token="mailing.show.types">
                    <div class="settings_general_form_item">
                        <label for="mailType"><bean:message key="mailing.Mailing_Type"/>:</label>
                        <html:select property="mailingType" size="1" styleId="mailType" onchange="checkButton(this.id)"
                                     disabled="<%= aForm.isWorldMailingSend() %>">
                            <html:option value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>"><bean:message
                                    key="mailing.Normal_Mailing"/></html:option>
                            <html:option value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>"><bean:message
                                    key="mailing.Event_Mailing"/></html:option>
                            <html:option value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>"><bean:message
                                    key="mailing.Rulebased_Mailing"/></html:option>
                        </html:select>
                    </div>
                </agn:ShowByPermission>
            </div>
        </div>
        <div class="expand_grey_box_bottom"></div>
    </div>
</div>


<div class="expand_grey_box_container">
    <div class="expand_grey_box_top toggle_open" id="settings_targetgroups_container_button"
         onclick="toggleContainer(this, 'targetgroupsContainerVisible');">
        <div class="expand_grey_box_top_subcontainer">
            <a href="#Targets"><bean:message key="Targets"/>:</a>
        </div>
    </div>
    <div>
        <div class="expand_grey_box_content">
            <div class="settings_targetgroups_left_column">
                <div class="targetgroups_select_container">
                    <logic:equal name="mailingBaseForm" property="worldMailingSend" value="false">
                        <div class="float_left">
                            <html:select property="targetID" size="1" styleClass="dropdown">
                                <html:option value="0">---</html:option>
                                <logic:iterate id="agntbl3" name="mailingBaseForm" property="targets" length="500">
                                     <html:option value="${agntbl3.id}">${agntbl3.targetName}</html:option>
                                </logic:iterate>
                            </html:select>
                        </div>
                        <div class="float_left add_target_button">
                            <input type="hidden" name="addtarget" value=""/>

                            <a href="#" class="settings_targetgroups_add"
                               onclick="document.mailingBaseForm.addtarget.value='addtarget'; document.mailingBaseForm.submit();return false;">
                                <bean:message key="mailing.AddTargetGroup"/>
                            </a>

                        </div>
                    </logic:equal>
                </div>
                <div class="settings_targetgroups_added_targetgroups">
                    <logic:present name="mailingBaseForm" property="targetGroups">
                        <logic:iterate name="mailingBaseForm" property="targetGroupsList" id="aTarget" length="100">
                                <c:choose>
                                    <c:when test="${aTarget.deleted == 0}">
                                        <div>${aTarget.targetName}
                                            &nbsp;
                                            <logic:equal name="mailingBaseForm" property="worldMailingSend"
                                                         value="false">
                                                <input type="hidden" name="removetarget${aTarget.id}" value=""/>
                                                <a href="#"
                                                   onclick="document.mailingBaseForm.removetarget${aTarget.id}.value='removetarget${aTarget.id}';document.mailingBaseForm.submit();return false;"
                                                   class="removeTargetgroup"><img
                                                        src="${emmLayoutBase.imagesURL}/removetargetgroup.png"/></a>
                                            </logic:equal>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
						     	 <span class="warning">${aTarget.targetName} (<bean:message
                                          key="target.Deleted"/>)&nbsp;
                                      <logic:equal name="mailingBaseForm" property="worldMailingSend" value="false">
                                          <input type="hidden" name="removetarget${aTarget.id}" value=""/>
                                          <a href="#"
                                             onclick="document.mailingBaseForm.removetarget${aTarget.id}.value='removetarget${aTarget.id}';document.mailingBaseForm.submit();return false;"
                                             class="removeTargetgroup">
                                              <img src="${emmLayoutBase.imagesURL}/removetargetgroup.png"/>
                                          </a>
                                      </logic:equal>
                                  </span></br>
                                    </c:otherwise>
                                </c:choose>
                            </logic:iterate>
                    </logic:present>
                    <logic:notPresent name="mailingBaseForm" property="targetGroups">
                        <div><bean:message key="statistic.All_Subscribers"/></div>
                    </logic:notPresent>
                </div>
            </div>
            <div class="settings_targetgroups_right_column">
                <% if (showTargetMode) { %>
                <input type="hidden" name="__STRUTS_CHECKBOX_targetMode" value="0"/>

                <div class="settings_targetgroups_form_item">
                    <html:checkbox styleId="checkbox_target_mode" property="targetMode" value="1"
                                   disabled="<%= aForm.isWorldMailingSend() %>"/>
                    <label for="checkbox_target_mode" id="checkbox_target_mode_label"><bean:message
                            key="mailing.targetmode.and"/></label>
                </div>
                <% if (aForm.isWorldMailingSend()) { %>
                <html:hidden property="targetMode"/>
                <% } %>
                <% } else { %>
                <html:hidden property="targetMode"/>
                <% } %>
                <% if (showNeedsTarget) { %>
                <input type="hidden" name="__STRUTS_CHECKBOX_needsTarget" value="false"/>

                <div class="settings_targetgroups_form_item">
                    <html:checkbox styleId="checkbox_needs_target" property="needsTarget"
                                   disabled="<%= (aForm.isWorldMailingSend() || (aForm.getMailingType()==Mailing.TYPE_DATEBASED)) %>"/>
                    <label for="checkbox_needs_target" id="checkbox_needs_target_label"><bean:message
                            key="mailing.needsTarget"/></label>
                </div>
                <% if (aForm.isWorldMailingSend() || (aForm.getMailingType() == Mailing.TYPE_DATEBASED)) { %>
                <html:hidden property="needsTarget"/>
                <% } %>
                <% } else { %>
                <html:hidden property="needsTarget"/>
                <% } %>
            </div>

        </div>
        <div class="expand_grey_box_bottom"></div>
    </div>
</div>
<br>