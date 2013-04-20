<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.web.forms.*, org.agnitas.beans.*, org.agnitas.stat.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<%
    int tmpCampaignID = (Integer) request.getAttribute("tmpCampaignID");
    String file = "";
    String timekey = "";
    java.util.Hashtable my_map = null;
    if (tmpCampaignID != 0) {
        // csv download stuff: 
        org.agnitas.util.EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
        TimeZone zone = TimeZone.getTimeZone(((Admin) session.getAttribute("emm.admin")).getAdminTimezone());

        my_calendar.changeTimeWithZone(zone);
        java.util.Date my_time = my_calendar.getTime();
        String Datum = my_time.toString();

        timekey = Long.toString(my_time.getTime());
        pageContext.setAttribute("time_key", timekey);
        my_map = null;

        if (pageContext.getSession().getAttribute("map") == null)    // original line
        {
            my_map = new java.util.Hashtable();
            pageContext.getSession().setAttribute("map", my_map);
            // pageContext.setAttribute("map",my_map);
        } else {
            // my_map = (java.util.Hashtable)(pageContext.getAttribute("map"));
            my_map = (java.util.Hashtable) (pageContext.getSession().getAttribute("map"));
        }

        file = ((CampaignForm) session.getAttribute("campaignForm")).getCsvfile();

//    put csv file in pagecontext:
        // my_map.put(timekey,  file);
        // pageContext.setAttribute("map", my_map);
        //request.setAttribute("map", my_map);

    }
%>

<html:form action="/campaign">
    <html:hidden property="action"/>
    <html:hidden property="campaignID"/>
    <html:hidden property="__STRUTS_CHECKBOX_netto" value="false"/>

    <div class="grey_box_container archive_stat_panel">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <div class="grey_box_form_item stat_recipient_form_item">
                    <bean:message key="target.Target"/>:&nbsp;
                    <html:select property="targetID">
                        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <c:forEach var="target" items="${targetGroups}">
                            <html:option value="${target.id}">
                                ${target.targetName}
                            </html:option>
                        </c:forEach>
                    </html:select><br><br>
                </div>
            </div>
            <div class="grey_box_center_column">
                <html:checkbox property="netto"/>&nbsp;<bean:message key="statistic.Unique_Clicks"/>
            </div>
            <div class="campaign_stat_save">
                <html:link page='<%= new String("/file_download?key=" + timekey) %>'><img
                        src="${emmLayoutBase.imagesURL}/icon_save.gif"
                        border="0"></html:link>
            </div>
            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <a href="#" onclick="document.campaignForm.submit();"><span><bean:message
                            key="button.OK"/></span></a>
                </div>
            </div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <% if (tmpCampaignID != 0) { %>


    <table border="0" cellspacing="0" cellpadding="0" class="list_table">

        <%
            file = "\"" + SafeString.getLocaleString("Mailing", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
            file += "\"" + SafeString.getLocaleString("statistic.Opened_Mails", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
            file += "\"" + SafeString.getLocaleString("statistic.Opt_Outs", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
            file += "\"" + SafeString.getLocaleString("statistic.Bounces", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
            file += "\"" + SafeString.getLocaleString("Recipients", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";";
            file += "\"" + SafeString.getLocaleString("statistic.Clicks", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
            file += "\r\n";
        %>


        <tr>
            <th><bean:message key="Mailing"/>&nbsp;</th>

            <th><bean:message key="statistic.Opened_Mails"/></th>

            <th><bean:message key="statistic.Opt_Outs"/></th>

            <th><bean:message key="statistic.Bounces"/></th>

            <th><bean:message key="Recipients"/></th>

            <th><bean:message key="statistic.Clicks"/></th>

        </tr>

        <% CampaignForm aForm = (CampaignForm) session.getAttribute("campaignForm");
            // Hashtable mailingData = ((CampaignForm)session.getAttribute("campaignForm")).getMailingData();
            Hashtable mailingData = aForm.getMailingData();
            if (mailingData == null) {
                mailingData = new Hashtable();
            }
            // Enumeration keys = mailingData.keys();
            Iterator<Number> keys = aForm.getSortedKeys().iterator();
            ApplicationContext aContext = WebApplicationContextUtils.getWebApplicationContext(application);
            // CampaignStatEntry aktEntry = (CampaignStatEntry) aContext.getBean("CampaignStatEntry");
            CampaignStatEntry aktEntry = null;

            int aktKey = 0;

            while (keys.hasNext()) {
                aktKey = (keys.next()).intValue();
                aktEntry = (CampaignStatEntry) (mailingData.get(new Integer(aktKey)));

        %>
        <tr>
            <td><html:link
                    page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + aktKey) %>'><b>
                <nobr><% if (aktEntry.getShortname().length() > 25) { %><%= aktEntry.getShortname().substring(0, 24) %><% } else { %><%= aktEntry.getShortname() %><% } %>
                    &nbsp;</nobr>
            </b></html:link></td>
            <% file += "\"" + aktEntry.getShortname() + "\";"; %>

            <td align="right">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                           width="<%=  ((float)(aktEntry.getOpened()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxOpened() ) * 50 + 1 %>"
                           height="10"><br><%= aktEntry.getOpened() %>&nbsp;
            </td>
            <% file += "\"" + aktEntry.getOpened() + "\";"; %>

            <td align="right">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                           width="<%=  ((float)(aktEntry.getOptouts()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxOptouts() ) * 50 + 1 %>"
                           height="10"><br><%= aktEntry.getOptouts() %>&nbsp;
            </td>
            <% file += "\"" + aktEntry.getOptouts() + "\";"; %>

            <td align="right">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                           width="<%=  ((float)(aktEntry.getBounces()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxBounces() ) * 50 + 1 %>"
                           height="10"><br><%= aktEntry.getBounces() %>&nbsp;
            </td>
            <% file += "\"" + aktEntry.getBounces() + "\";"; %>

            <td align="right">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                           width="<%=  ((float)(aktEntry.getTotalMails()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxSubscribers() ) * 50 + 1 %>"
                           height="10"><br><%= aktEntry.getTotalMails() %>&nbsp;
            </td>
            <% file += "\"" + aktEntry.getTotalMails() + "\";"; %>

            <td align="right">
                &nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                           width="<%=  ((float)(aktEntry.getClicks()) / (float)((CampaignForm)session.getAttribute("campaignForm")).getMaxClicks() ) * 50 + 1 %>"
                           height="10"><br><%= aktEntry.getClicks() %>&nbsp;
            </td>
            <% file += "\"" + aktEntry.getClicks() + "\"" + "\r\n"; %>
        </tr>
        <% } %>

        <tr>
            <td><b><bean:message key="statistic.Total"/></b></td>
            <%
                file += "\"" + SafeString.getLocaleString("statistic.Total", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";"; %>

            <td align="right">
                <b><%= ((CampaignForm) session.getAttribute("campaignForm")).getOpened() %>&nbsp;</b>
            </td>
            <% file += "\"" + ((CampaignForm) session.getAttribute("campaignForm")).getOpened() + "\";"; %>

            <td align="right">
                <b><%= ((CampaignForm) session.getAttribute("campaignForm")).getOptouts() %>&nbsp;</b>
            </td>
            <% file += "\"" + ((CampaignForm) session.getAttribute("campaignForm")).getOptouts() + "\";"; %>

            <td align="right">
                <b><%= ((CampaignForm) session.getAttribute("campaignForm")).getBounces() %>&nbsp;</b>
            </td>
            <% file += "\"" + ((CampaignForm) session.getAttribute("campaignForm")).getBounces() + "\";"; %>

            <td align="right">
                <b><%= ((CampaignForm) session.getAttribute("campaignForm")).getSubscribers() %>&nbsp;</b>
            </td>
            <% file += "\"" + ((CampaignForm) session.getAttribute("campaignForm")).getSubscribers() + "\";"; %>

            <td align="right">
                <b><%= ((CampaignForm) session.getAttribute("campaignForm")).getClicks() %>&nbsp;
                </b>
            </td>
            <% file += "\"" + ((CampaignForm) session.getAttribute("campaignForm")).getClicks() + "\"\r\n"; %>
        </tr>

    </table>

    <div class="button_container">
        <div class="action_button">
            <html:link page='<%= new String("/campaign.do?action=" + CampaignAction.ACTION_LIST) %>'><span><bean:message
                    key="button.Cancel"/></span></html:link>
        </div>
        <div class="action_button"><bean:message key="Statistics"/>:</div>
    </div>

    <% } %>

    <% // put csv file in request-Context.
        my_map.put(timekey, file);
        pageContext.getSession().setAttribute("map", my_map);
        // pageContext.getSession().setAttribute("map", timekey);
    %>

</html:form>