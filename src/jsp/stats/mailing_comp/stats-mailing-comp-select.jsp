<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'mailings';
    var columnindex = 0;
    var dragging = false;
    minWidthLast = 150;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<html:form action="/mailing_compare" >
    <html:hidden property="action"/>

    <div class="content_element_container targetgroups_select_container">

        <bean:message key="target.Target"/>:
        <html:select property="targetID" size="1">
            <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
            <c:forEach var="targetGroup" items="${targetGroups}">
                <html:option value="${targetGroup.id}">
                    ${targetGroup.targetName}
                </html:option>
            </c:forEach>
        </html:select>
        <br>
        <br>
    </div>

    <table id="mailings" border="0" cellspacing="0" cellpadding="0" class="list_table">
        <tr>
            <th class="comparasion_select_name"><bean:message key="Mailing"/></th>
            <th class="comparasion_select_desc"><bean:message key="default.description"/></th>
            <th class="comparasion_select_comp">
                <div align="right"><bean:message key="statistic.compare"/></div>
            </th>
        </tr>

        <c:set var="index" value="0" scope="request"/>

        <c:forEach var="mailing" items="${mailings}">

            <c:set var="trStyle" value="even" scope="request"/>
            <c:if test="${(index mod 2) == 0}">
                <c:set var="trStyle" value="odd" scope="request"/>
            </c:if>
            <c:set var="index" value="${index + 1}" scope="request"/>

            <tr class="trStyle">
                <td class="comparasion_select_name">
                    <span class="ie7hack">
                        <html:link page="/mailing_stat.do?action=${ACTION_MAILINGSTAT}&mailingID=${mailing.id}">${mailing.shortname}</html:link>
                    &nbsp;&nbsp;
                    </span>
                </td>
                <td class="comparasion_select_desc">
                    <span class="ie7hack">
                        <html:link page="/mailing_stat.do?action=${ACTION_MAILINGSTAT}&mailingID=${mailing.id}">${mailing.description}</html:link>
                        &nbsp;&nbsp;
                    </span>
                </td>
                <td class="comparasion_select_comp">
                    <span>
                        <input type="checkbox" name='MailCompID_${mailing.id}'>
                    </span>
                </td>
            </tr>
        </c:forEach>

    </table>
    <br>

    <div class="button_container">
        <div class="action_button">
            <a href="#"
               onclick="document.compareMailingForm.submit();return false;">
                <span><bean:message key="statistic.compare"/></span>
            </a>
        </div>
    </div>

</html:form>

<script type="text/javascript">
    table = document.getElementById('mailings');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#mailings tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>
