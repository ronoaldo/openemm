<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<%@ attribute name="cmId" %>
<%@ attribute name="cmContent" %>
<%@ attribute name="phName" %>
<%@ attribute name="phOrder" %>
<%@ attribute name="targetId" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
    function mouseOverDo${cmId}() {
        var popup = document.getElementById("popup.${cmId}");
        var cmContent = document.getElementById("cm.content.${cmId}");
        adjusPosition(cmContent, popup);
        popup.style.visibility = "visible";
    }

    function mouseOutDo${cmId}() {
        var popup = document.getElementById("popup.${cmId}");
        popup.style.visibility = "hidden";
    }

    function adjusPosition(cmContent, popup) {
        // get absolute position of cmContent
        var posX = cmContent.offsetLeft;
        var posY = cmContent.offsetTop;
        var tmp = cmContent;
        while (tmp.offsetParent) {
            posX = posX + tmp.offsetParent.offsetLeft;
            posY = posY + tmp.offsetParent.offsetTop;
            if (tmp == document.getElementsByTagName('body')[0]) {
                break;
            }
            else {
                tmp = tmp.offsetParent;
            }
        }
        popup.style.left = posX;
        popup.style.top = posY;
        var controlsTable = document.getElementById('popup.controls.${cmId}');
        if (cmContent.offsetWidth > controlsTable.offsetWidth) {
            popup.style.width = cmContent.offsetWidth + "px";    
        }
        else {
            popup.style.width = controlsTable.offsetWidth + "px";
        }
    }

    function onEditClick(cmId) {
        document.getElementById("cmToEdit").value = cmId;
        var innerForm = document.getElementsByTagName("form")[0];
        innerForm.target = "_top";
        innerForm.submit();
    }

     function onNewClick(cmId) {
        var phName = document.getElementById("cm." + cmId + ".ph_name").value;
        document.getElementById("phForNewCM").value = phName;
        var innerForm = document.getElementsByTagName("form")[0];
        innerForm.target = "_top";
        innerForm.submit();
    }

</script>



<tr id="tr.${phName}.${phOrder}">
    <td>
        <table id="cm.${cmId}" cellpadding="0" cellspacing="0" class="cm-panel" onmouseover="mouseOverDo${cmId}();" onmouseout="mouseOutDo${cmId}();">
            <tr>
                <td colspan="3">
                    <div style="background-color:#fff; width:100%;" id="cm.content.${cmId}">
                        ${cmContent}
                    </div>

                    <div style="position:absolute; visibility:hidden; background-color:#eeeeee; border: 1px solid #777;" id="popup.${cmId}">
                        <table cellpadding="3" cellspacing="0" width="100%" id="popup.controls.${cmId}">
                            <tr>
                                
                                <td style="font-size:12px; font-family:Tahoma, Arial, sans-serif;" id="cmPanel.phName.${cmId}">${phName}</td>

                                <td>
                                    <img src="${emmLayoutBase.imagesURL}/cms_arrange_up.gif"
                                         alt="<bean:message key="toPrevPlaceholder" bundle="cmsbundle"/>"
                                         border="0"
                                         onclick="toPrevPlaceholder(${cmId});"
                                         style="cursor:pointer">
                                </td>

                                <td>
                                    <img src="${emmLayoutBase.imagesURL}/cms_arrange_down.gif"
                                         alt="<bean:message key="toNextPlaceholder" bundle="cmsbundle"/>"
                                         border="0"
                                         onclick="toNextPlaceholder(${cmId});"
                                         style="cursor:pointer">
                                </td>

                                <td width="100%" class="simple-text">
                                    <select name="cm_target.${cmId}" size="1" style="height:20px">
                                        <option value="0" class="simple-text">---</option>
                                        <c:forEach var="targetGroup" items="${mailingContentForm.targetGroups}">
                                            <c:set var="curTargetGroupId" value="${targetGroup.key}"/>
                                            <logic:equal name="curTargetGroupId" value="${targetId}">
                                                <option value="${targetGroup.key}" selected="1"
                                                        class="simple-text">${targetGroup.value.shortname}</option>
                                            </logic:equal>
                                            <logic:notEqual name="curTargetGroupId" value="${targetId}">
                                                <option value="${targetGroup.key}"
                                                        class="simple-text">${targetGroup.value.shortname}</option>
                                            </logic:notEqual>
                                        </c:forEach>
                                    </select>
                                </td>

                                <td width="100%"></td>

                                <td valign="middle" align="right">
                                   <img src="${emmLayoutBase.imagesURL}/create_cm.gif" onclick="onNewClick(${cmId});" alt="edit"  style="cursor:pointer;"/>
                                </td>

                                <td valign="middle" align="right">
                                   <img src="${emmLayoutBase.imagesURL}/revise.gif" onclick="onEditClick(${cmId});" alt="edit" style="cursor:pointer;"/>
                                </td>

                                <td valign="middle" align="right">
                                    <input type="image"
                                           src="${emmLayoutBase.imagesURL}/cms_remove_cm.png"
                                           name="removeCM_${cmId}" value="${cmId}"/>
                                </td>
                            </tr>
                        </table>
                    </div>

                </td>
            </tr>
        </table>
    </td>
</tr>