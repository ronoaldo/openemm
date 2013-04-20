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
 --%>
 
<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.target.impl.*, org.agnitas.beans.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<bean:define id="index" name="${FORM_NAME}" property="numTargetNodes" toScope="page" /> 

<c:set var="ACTION_SAVE" value="<%= TargetAction.ACTION_SAVE %>" scope="page" />

<c:if test="${empty TARGET_LOCKED}">
	<c:set var="TARGET_LOCKED" value="false" scope="page" />
</c:if>

<tr>
	<!-- chain operator -->
	<td>
		<c:choose>
			<c:when test="${index != 0}">
				<html:select property="chainOperatorNew" size="1" disabled="${TARGET_LOCKED}">
					<html:option value="<%= Integer.toString(TargetNode.CHAIN_OPERATOR_AND) %>" key="default.and" />
					<html:option value="<%= Integer.toString(TargetNode.CHAIN_OPERATOR_OR) %>" key="default.or" />
				</html:select>
			</c:when>
			<c:otherwise>
				<html:hidden property="chainOperatorNew" value="0"/>
                <div class="advanced_search_filter_left_space">&nbsp;</div>
			</c:otherwise>
		</c:choose>
	</td>

	<!-- opening parenthesis -->
	<td>
		<html:select property="parenthesisOpenedNew" size="1" disabled="${TARGET_LOCKED}">
			<html:option value="0">&nbsp</html:option>
			<html:option value="1">(</html:option>
		</html:select>
	</td>

	<!-- DB column -->
	<td>
		<html:select property="columnAndTypeNew" value="email#VARCHAR" size="1"  styleClass="advanced_search_filter_select2" disabled="${TARGET_LOCKED}">
			<agn:ShowColumnInfo id="colsel">
			    <c:set var="separatorSymbol" value="#" scope="page" />
				<c:set var="columnName" value='<%= ((String)pageContext.getAttribute("_colsel_column_name")).toUpperCase() %>' scope="page" />
				<c:set var="columnType" value='<%= pageContext.getAttribute("_colsel_data_type") %>' scope="page" />
				<html:option value="${columnName}${separatorSymbol}${columnType}"><%= pageContext.getAttribute("_colsel_shortname") %></html:option>
			</agn:ShowColumnInfo>

			<c:set var="columnName" value="<%= AgnUtils.getSQLCurrentTimestampName() %>" scope="page" />
			<html:option value="${columnName}${separatorSymbol}DATE" key="default.sysdate" />
		</html:select>
	</td>

	<!-- operator -->
	<td>
		<html:select property="primaryOperatorNew" size="1"  styleClass="advanced_search_filter_select3" disabled="${TARGET_LOCKED}">
			<logic:iterate collection="<%= TargetNode.ALL_OPERATORS %>" id="all_operator">
				<html:option value="${all_operator.operatorCode}">${all_operator.operatorSymbol}</html:option>
			</logic:iterate>
		</html:select>
	</td>

	<!-- value -->
	<td>
	    <html:text property="primaryValueNew" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}" />
	<%--
		<input type="text" name="primaryValueNew" value=""  class="advanced_search_filter_select4" disabled="${TARGET_LOCKED}">
		--%>
	</td>

	<!-- closing parenthesis -->
	<td>
		<html:select property="parenthesisClosedNew" size="1" disabled="${TARGET_LOCKED}">
			<html:option value="0">&nbsp</html:option>
			<html:option value="1">)</html:option>
		</html:select>
	</td>

	<!-- add / remove button -->
	<td>
        <input type="hidden" id="addTargetNode" name="addTargetNode" value=""/>
        
        <c:if test="${not TARGET_LOCKED}">
	        <html:link styleClass="advanced_search_add" href="#" onclick="document.getElementById('addTargetNode').value='true'; document.${FORM_NAME}.submit(); return false;"><bean:message key="button.Add"/></html:link>
    	</c:if>    
	</td>
</tr>
