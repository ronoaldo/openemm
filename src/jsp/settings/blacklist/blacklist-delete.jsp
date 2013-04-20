<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.BlacklistAction, java.net.URLEncoder" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="logic" uri="http://struts.apache.org/tags-logic" %>

<div class="new_mailing_start_description"><bean:message
        key="Recipient"/>:&nbsp;<%= request.getParameter("delete") %> <br>
    <bean:message key="recipient.blacklist.delete"/>
</div>

<html:form action="/blacklist.do">
	<html:hidden property="action" value="<%= Integer.toString(BlacklistAction.ACTION_DELETE) %>" />
	<html:hidden property="delete" value="<%= URLEncoder.encode((String)request.getParameter(\"delete\"), \"UTF-8\") %>" />
	
	<logic:notEmpty name="blacklistForm" property="blacklistedMailinglists">
		<div class="new_mailing_start_description">
			<bean:message key="blacklist.mailinglists"/>:<br/>
			<logic:iterate collection="${blacklistForm.blacklistedMailinglists}" id="element" indexId="index">
				<html:checkbox property="checkedBlacklistedMailinglists[${index}]" value="${element.id}">${element.shortname}</html:checkbox><br/>
			</logic:iterate>
		</div>
	</logic:notEmpty>
	
	<div class="remove_element_button_container">
	    <div class="greybox_small_top"></div>
	    <div class="greybox_small_content">
	        <div class="new_mailing_step1_left_column">
	            <div class="big_button"><a href="javascript:document.blacklistForm.submit()"><span><bean:message
	                    key="button.Delete"/></span></a></div>
	        </div>
	        <div class="new_mailing_step1_right_column">
	            <div class="big_button"><a
	                    href="<html:rewrite page='<%= new String(\"/blacklist.do?action=\"+BlacklistAction.ACTION_LIST) %>'/>"><span><bean:message
	                    key="button.Cancel"/></span></a></div>
	        </div>
	    </div>
	    <div class="greybox_small_bottom"></div>
	</div>
</html:form>