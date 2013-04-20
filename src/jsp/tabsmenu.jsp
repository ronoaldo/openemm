<%@ page language="java" import="org.agnitas.util.*"  contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ul class="tabs">
        <agn:ShowNavigation navigation='${agnNavigationKey}'
                            highlightKey='${agnHighlightKey}'
                            plugin='${agnPluginId}'
                            extension='${agnExtensionId}'>
		    <agn:ShowByPermission token="${_navigation_token}">
		    	<%
		    		String cssStyleClass = (_navigation_isHighlightKey.booleanValue()) ? "tab_right tab_active" : "tab_right";
		    	%>
	                <li class="<%= cssStyleClass %>">
                	        	<% String nav_link = _navigation_href;
                    	           if(request.getAttribute("agnNavHrefAppend")!=null) {
                        	       		nav_link = nav_link + request.getAttribute("agnNavHrefAppend");
                            	   }
                          		%>
	                          	<% if( _navigation_isHighlightKey.booleanValue() ){ %>
    	                        	<html:link styleClass="tab_left" page="<%= nav_link %>">
    	                        		<c:if test="${empty _navigation_plugin}">
        	                        		<bean:message key="${_navigation_navMsg}"/>
        	                        	</c:if>
    	                        		<c:if test="${not empty _navigation_plugin}">
	    	                        		<agn:message key="${_navigation_navMsg}" plugin="${_navigation_plugin}"/>
	    	                        	</c:if>
            	                    </html:link>
                	            <% } else { %>
                    	            <html:link styleClass="tab_left" page="<%= nav_link %>">
    	                        		<c:if test="${empty _navigation_plugin}">
        	                        		<bean:message key="${_navigation_navMsg}"/>
        	                        	</c:if>
    	                        		<c:if test="${not empty _navigation_plugin}">
	    	                        		<agn:message key="${_navigation_navMsg}" plugin="${_navigation_plugin}"/>
	    	                        	</c:if>
                            	    </html:link>
	                            <% } %>
  	 	                    </li>
        	</agn:ShowByPermission>
        </agn:ShowNavigation>
</ul>