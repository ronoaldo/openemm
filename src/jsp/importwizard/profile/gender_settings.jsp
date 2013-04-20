<%--
  The contents of this file are subject to the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://www.openemm.org/cpal1.html. The License is based on the Mozilla
  Public License Version 1.1 but Sections 14 and 15 have been added to cover
  use of software over a computer network and provide for limited attribution
  for the Original Developer. In addition, Exhibit A has been modified to be
  consistent with Exhibit B.
  Software distributed under the License is distributed on an "AS IS" basis,
  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  the specific language governing rights and limitations under the License.

  The Original Code is OpenEMM.
  The Original Developer is the Initial Developer.
  The Initial Developer of the Original Code is AGNITAS AG. All portions of
  the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
  Reserved.

  Contributor(s): AGNITAS AG.
  --%>
<%@ page language="java"
         import="org.agnitas.web.ImportProfileAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<c:set var="index" value="0" scope="request"/>

<table class="send_status_table">
    <tr class="even">
       <th class="gender_element"><bean:message key="import.profile.gender.string"/></th>
       <th class="gender_element"><bean:message key="import.profile.gender.int"/></th>
       <th class="gender_table_delete_element">&nbsp;</th>
    </tr>
<c:forEach var="entry" items="${importProfileForm.profile.genderMappingJoined}">
    <c:set var="trStyle" value="even" scope="request"/>
    <c:if test="${(index mod 2) == 0}">
        <c:set var="trStyle" value="odd" scope="request"/>
    </c:if>
    <c:set var="index" value="${index + 1}" scope="request"/>
    <tr class="${trStyle}">
        <td class="gender_element">
            <span class="ie7hack">
                ${entry.key}
            </span>
        </td>
        <td class="gender_element">
           <span class="ie7hack">
               ${entry.value}
           </span>
        </td>
        <td class="gender_table_delete_element">
           <span class="ie7hack">
               <input type="hidden" name="removeGender_${entry.value}" value=""/>
               <a class="mailing_delete" href="#" title="<bean:message key='button.Delete'/>"
               onclick="document.importProfileForm.removeGender_${entry.value}.value='${entry.key}'; document.importProfileForm.submit();return false;"></a>
            </span>
        </td>
    </tr>
</c:forEach>

</table>

<c:if test="${importProfileForm.genderQuantity > 0}">
    <br>

    <div>
        <strong>
            <bean:message key="import.profile.add.gender.mapping"/>
        </strong>
    </div>

    <div class="gender_new_panel">
        <div style="float: left;">
            <html:text property="addedGender" style="width: 140px" maxlength="100"/>
            <html:select property="addedGenderInt" size="1">
                <c:forEach var="gender" items="${importProfileForm.genderValues}">
                    <html:option value="${gender}">
                        ${gender}
                    </html:option>
                </c:forEach>
            </html:select>
            <input type="hidden" id="addGender" name="addGender" value=""/>
        </div>

            <div class="action_button add_button">
                <a href="#"
                   onclick="document.importProfileForm.addGender.value='add'; document.importProfileForm.submit();return false;">
                    <span><bean:message key="button.Add"/></span>
                </a>
            </div>
    </div>
</c:if>