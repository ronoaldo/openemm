<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.forms.ContentModuleForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<% ContentModuleForm aForm = (ContentModuleForm) session.getAttribute("contentModuleForm"); %>

<html:form action="/cms_contentmodule" focus="cmtId">
    <input type="hidden" name="action" id="action">
    <html:hidden property="contentModuleId" value="0"/>

    <jsp:include page="cms-contentmodule-new-pure.jsp"/>

</html:form>