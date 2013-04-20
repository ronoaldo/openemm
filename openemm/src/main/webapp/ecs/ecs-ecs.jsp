<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.ecs.web.forms.EcsMailingStatForm" %>
<%@ page import="org.agnitas.ecs.backend.service.EmbeddedClickStatService" %>
<%@ page import="org.agnitas.ecs.EcsGlobals" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
    function checkIfmeWidth(){
        var pWidth = ${ecsForm.frameWidth};
        var frameElem = document.getElementById("ecs_frame");
        var frameParent = document.getElementsByClassName("blue_box_container")[0];
        if(frameElem){
        if(pWidth > frameParent.offsetWidth) frameElem.width = '100%';
        } else {
            document.getElementById("heatmap-description").style.display = 'none';
        }
    }
    Event.observe(window, 'load', function() { checkIfmeWidth() });
</script>

<div id="filterbox_container_for_name" >
    <div class="filterbox_form_container">
        <div id="filterbox_top"></div>
        <div id="searchbox_content" class="filterbox_form_container">
            <div id="filterbox_small_label" class="filterbox_small_label_tab">
                ${ecsForm.shortname}&nbsp;&nbsp;|&nbsp;&nbsp;${ecsForm.description}
            </div>
        </div>
        <div id="filterbox_bottom"></div>
    </div>
</div>
<div class="content_element_container">

   <html:form action="/ecs_stat">
        <html:hidden property="mailingId"/>
        <div id="filterbox_container" style="margin-left:0px;">

            <div class="filterbox_form_container">
                <div id="filterbox_top"></div>
                <div id="searchbox_content">
                    <div style="clear:left; margin-bottom:5px; margin-left:10px; margin-top:5px;">

                        <bean:message key="ecs.ViewMode"/>:
                        <html:select property="viewMode" style="width:190px;">
                            <html:option value="${GROSS_CLICKS}"><bean:message key="statistic.GrossClicks"/></html:option>
                            <html:option value="${NET_CLICKS}"><bean:message key="statistic.NetClicks"/></html:option>
                            <html:option value="${PURE_MAILING}"><bean:message key="ecs.PureMailing"/></html:option>
                        </html:select>
                        &nbsp;&nbsp;
                        <bean:message key="recipient.Recipient"/>:
                        <html:select property="selectedRecipient" style="width:190px;">
                            <c:forEach var="recipient" items="${ecsForm.testRecipients}">
                                <html:option value="${recipient.key}">
                                    ${recipient.value}
                                </html:option>
                            </c:forEach>
                        </html:select>
                        &nbsp;&nbsp;
                        <bean:message key="default.Size"/>:
                        <html:select property="frameSize" size="1" style="width:190px;">
                            <html:option value="4">640x480</html:option>
                            <html:option value="1">800x600</html:option>
                            <html:option value="2">1024x768</html:option>
                            <html:option value="3">1280x1024</html:option>
                        </html:select>
                    </div>

                    <br>

                    <div style="clear:left; margin-left:10px;">
                        <label for="colorDescription" style="float:left;"><bean:message key="ecs.ColorCoding"/>:</label>

                        <div style=" margin-bottom:5px;" id="colorDescription">
                            <c:forEach var="color" items="${ecsForm.rangeColors}" varStatus="rowCounter">
                                <div id="divColor${rowCounter.count}"
                                     style="float:left; border:1px solid #000;width:15px;background:#${color.color}; margin-left:20px;">
                                    &nbsp;</div>
                                <label for="divColor${rowCounter.count}" style="float:left;">&nbsp;<bean:message
                                        key="ecs.Heatmap.max"/>&nbsp;${color.rangeEnd}%</label>

                            </c:forEach>
                        </div>
                        <div class="filterbox_form_button filterbox_form_button_right_corner">
                            <input type="hidden" name="refresh_view" value=""/>
                            <a href="#"
                               onclick="document.ecsForm.refresh_view.value='refresh_view'; document.ecsForm.submit(); return false;"><span><bean:message
                                    key="button.Show"/></span></a>
                        </div>
                    </div>
                </div>
                <div id="filterbox_bottom"></div>
            </div>
        </div>

       <div class="ecs_statistics_container">
               <%-- Embedded click statistics view --%>
           <logic:empty name="ecsForm" property="heatmapErrors">
               <iframe src="ecs_view?mailingId=${ecsForm.mailingId}&recipientId=${ecsForm.selectedRecipient}&viewMode=${ecsForm.viewMode}&companyId=${ecsForm.companyId}"
                       class="ecs_statistics_frame"
                       id="ecs_frame" width="${ecsForm.frameWidth}" height="${ecsForm.frameHeight}"></iframe>
           </logic:empty>
           <br><br>
           <span id="heatmap-description"><bean:message key="ecs.Heatmap.description"/></span>
       </div>
    </html:form>
</div>
