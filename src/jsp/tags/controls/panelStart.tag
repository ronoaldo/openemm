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
<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/tags/taglibs.jsp" %>

<%@ attribute name="title" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

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
