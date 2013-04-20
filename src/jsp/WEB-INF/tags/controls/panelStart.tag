<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<%@ attribute name="title" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td><img
                src="${emmLayoutBase.imagesURL}/tagw_left.gif"
                border="0"></td>
        <td class="tag" width="100%"><b><bean:message key="${title}"/></b></td>
        <td><img
                src="${emmLayoutBase.imagesURL}/tagw_right.gif"
                border="0"></td>
    </tr>
    <tr>
        <td bgcolor="#EBEBEB"><img
                src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                width="1" height="1" border="0"></td>
        <td height="100%" valign="top" bgcolor="#EBEBEB">