<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.dao.*, org.agnitas.target.impl.*, org.agnitas.beans.*, java.util.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="grey_box_container">
    <div class="grey_box_top"></div>
    <div class="grey_box_content">
        <div class="plugin_detail_container">
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.id" />:</label>
                <label class="plugin_detail_value">
                	${pluginDetail.pluginId}
		            <c:if test="${pluginDetail.systemPlugin}">
			        	<i>(<bean:message key="pluginmanager.plugin.systemplugin" />)</i>
		            </c:if>
                </label>
            </div>
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.name" />:</label>
                <label class="plugin_detail_value">${pluginDetail.pluginName}</label>
            </div>
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.description" />:</label>
                <label class="plugin_detail_value">${pluginDetail.description}</label>
            </div>
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.version" />:</label>
                <label class="plugin_detail_value">${pluginDetail.version}</label>
            </div>
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.vendor" />:</label>
                <label class="plugin_detail_value">${pluginDetail.vendor}</label>
            </div>
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.status" />:</label>
                <c:if test="${pluginDetail.activated}">
                    <label class="plugin_detail_value"><bean:message key="pluginmanager.plugin.activated" /></label>
                    <div class="float_left">
                        <div class="action_button float_left"><html:link page="/pluginManager.do?action=deactivate&plugin=${pluginDetail.pluginId}"><span><bean:message key="pluginmanager.plugin.deactivate"/></span></html:link></div>
                    </div>
                </c:if>
                <c:if test="${not pluginDetail.activated}">
                    <label class="plugin_detail_value"><bean:message key="pluginmanager.plugin.deactivated" /></label>
                    <div class="float_left">
                        <div class="action_button float_left"><html:link page="/pluginManager.do?action=activate&plugin=${pluginDetail.pluginId}"><span><bean:message key="pluginmanager.plugin.activate"/></span></html:link></div>
                    </div>
                    <c:if test="${not pluginDetail.systemPlugin}">
	                    <div class="float_left">
	                        <div class="action_button float_left"><html:link page="/pluginManager.do?action=uninstall&plugin=${pluginDetail.pluginId}"><span><bean:message key="pluginmanager.plugin.uninstall"/></span></html:link></div>
	                    </div>
	                </c:if>
                </c:if>
            </div>
            <div class="plugin_detail_item">
                <label class="plugin_detail_label"><bean:message key="pluginmanager.plugin.depending_plugins"/>:</label>
                <c:if test="${fn:length(pluginDetail.dependingPluginIds) != 0}">
                    <c:forEach items="${pluginDetail.dependingPluginIds}" var="dependingPlugin">
                        <html:link styleClass="blue_link" page="/pluginManager.do?action=detail&plugin=${dependingPlugin}">${dependingPlugin}</html:link><br>
                    </c:forEach>
                </c:if>
                <c:if test="${fn:length(pluginDetail.dependingPluginIds) == 0}">
                    <label class="plugin_detail_value"><bean:message key="pluginmanager.plugin.no_depending_plugins" /></label>
                </c:if>
            </div>
        </div>
    </div>
    <div class="grey_box_bottom"></div>
</div>

<div class="button_container">
    <div class="action_button">
        <html:link page="/pluginManager.do?action=list">
            <span><bean:message key="button.Back"/></span>
        </html:link>
    </div>
</div>