<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table>
    <tr>
        <td><bean:message key="settings.Admin.numberofrows"/></td>
        <td>
            <html:select property="numberofRows">
                <% String[] sizes = {"20", "50", "100"};
                    for(int i = 0; i < sizes.length; i++) { %>
                <html:option value="<%= sizes[i] %>"><%= sizes[i] %>
                </html:option>
                <% } %>
            </html:select>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <html:image src="button?msg=button.Show" border="0"/>
        </td>
    </tr>
</table>