<%-- to be checked md? --%>
<%@ page language="java"
         import="org.agnitas.util.EmmCalendar, org.agnitas.web.forms.CompareMailingForm"
         contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/mailing_compare">
    <html:hidden property="action"/>
    <% // define Hashtable for Download file:
        org.agnitas.util.EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
        java.util.Date my_time = my_calendar.getTime();
        String Datum = my_time.toString();
        String timekey = Long.toString(my_time.getTime());
        pageContext.setAttribute("time_key", timekey);

        if (pageContext.getSession().getAttribute("map") == null) {
            java.util.Hashtable my_map = new java.util.Hashtable();
            pageContext.getSession().setAttribute("map", my_map);
        } %>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column" style="float: none;">

                <div align="right" class="float_right box_button">
                    <html:link page='<%= new String(\"/file_download?key=\" + timekey) %>'>
                        <img src="${emmLayoutBase.imagesURL}/icon_save.gif" border="0">
                    </html:link>
                </div>

                <div class="grey_box_form_item stat_recipient_form_item">
                    <div class="compare_view_group_container">
                        <bean:message key="target.Target"/>:
                        <html:select property="targetID" size="1">
                            <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                            <c:forEach var="targetGroup" items="${targetGroups}">
                                <html:option value="${targetGroup.id}">
                                    ${targetGroup.targetName}
                                </html:option>
                            </c:forEach>
                        </html:select>
                    </div>

                </div>

            </div>

            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <a href="#"
                       onclick="document.compareMailingForm.submit();return false;">
                        <span><bean:message key="button.OK"/></span>
                    </a>
                </div>
            </div>

        </div>
        <div class="grey_box_bottom"></div>
    </div>

    </br>
    <% CompareMailingForm form = null;
        if (session.getAttribute("compareMailingForm") != null) {
            form = ((CompareMailingForm) session.getAttribute("compareMailingForm"));
        }

        String file = form.getCvsfile();
    %>


    <table id="mailings" border="0" cellspacing="0" cellpadding="0" class="list_table compare_view_table" width="100%">
        <tr>
            <th class="comparasion_view_mailing"><bean:message key="Mailing"/></th>
            <td></td>
            <th class="comparasion_view_recipients"><bean:message key="Recipients"/></th>
            <td></td>
            <th class="comparasion_view_clicks"><bean:message key="statistic.Clicks"/></th>
            <td></td>
            <th class="comparasion_view_opened"><bean:message key="statistic.opened"/></th>
            <td></td>
            <th class="comparasion_view_bounces"><bean:message key="statistic.Bounces"/></th>
            <td></td>
            <th class="comparasion_view_outs"><bean:message key="statistic.Opt_Outs"/></th>
            <td></td>
        </tr>

        <% // Integer mailingID=null;
            Hashtable allClicks = form.getNumClicks();
            Hashtable allReceive = form.getNumRecipients();
            Hashtable allOpen = form.getNumOpen();
            Hashtable allBounce = form.getNumBounce();
            Hashtable allOptout = form.getNumOptout();
            Hashtable allNames = form.getMailingName();
            Hashtable allDesc = form.getMailingDescription();
        %>
        <logic:iterate name="compareMailingForm" property="mailings" id="mailingID">
            <% Integer aMailingID = (Integer) pageContext.getAttribute("mailingID");
                file += allNames.get(aMailingID) + ";";
                file += allReceive.get(aMailingID) + ";";
                file += allClicks.get(aMailingID) + ";";
                file += allOpen.get(aMailingID) + ";";
                file += allBounce.get(aMailingID) + ";";
                file += allOptout.get(aMailingID) + "\n";

                String desc = (String) allDesc.get(mailingID);

                if (desc != null && desc.length() > 0) {
                    desc = "(" + desc + ")";
                } else {
                    desc = "<br>";
                }
            %>
            <tr>
                <td>
                    <span class="ie7hack">
                        <html:link page='<%= new String(\"/mailing_stat.do?action=7&mailingID=\" + aMailingID.intValue()) %>'><b><%= allNames.get(aMailingID) %>
                        </b><br><%= desc %>
                        </html:link>
                    </span>
                </td>
                <td background="${emmLayoutBase.imagesURL}/border_06.gif"><img
                        src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
                        height="10" border="0"></td>
                <%
                    float recipientsWidth = 1;
                    if(form.getBiggestRecipients() != 0)
                        recipientsWidth = ((float)((Integer)allReceive.get(aMailingID)).intValue() / (float)form.getBiggestRecipients() ) * 50;
                %>
                <td>&nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                               width="<%=  recipientsWidth %>"
                               height="10">&nbsp;
                    <br>

                    <div align=left>&nbsp;<%= allReceive.get(aMailingID) %>
                    </div>
                </td>
                <td background="${emmLayoutBase.imagesURL}/border_06.gif"><img
                        src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
                        height="10" border="0"></td>
                <%
                    float clicksWidth = 1;
                    if(form.getBiggestClicks() != 0)
                        clicksWidth = ((float)((Integer)allClicks.get(aMailingID)).intValue() / (float)form.getBiggestClicks() ) * 50;
                %>
                <td>&nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                               width="<%=  clicksWidth %>"
                               height="10">&nbsp;
                    <br>

                    <div align=left>&nbsp;<%= allClicks.get(aMailingID) %>
                    </div>
                </td>
                <td background="${emmLayoutBase.imagesURL}/border_06.gif"><img
                        src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
                        height="10" border="0"></td>
                <%
                    float openedWidth = 1;
                    if(form.getBiggestOpened() != 0)
                        openedWidth = ((float)((Integer)allOpen.get(aMailingID)).intValue() / (float)form.getBiggestOpened() ) * 50;
                %>
                <td>&nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                               width="<%=  openedWidth %>"
                               height="10">&nbsp;
                    <br>

                    <div align=left>&nbsp;<%= allOpen.get(aMailingID) %>
                    </div>
                </td>
                <td background="${emmLayoutBase.imagesURL}/border_06.gif"><img
                        src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
                        height="10" border="0"></td>
                <%
                    float bounceWidth = 1;
                    if(form.getBiggestBounce() != 0)
                        bounceWidth = ((float)((Integer)allBounce.get(aMailingID)).intValue() / (float)form.getBiggestBounce() ) * 50;
                %>
                <td>&nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                               width="<%= bounceWidth  %>"
                               height="10">&nbsp;
                    <br>

                    <div align=left>&nbsp;<%= allBounce.get(aMailingID) %>
                    </div>
                </td>
                <td background="${emmLayoutBase.imagesURL}/border_06.gif"><img
                        src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
                        height="10" border="0"></td>
                <%
                    float optoutsWidth = 1;
                    if(form.getBiggestOptouts() != 0)
                        optoutsWidth = ((float)((Integer)allOptout.get(aMailingID)).intValue() / (float)form.getBiggestOptouts() ) * 50;
                %>
                <td>&nbsp;<img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                               width=" <%= optoutsWidth%>"
                               height="10">&nbsp;
                    <br>

                    <div align=left>&nbsp;<%= allOptout.get(aMailingID) %>
                    </div>
                </td>
            </tr>
        </logic:iterate>

    </table>

    <% ((java.util.Hashtable) pageContext.getSession().getAttribute("map")).put(pageContext.getAttribute("time_key"), file); %>

    <div class="button_container">
        <div class="action_button">
            <html:link page="/mailing_compare.do?action=1">
                <span>
                    <bean:message key="button.Back"/>
                </span>
            </html:link>
        </div>
    </div>

</html:form>