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
 
<%@ page language="java" import="org.agnitas.util.*,org.agnitas.web.*,org.agnitas.target.*,org.agnitas.target.impl.*,org.agnitas.beans.*,java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="MAILTYPE_TEXT" value="<%=Recipient.MAILTYPE_TEXT%>" scope="page"/>
<c:set var="MAILTYPE_HTML" value="<%=Recipient.MAILTYPE_HTML%>" scope="page"/>
<c:set var="MAILTYPE_HTML_OFFLINE" value="<%=Recipient.MAILTYPE_HTML_OFFLINE%>" scope="page"/>
<c:set var="MAILTYPE_MHTML" value="<%=Recipient.MAILTYPE_MHTML%>" scope="page"/>

<c:set var="COLUMN_TYPE_DATE" value="<%=TargetForm.COLUMN_TYPE_DATE%>" scope="page" />
<c:set var="COLUMN_TYPE_NUMERIC" value="<%=TargetForm.COLUMN_TYPE_NUMERIC%>" scope="page" />
<c:set var="COLUMN_TYPE_STRING" value="<%=TargetForm.COLUMN_TYPE_STRING%>" scope="page" />

<c:set var="OPERATOR_IS" value="<%=TargetNode.OPERATOR_IS.getOperatorCode()%>" scope="page" />
<c:set var="OPERATOR_MOD" value="<%=TargetNode.OPERATOR_MOD.getOperatorCode()%>" scope="page" />

<c:set var="ACTION_SAVE" value="<%=TargetAction.ACTION_SAVE%>" scope="page" />

<c:if test="${empty TARGET_LOCKED}">
	<c:set var="TARGET_LOCKED" value="false" scope="page" />
</c:if>

<input type="hidden" id="targetNodeToRemove" name="targetNodeToRemove" value="-1"/>

<logic:iterate id="currentColumnAndType" name="${FORM_NAME}" property="allColumnsAndTypes" indexId="index">
	<c:if test="${not empty currentColumnAndType}">
		<tr>
			<!-- chain operator -->
			<td>
				<c:choose>
					<c:when test="${index != 0}">
						<html:select name="${FORM_NAME}" property="chainOperator[${index}]" size="1" disabled="${TARGET_LOCKED}">
							<html:option value="<%=Integer
										.toString(TargetNode.CHAIN_OPERATOR_AND)%>" key="default.and" />
							<html:option value="<%=Integer
										.toString(TargetNode.CHAIN_OPERATOR_OR)%>" key="default.or" />
						</html:select>
					</c:when>
					<c:otherwise>
						<html:hidden name="${FORM_NAME}" property="chainOperator[${index}]" value="<%= Integer.toString(TargetNode.CHAIN_OPERATOR_NONE) %>" />
                        <div class="advanced_search_filter_left_space">&nbsp;</div>
					</c:otherwise>
				</c:choose>
			</td>

			<!-- opening parenthesis -->
			<td>
				<html:select name="${FORM_NAME}" property="parenthesisOpened[${index}]" size="1" disabled="${TARGET_LOCKED}" >
					<html:option value="0">&nbsp</html:option>
					<html:option value="1">(</html:option>
				</html:select>
			</td>

			<!-- DB column -->
			<td>
				<html:select property="columnAndType[${index}]" styleClass="advanced_search_filter_select2" disabled="${TARGET_LOCKED}">
                    <%String selectedColumnName = null;%>
					<agn:ShowColumnInfo id="colsel">
						<%
	          				String columnName = (String) pageContext.getAttribute("_colsel_column_name");
							String columnShortName = (String) pageContext.getAttribute("_colsel_shortname");
          					String columnType = (String) pageContext.getAttribute("_colsel_data_type");
			          		String columnNameToSelect = currentColumnAndType != null ? ((String) currentColumnAndType).substring(0, ((String) currentColumnAndType).indexOf("#")) : "";
							String selectedAttribute = "";
							if (columnName != null && columnName.equalsIgnoreCase(columnNameToSelect)) {
								selectedAttribute = " selected=\"selected\"";
							}
                            if(!columnNameToSelect.equals("")){
                                selectedColumnName = columnNameToSelect;
                            }
							String optionValue = columnName + "#" + columnType;
			            %>

						<option value="<%= optionValue %>"<%= selectedAttribute %>><%= columnShortName %></option>
					</agn:ShowColumnInfo>
                    <%
                        String dateColumnName = AgnUtils.getSQLCurrentTimestampName();
                        if (dateColumnName.equalsIgnoreCase(selectedColumnName)) { %>
                            <option value="<%= dateColumnName + "#DATE"%>" selected="selected"><bean:message key="default.sysdate"/></option>
                    <%  } else {%>
                            <option value="<%= dateColumnName + "#DATE"%>"><bean:message key="default.sysdate"/></option>
                    <%}%>
				</html:select>
			</td>

			<!-- operator -->
			<td>
				<html:select name="${FORM_NAME}" property="primaryOperator[${index}]" size="1" styleClass="advanced_search_filter_select3" disabled="${TARGET_LOCKED}">
					<logic:iterate name="${FORM_NAME}" property="validTargetOperators[${index}]" id="operator">
						<c:if test="${not empty operator}">
							<html:option value="${operator.operatorCode}">${operator.operatorSymbol}</html:option>
						</c:if>
					</logic:iterate>
				</html:select>
			</td>

			<!-- value -->
			<td>
				<bean:define id="columnType" name="${FORM_NAME}" property="columnType[${index}]" toScope="page" /> 
				<bean:define id="primaryOperator" name="${FORM_NAME}" property="primaryOperator[${index}]" toScope="page" /> 
				<bean:define id="columnName" name="${FORM_NAME}" property="columnName[${index}]" toScope="page" /> 

				<c:choose>
					<c:when test="${columnType == COLUMN_TYPE_DATE && primaryOperator != OPERATOR_IS}">
						<nobr> 
						<html:text name="${FORM_NAME}" property="primaryValue[${index}]" styleClass="advanced_search_filter_select6" disabled="${TARGET_LOCKED}" />
						<html:select name="${FORM_NAME}" property="dateFormat[${index}]" styleClass="advanced_search_filter_select7" size="1" disabled="${TARGET_LOCKED}">
							<html:option value="yyyymmdd" key="default.date.format.YYYYMMDD" />
							<html:option value="mmdd" key="default.date.format.MMDD" />
							<html:option value="yyyymm" key="default.date.format.YYYYMM" />
							<html:option value="dd" key="default.date.format.DD" />
							<html:option value="mm" key="default.date.format.MM" />
							<html:option value="yyyy" key="default.date.format.YYYY" />
						</html:select> 
						</nobr>
					</c:when>
					<c:when test="${columnType == COLUMN_TYPE_NUMERIC}">
						<c:choose>
							<c:when test="${primaryOperator == OPERATOR_IS}">
								<html:select name="${FORM_NAME}" property="primaryValue[${index}]" size="1" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}">
									<html:option value="null">null</html:option>
									<html:option value="not null">not null</html:option>
								</html:select>
							</c:when>
							<c:when test="${primaryOperator != OPERATOR_MOD}">
								<c:choose>
									<c:when test="${fn:toLowerCase(columnName) == 'mailtype'}">
										<html:select name="${FORM_NAME}" property="primaryValue[${index}]" size="1" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}">
											<html:option value="${MAILTYPE_TEXT}" key="recipient.mailingtype.text" />
											<html:option value="${MAILTYPE_HTML}" key="recipient.mailingtype.html" />
											<html:option value="${MAILTYPE_HTML_OFFLINE}" key="recipient.mailingtype.htmloffline" />
                                            <agn:ShowByPermission token="mailing.content.mhtml">
                                                <html:option value="${MAILTYPE_MHTML}" key="recipient.mailingtype.mhtml" />
                                            </agn:ShowByPermission>
										</html:select>
									</c:when>
									<c:when test="${fn:toLowerCase(columnName) == 'gender'}">
										<html:select name="${FORM_NAME}" property="primaryValue[${index}]" size="1" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}">
											<html:option value="0" key="recipient.gender.0.short" />
											<html:option value="1" key="recipient.gender.1.short" />
											<agn:ShowByPermission token="recipient.gender.extended">
												<html:option value="3" key="recipient.gender.3.short" />
												<html:option value="4" key="recipient.gender.4.short" />
												<html:option value="5" key="recipient.gender.5.short" />
											</agn:ShowByPermission>
											<html:option value="2" key="recipient.gender.2.short" />
										</html:select>
									</c:when>
									<c:otherwise>
										<html:text name="${FORM_NAME}" property="primaryValue[${index}]" size="60" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}"/>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<html:text name="${FORM_NAME}" property="primaryValue[${index}]" styleClass="advanced_search_filter_mod_textfield" disabled="${TARGET_LOCKED}" />
								<html:select name="${FORM_NAME}" property="secondaryOperator[${index}]" size="1" styleClass="advanced_search_filter_mod_select" disabled="${TARGET_LOCKED}">
									<logic:iterate collection="<%=TargetNode
													.getAllowedSecondaryOperatorsForPrimaryOperator(TargetNode.OPERATOR_MOD)%>" id="operator">
										<html:option value="${operator.operatorCode}">${operator.operatorSymbol}</html:option>
									</logic:iterate>
								</html:select>
								<html:text name="${FORM_NAME}" property="secondaryValue[${index}]" styleClass="advanced_search_filter_mod_textfield" disabled="${TARGET_LOCKED}"/>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${columnType == COLUMN_TYPE_STRING && primaryOperator != OPERATOR_IS}">
						<html:text name="${FORM_NAME}" property="primaryValue[${index}]" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}" />
					</c:when>
					<c:otherwise>
						<c:if test="${primaryOperator == OPERATOR_IS}">
							<html:select name="${FORM_NAME}" property="primaryValue[${index}]" size="1" styleClass="advanced_search_filter_select4" disabled="${TARGET_LOCKED}">
								<html:option value="null">null</html:option>
								<html:option value="not null">not null</html:option>
							</html:select>
						</c:if>
					</c:otherwise>
				</c:choose>
			</td>

			<!-- closing parenthesis -->
			<td>
				<html:select name="${FORM_NAME}" property="parenthesisClosed[${index}]" size="1" disabled="${TARGET_LOCKED}">
					<html:option value="0">&nbsp</html:option>
					<html:option value="1">)</html:option>
				</html:select>
			</td>
			<agn:ShowByPermission token="targets.change">
	   	   		<c:if test="${not TARGET_LOCKED}">
				<!-- add / remove button -->
					<td>
        	     	   <div class="advanced_search_delete">
		    	     		<html:link styleClass="mailing_delete" href="#" onclick="document.getElementById('targetNodeToRemove').value='${index}'; document.${FORM_NAME}.submit(); return false;" />
            		    </div>
					</td>
				</c:if>
			</agn:ShowByPermission>
		</tr>
	</c:if>
</logic:iterate>
