<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="top_navigation_container">
    <ul class="top_navigation_level1">
        <agn:ShowNavigation navigation="sidemenu"
                            highlightKey='<%= (String) request.getAttribute("sidemenu_active") %>'>
            <agn:ShowByPermission token="<%= _navigation_token %>">
                <%
                    String cssClassPostfix = _navigation_isHighlightKey.booleanValue() ? "_active" : "_no";
                    String styleClass = "top_navigation_level1" + cssClassPostfix;
                    if (_navigation_index.intValue() == 1) {
                        styleClass += "_round";
                    }
                %>
                <li>
                    <html:link page="<%= _navigation_href %>" styleClass="<%= styleClass %>">
                    	<c:if test="${empty _navigation_plugin}">
		                   	<bean:message key="<%= _navigation_navMsg %>"/>
		                </c:if>
		                <c:if test="${not empty _navigation_plugin}">
			                <agn:message key="${_navigation_navMsg}" plugin="${_navigation_plugin}"/>
		                </c:if>
                    </html:link>
                </li>
            </agn:ShowByPermission>
        </agn:ShowNavigation>
    </ul>
</div>
<agn:ShowNavigation navigation="sidemenu"
                    highlightKey='<%= (String) request.getAttribute("sidemenu_active") %>'>
    <agn:ShowByPermission token="<%= _navigation_token %>">

        <% if (_navigation_isHighlightKey.booleanValue()) { %>
        <div><br><br></div>
        <div class="float_left" style="width:1070px;">
            <ul class="top_navigation_level2">
                <agn:ShowNavigation navigation='<%= _navigation_navMsg+"Sub" %>'
                                    highlightKey="<%= (String) request.getAttribute(\"sidemenu_sub_active\") %>"
                                    prefix="_sub">
                    <agn:ShowByPermission token="<%= _sub_navigation_token %>">
                        <%
                            String subCssClassPostfix = _sub_navigation_isHighlightKey.booleanValue() ? "_active" : "_no";
                        %>
                        <li>
                            <%
                                if (_sub_navigation_index.intValue() != 1) {

                            %>
                            <label>|</label>
                            <% } %>
                            <html:link page="<%= _sub_navigation_href %>" styleClass="<%= \"top_navigation_level2\" + subCssClassPostfix %>">
                            	<c:if test="${empty _sub_navigation_plugin}">
	                            	<bean:message key="<%= _sub_navigation_navMsg %>"/>
	                            </c:if>
	                            <c:if test="${not empty _sub_navigation_plugin}">
	                            	<agn:message key="${_sub_navigation_navMsg}" plugin="${_sub_navigation_plugin}"/>
	                            </c:if>
                            </html:link>
                        </li>
                    </agn:ShowByPermission>
                </agn:ShowNavigation>
            </ul>
        </div>
        <% } %>
    </agn:ShowByPermission>
</agn:ShowNavigation>


