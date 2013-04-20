<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.beans.Admin, org.agnitas.util.AgnUtils,org.agnitas.web.forms.IPStatForm , java.util.TimeZone"
         buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% // key for the csv download
    java.util.TimeZone tz = TimeZone.getTimeZone(((Admin) session.getAttribute("emm.admin")).getAdminTimezone());
    java.util.GregorianCalendar aCal = new java.util.GregorianCalendar(tz);
    java.util.Date my_time = aCal.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    java.util.Hashtable my_map = null;
    if (pageContext.getSession().getAttribute("map") == null) {
        my_map = new java.util.Hashtable();
        pageContext.getSession().setAttribute("map", my_map);
    } else {
        my_map = (java.util.Hashtable) (pageContext.getSession().getAttribute("map"));
    }
    String file = ((IPStatForm) (session.getAttribute("ipStatForm"))).getCsvfile();
    my_map.put(timekey, file);
    pageContext.getSession().setAttribute("map", my_map);
%>


<html:form action="/ip_stats">
    <html:hidden property="action"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column" style="float: none;">

                <div align="right" class="float_right box_button"><html:link
                        page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                        src="${emmLayoutBase.imagesURL}/icon_save.gif"
                        border="0"></html:link></div>

                <div class="grey_box_form_item stat_recipient_form_item">
                    <label><bean:message key="target.Target"/>:</label>
                    <html:select property="targetID" size="1">
                        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <c:forEach var="target" items="${targets}">
                            <html:option value="${target.id}">
                                ${target.targetName}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>

                <div class="grey_box_form_item stat_recipient_form_item">
                    <label><bean:message key="Mailinglist"/>:</label>
                    <html:select property="listID" size="1">
                        <html:option value="0"><bean:message key="statistic.All_Mailinglists"/></html:option>
                        <c:forEach var="mlist" items="${mailinglists}">
                            <html:option value="${mlist.id}">
                                ${mlist.shortname}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>
            </div>

            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <a href="#" onclick="document.ipStatForm.submit();">
                        <span><bean:message key="button.OK"/></span>
                    </a>
                </div>
            </div>

        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="upload_start_container">

        <table border="0" cellspacing="0" cellpadding="0">

            <% if ((((IPStatForm) session.getAttribute("ipStatForm")).getTotal()) != 0) {
            %>

            <tr>
                <td colspan="3">
                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td><span class="head3"><bean:message key="statistic.IPAddress"/></span>&nbsp;&nbsp;</td>
                            <td colspan="2">&nbsp;&nbsp;<span class="head3"><bean:message key="Recipients"/></span></td>
                        </tr>

                        <tr>
                            <td colspan="3">
                                <hr size="1">
                            </td>
                        </tr>

                        <% int j = 0;
                            while (j < ((IPStatForm) session.getAttribute("ipStatForm")).getLines()) { %>
                        <tr>
                            <td><%= ((IPStatForm) session.getAttribute("ipStatForm")).getIps(j) %>&nbsp;&nbsp;</td>
                            <td align="right"><%= (((IPStatForm) session.getAttribute("ipStatForm")).getSubscribers(j)) %>
                                &nbsp;</td>
                            <td><img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                                     width='<%= ( (float) ( (IPStatForm)session.getAttribute("ipStatForm")).getSubscribers(j)  )/ (float) ((IPStatForm)session.getAttribute("ipStatForm")).getBiggest()  * 250 %>'
                                     height="10">
                            <td>
                        </tr>
                        <% j++;
                        } %>

                        <tr>
                            <td colspan="3">&nbsp;&nbsp;</td>
                        </tr>

                        <tr>
                            <td><bean:message key="statistic.Other"/>:&nbsp;&nbsp;</td>
                            <td align="right"><%= ((IPStatForm) session.getAttribute("ipStatForm")).getRest() %>
                                &nbsp;</td>
                            <td>&nbsp;</td>
                        </tr>

                        <tr>
                            <td colspan="3">
                                <hr>
                            </td>
                        </tr>

                        <tr>
                            <td><b><bean:message key="statistic.Total"/>:</b>&nbsp;&nbsp;</td>
                            <td align="right"><b><%= ((IPStatForm) session.getAttribute("ipStatForm")).getTotal() %>
                            </b>&nbsp;</td>
                            <td>&nbsp;</td>
                        </tr>
                    </table>
                </td>
            </tr>
            <% } else { %>

            <tr>
                <td colspan="3">
                    <b><bean:message key="recipient.NoSubscribersForSelection"/></b>
                </td>
                </td>

                        <% } %>
        </table>
    </div>
</html:form>
