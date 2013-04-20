<%-- checked --%>
<%@ page language="java" import="org.agnitas.util.AgnUtils" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<html:form action="/recipient_stats" method="post">
    <html:hidden property="action"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column" style="float: none;">

                <div class="grey_box_form_item stat_recipient_form_item">
                    <label><bean:message key="Mailinglist"/>:</label>
                    <html:select property="mailingListID" size="1">
                        <html:option value="0"><bean:message key="statistic.All_Mailinglists"/></html:option>
                        <c:forEach var="mailinglist" items="${mailinglists}">
                            <html:option value="${mailinglist.id}">
                                ${mailinglist.shortname}
                            </html:option>
                        </c:forEach>
                    </html:select>
                </div>

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
            </div>

            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <a href="#" onclick="document.recipientStatForm.submit();">
                        <span><bean:message key="button.Submit"/></span>
                    </a>
                </div>
            </div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

</html:form>