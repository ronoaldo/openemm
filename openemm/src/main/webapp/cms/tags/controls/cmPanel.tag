<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/cms/taglibs.jsp" %>

<%@ attribute name="cmId" %>
<%@ attribute name="cmContent" %>
<%@ attribute name="phName" %>
<%@ attribute name="phOrder" %>
<%@ attribute name="targetId" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<tr id="tr.${phName}.${phOrder}">
    <td>
        <table id="cm.${cmId}" cellpadding="2" width="450px" class="cm-panel">
            <tr>
                <td colspan="3">
                    <div style="background-color:#fff; width:100%;">
                        ${cmContent}
                    </div>
                </td>
                <td height="100%">
                    <table cellpadding="0" cellspacing="0" height="100%">
                        <tr>
                            <td valign="bottom" align="right">
                                <img src="${emmLayoutBase.imagesURL}/revise.gif"
                                    alt="edit CM" onclick="editCM(${cmId});" border="0" style="cursor:pointer"/>
                            </td>
                            <td width="3px">&nbsp;</td>
                            <td valign="bottom" align="right">
                                <input type="image"
                                    src="${emmLayoutBase.imagesURL}/cms_remove_cm.png"
                                    name="removeCM_${cmId}" value="${cmId}"/>
                            </td>
                        </tr>
                        <tr>
                            <td valign="middle" height="100%" colspan="3">
                                <table cellpadding="0" cellspacing="0">
                                   <tr>
                                       <td>
                                            <img src="${emmLayoutBase.imagesURL}/cms_arrange_up.gif"
                                            alt="<bean:message key="toPrevPlaceholder" bundle="cmsbundle"/>"
                                            border="0"
                                            onclick="toPrevPlaceholder(${cmId});"
                                            style="cursor:pointer">
                                       </td>
                                   </tr>
                                   <tr>
                                        <td height="4px"></td>
                                    </tr>
                                    <tr>
                                       <td>
                                            <img src="${emmLayoutBase.imagesURL}/cms_arrange_down.gif"
                                                alt="<bean:message key="toNextPlaceholder" bundle="cmsbundle"/>"
                                                border="0"
                                                onclick="toNextPlaceholder(${cmId});"
                                                style="cursor:pointer">
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td width="100%" class="simple-text">
                    <bean:message key="TargetGroup" bundle="cmsbundle"/>:&nbsp;
                    <c:set var="targetGroupDeleted" value="0" scope="page" />
                    <select name="cm_target.${cmId}" size="1" style="height:20px">
                        <option value="0" class="simple-text">---</option>
                        <c:forEach var="targetGroup" items="${mailingContentForm.targetGroups}" >
                            <c:set var="curTargetGroupId" value="${targetGroup.key}"/>
                            
                            <c:if test="${(not targetGroup.value.deleted) or (targetGroup.key == targetId)}">
	                            <logic:equal name="curTargetGroupId" value="${targetId}">
	                                <option value="${targetGroup.key}" selected="1" class="simple-text">
	                                	${targetGroup.value.shortname}
	                                	<c:if test="${targetGroup.value.deleted}">
	                                	  (<bean:message key="Deleted" />)
	                                	  <c:set var="targetGroupDeleted" value="1" scope="page" />
	                                	</c:if>
	                                </option>
	                            </logic:equal>
	                            <logic:notEqual name="curTargetGroupId" value="${targetId}">
	                                <option value="${targetGroup.key}" class="simple-text">
	                                	${targetGroup.value.shortname}
	                                	<c:if test="${targetGroup.value.deleted}">
	                                	  (<bean:message key="Deleted" />)
	                                	  <c:set var="targetGroupDeleted" value="1" scope="page" />
	                                	</c:if>
	                                </option>
	                            </logic:notEqual>
	                        </c:if>
                        </c:forEach>
                    </select>
                    
                    <c:if test="${targetGroupDeleted != 0}">
                      <span style="color: #ff0000">(<bean:message key="Deleted" />)</span>
                    </c:if>
                </td>
                <%-- was used if several CMs can be placed inside one placeholder --%>
                <%--<td>
                    <img src="${emmLayoutBase.imagesURL}/cms_prev_placeholder.gif"
                         alt="<bean:message key="toPrevPlaceholder" bundle="cmsbundle"/>" border="0"
                         onclick="toPrevPlaceholder(${cmId});"
                         style="cursor:pointer">
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/cms_next_placeholder.gif"
                         alt="<bean:message key="toNextPlaceholder" bundle="cmsbundle"/>" border="0"
                         onclick="toNextPlaceholder(${cmId});"
                         style="cursor:pointer">
                </td>--%>
            </tr>
        </table>
    </td>
</tr>