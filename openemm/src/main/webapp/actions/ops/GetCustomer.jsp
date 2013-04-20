<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%><%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int index=((Integer)request.getAttribute("opIndex")).intValue();
 String loadAlways=new String("actions["+index+"].loadAlways");%>

<html:hidden property='<%= "__STRUTS_CHECKBOX_"+loadAlways %>' value="false"/>

<div class="update_customer_action_box"><label></label>
    <html:checkbox property="<%= loadAlways %>">&nbsp;<bean:message key="action.getcustomer.loadalways"/></html:checkbox>
</div>
<agn:ShowByPermission token="actions.change">
<div class="action_button no_margin_right no_margin_bottom">
    <a href="<html:rewrite page='<%= new String("/action.do?action=" + EmmActionAction.ACTION_SAVE + "&deleteModule=" + index) %>'/>"><span><bean:message key="button.Delete"/></span></a>
</div>
</agn:ShowByPermission>