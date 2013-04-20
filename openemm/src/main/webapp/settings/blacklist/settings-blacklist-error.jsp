<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

	<table>
	<html:form action="/blacklist">
		 <html:hidden property="action" value="${ACTION_LIST}" />
    <tr>
        <td><html:image src="button?msg=button.OK" border="0" /></td>
    </tr>
   
	</html:form> 
	</table>
