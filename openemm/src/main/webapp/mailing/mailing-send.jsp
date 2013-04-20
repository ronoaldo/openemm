<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.beans.Mailing, org.agnitas.util.AgnUtils, org.agnitas.web.MailingSendAction, java.text.DateFormat, java.text.DecimalFormat, java.text.NumberFormat"
         contentType="text/html; charset=utf-8" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="org.agnitas.web.MailingSendForm" %>
<%@ page import="org.agnitas.stat.DeliveryStat" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% int tmpMailingID = 0;
    DeliveryStat aDelstat = null;
    MailingSendForm aForm = null;
    if (request.getAttribute("mailingSendForm") != null) {
        aForm = (MailingSendForm) request.getAttribute("mailingSendForm");
        tmpMailingID = aForm.getMailingID();
        aDelstat = aForm.getDeliveryStat();
    }
%>

<div id="filterbox_container_for_name">
    <div class="filterbox_form_container">
        <div id="filterbox_top"></div>
        <div id="searchbox_content" class="filterbox_form_container">
            <div id="filterbox_small_label" class="filterbox_small_label_tab">
                ${mailingSendForm.shortname}&nbsp;&nbsp;<%if (aForm != null && aForm.getDescription() != null && !aForm.getDescription().isEmpty()) {%>
                |&nbsp;&nbsp;${mailingSendForm.description}<% } %>
            </div>
        </div>
        <div id="filterbox_bottom"></div>
    </div>
</div>
<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">
        <div class="send_page_panel_text"><bean:message key="mailing.SendPreviewMessage"></bean:message></div>
        <div class="button_grey_box_container">
            <div class="action_button no_margin_right no_margin_bottom">
                <html:link
                        page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_PREVIEW_SELECT + \"&mailingID=\" + tmpMailingID) %>'>
                    <span><bean:message key="mailing.Preview"/></span>
                </html:link>
            </div>
        </div>
    </div>
    <div class="grey_box_bottom"></div>
</div>

<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">
        <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
            <logic:equal name="mailingSendForm" property="mailingtype"
                         value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>">
                <bean:message key="mailing.MailingSentAllready"/>
            </logic:equal>
            <logic:equal name="mailingSendForm" property="mailingtype"
                         value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
                <bean:message key="mailing.deactivate_event_explain"/>
            </logic:equal>
            <logic:equal name="mailingSendForm" property="mailingtype"
                         value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
                <bean:message key="mailing.deactivate_rule_explain"/>
                <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                    <% DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)); %>
                    <br><bean:message
                        key="mailing.SendingTimeDaily"/>:&nbsp;<%= timeFormat.format(aDelstat.getScheduledSendTime()) %>
                </logic:equal>
            </logic:equal>
        </logic:equal>

        <logic:equal name="mailingSendForm" property="worldMailingSend" value="false">
            <logic:equal name="mailingSendForm" property="mailingtype"
                         value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>">
                <bean:message key="mailing.MailingReadyForSending"/>
            </logic:equal>
            <logic:equal name="mailingSendForm" property="mailingtype"
                         value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
                <bean:message key="mailing.activate_event_explain"/>
            </logic:equal>
            <logic:equal name="mailingSendForm" property="mailingtype"
                         value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
                <bean:message key="mailing.activate_rule_explain"/>
            </logic:equal>
        </logic:equal>
    </div>
    <div class="grey_box_bottom"></div>
</div>

<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">

    <div class="send_page_button_text"><bean:message key="link.check"/>:</div>
    <div class="button_container send_page_button_container">
	    <div class="action_button send_page_button">
    	    <html:link page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_CHECK_LINKS + \"&mailingID=\" + tmpMailingID) %>'>
				<span><bean:message key="button.check"/></span>
			</html:link>
		</div>
	</div>

        <agn:ShowByPermission token="mailing.send.admin">
            <c:choose>
                <c:when test="${not mailingSendForm.hasDeletedTargetGroups}">
                    <div class="send_page_button_text"><bean:message key="adminMail"/>:</div>
                    <div class="button_container send_page_button_container">
                        <div class="action_button send_page_button">
                            <html:link
                                    page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_SEND_ADMIN + \"&mailingID=\" + tmpMailingID) %>'>
                                <span><bean:message key="button.Send"/></span>
                            </html:link>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="send_page_button_text"><span class="warning">
                    <bean:message key="MailingTestAdmin.deleted_target_groups"/></span>
                    </div>
                </c:otherwise>
            </c:choose>
        </agn:ShowByPermission>

        <agn:ShowByPermission token="mailing.send.test">
        <c:choose>
            <c:when test="${not mailingSendForm.hasDeletedTargetGroups}">
                <div class="send_page_button_text"><bean:message key="testMail"/>:</div>
                <div class="button_container send_page_button_container">
                    <div class="action_button send_page_button">
                        <html:link
                                page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_SEND_TEST + \"&mailingID=\" + tmpMailingID) %>'>
                            <span><bean:message key="button.Send"/></span>
                        </html:link>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="send_page_button_text"><span class="warning"><bean:message
                        key="MailingTestDistrib.deleted_target_groups"/></span>
                </div>
            </c:otherwise>
        </c:choose>
    </agn:ShowByPermission>

    <logic:equal name="mailingSendForm" property="isTemplate" value="false">
        <logic:equal name="mailingSendForm" property="mailingtype"
                     value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
            <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                <agn:ShowByPermission token="mailing.send.world">
                    <div class="send_page_button_text"><bean:message key="mailing.MailingDeactivate"/>:</div>
                    <div class="button_container send_page_button_container">
                        <div class="action_button send_page_button">
                            <html:link
                                    page='<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_DEACTIVATE_MAILING + "&mailingID=" + tmpMailingID) %>'>
                                <span><bean:message key="btndeactivate"/></span>
                            </html:link>
                        </div>
                    </div>
                </agn:ShowByPermission>
            </logic:equal>
        </logic:equal>

        <logic:equal name="mailingSendForm" property="mailingtype"
                     value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
            <logic:equal name="mailingSendForm" property="worldMailingSend" value="true">
                <agn:ShowByPermission token="mailing.send.world">
                    <div class="send_page_button_text"><bean:message key="mailing.MailingDeactivate"/>:</div>
                    <div class="button_container send_page_button_container">
                        <div class="action_button send_page_button">
                            <html:link
                                    page='<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_DEACTIVATE_MAILING + "&mailingID=" + tmpMailingID) %>'>
                                <span><bean:message key="btndeactivate"/></span>
                            </html:link>
                        </div>
                    </div>
                </agn:ShowByPermission>
            </logic:equal>
        </logic:equal>

        <logic:equal name="mailingSendForm" property="mailingtype"
                     value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>">
            <logic:equal name="mailingSendForm" property="worldMailingSend" value="false">
                <agn:ShowByPermission token="mailing.send.world">
                    <div class="send_page_button_text"><bean:message key="mailing.MailingActivate"/>:</div>
                    <div class="button_container send_page_button_container">
                        <div class="action_button send_page_button">
                            <html:link
                                    page='<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_ACTIVATE_CAMPAIGN + "&to=3&mailingID=" + tmpMailingID) %>'>
                                <span><bean:message key="btnactivate"/></span>
                            </html:link>
                        </div>
                    </div>
                </agn:ShowByPermission>
            </logic:equal>
        </logic:equal>

        <logic:equal name="mailingSendForm" property="mailingtype"
                     value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>">
            <logic:equal name="mailingSendForm" property="worldMailingSend" value="false">
                <agn:ShowByPermission token="mailing.send.world">
                    <html:form action="/mailingsend">
                        <div class="datebased_mailing_panel">
                        <input type="hidden" name="action" value="<%= MailingSendAction.ACTION_ACTIVATE_RULEBASED %>">
                        <input type="hidden" name="to" value="4">
                        <html:hidden property="mailingID"/>

                        <div class="send_page_button_text"><bean:message key="mailing.MailingActivate"/>:</div>
                        <div class="button_container send_page_button_container">
                            <div class="action_button send_page_button">
                                <a href="#" onclick="document.getElementsByName('mailingSendForm')[0].submit();">
                                    <span><bean:message key="btnactivate"/></span>
                                </a>
                            </div>
                        </div>
                        <%
                            int i;
                            TimeZone aZone = AgnUtils.getTimeZone(request);
                            GregorianCalendar aDate = new GregorianCalendar(aZone);
                            // aDate.setTimeZone(aZone);
                            // DateFormat showFormat=new SimpleDateFormat("dd.MM.yyyy");
                            DateFormat showFormat = DateFormat.getDateInstance(DateFormat.FULL, (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
                            DateFormat internalFormat = new SimpleDateFormat("yyyyMMdd");
                            NumberFormat aFormat = new DecimalFormat("00");
                        %>
                        <div class="datebased_mailing_time_panel">
                            <input type="hidden" name="sendDate" value="<%= internalFormat.format(aDate.getTime()) %>">

                            <div class="send_page_button_text datebased_mailing_time_text"><bean:message
                                    key="mailing.SendingTimeDaily"/>:&nbsp;</div>
                            <div class="send_page_button_text">
                                <html:select property="sendHour" size="1">
                                    <% for (i = 0; i <= 23; i++) { %>
                                    <html:option
                                            value="<%= Integer.toString(i) %>"><%=aFormat.format((long) i) %>:00h</html:option>
                                    <% } %>
                                </html:select>
                            </div>
                            <input type="hidden" name="sendMinute" value="0">

                            <div class="send_page_button_text datebased_mailing_time_text">&nbsp;<%= aZone.getID() %>
                            </div>
                        </div>
                    </html:form>
                    </div>
                </agn:ShowByPermission>
            </logic:equal>
        </logic:equal>

        <agn:ShowByPermission token="mailing.send.world">
            <logic:equal name="mailingSendForm" property="canSendWorld" value="true">
                <c:choose>
                    <c:when test="${not mailingSendForm.hasDeletedTargetGroups}">
                        <div class="send_page_button_text send_now_text"><bean:message key="Mailing"/>:</div>
                        <div class="button_container send_page_button_container">
                            <div class="action_button send_page_button">
                                <html:link
                                        page='<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_VIEW_SEND2 + "&mailingID=" + tmpMailingID) %>'>
                                    <span><bean:message key="button.Send"/></span>
                                </html:link>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="send_page_button_text">
                            <span class="warning"><bean:message key="MailingSendNow.deleted_target_groups"/></span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </logic:equal>
        </agn:ShowByPermission>

    </logic:equal>
</div>
<div class="grey_box_bottom"></div>
</div>

<logic:equal name="mailingSendForm" property="mailingtype" value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>">
    <iframe name="delstatbox" id="statFrame"
            src="<html:rewrite page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_VIEW_DELSTATBOX + \"&mailingID=\" + tmpMailingID) %>'/>"
            ALLOWTRANSPARENCY="true" width="950" height="300" bgcolor="#73A2D0" scrolling="no" frameborder="0">
        <bean:message key="import.csv_no_iframe"/>
    </iframe>
    <br>
</logic:equal>
