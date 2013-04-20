<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<%@ include file="/WEB-INF/taglibs.jsp" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'contentModule';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;

    function toggleContainer(container){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        $(container).next().toggle();
    }

    Event.observe(window, 'load', function() {
        <agn:ShowByPermission token="settings.open">
                 var closed = document.getElementsByClassName('toggle_closed');
                if(closed)
                for(var i=0;i<closed.length;i++){
                    closed[i].addClassName('toggle_open');
                    closed[i].next().show();
                    closed[i].removeClassName('toggle_closed');
                }
        </agn:ShowByPermission>
        <agn:HideByPermission token="settings.open">
            toggleContainer(document.getElementById("new_cm_template"));
        </agn:HideByPermission>
        onPageLoad();
    });

    function addContentModulePreview(container, contentModule_id) {
        var previewFrame = document.getElementById(container);
        previewFrame.innerHTML = "<a onclick=\" var imgPreview = document.getElementById('" + container + "'); " +
                                 " imgPreview.style.display = 'none';  Effect.toggle('img_preview" + contentModule_id + "', 'appear');" +
                                 " return false;\" href='#'><bean:message key="hidePreview" bundle="cmsbundle"/></a><br>" +
                                 " <iframe width='${PREVIEW_WIDTH}' scrolling='auto' height='${PREVIEW_HEIGHT}' border='0' " +
                                 " src=\" <html:rewrite page="/cms_contentmodule.do?action=${ACTION_PURE_PREVIEW}"/>&contentModuleId=" + contentModule_id + "\" " +
                                 " style='background-color : #FFFFFF;'>       Your Browser does not support IFRAMEs, please update!    </iframe>";
    }
    function parametersChanged() {
        document.getElementsByName('contentModuleForm')[0].numberOfRowsChanged.value = true;
    }
</script>

<html:form action="/cms_contentmodule">
    <html:hidden property="numberOfRowsChanged"/>
    <input type="hidden" name="action" id="action">
    <input type="hidden" name="contentModuleId" id="contentModuleId" value="0">

<div style="margin-left:28px;">
        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
        <div class="export_wizard_content category_selector" style="margin-top:0px;">
        <bean:message key="cms.Category"/>:&nbsp;
        <html:select property="categoryToShow" size="1" styleClass="cms_new_element_select">
            <html:option value="0">
                    &lt;<bean:message key="none"/>&gt;
            </html:option>
            <c:forEach var="cmCategory" items="${contentModuleForm.allCategories}">
                <html:option value="${cmCategory.id}">
                    ${cmCategory.name}
                </html:option>
            </c:forEach>
        </html:select>
    </div>

    </div>
        <div id="advanced_search_bottom"></div>
    </div>

    <div style="margin-left:28px;">
        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="advanced_search_toggle cms_new_element toggle_open" id="new_cm_template"
                 onclick="toggleContainer(this);"><a href="#"><bean:message key="cms.NewContentModule"/></a></div>
            <div>
                <jsp:include page="cms-contentmodule-new-pure.jsp"/>
            </div>
        </div>
        <div id="advanced_search_bottom"></div>
    </div>


    <div class="list_settings_container cm_list_panel">
        <div class="filterbox_form_button"><a href="#" onclick="parametersChanged(); document.getElementById('action').value='1'; document.contentModuleForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${contentModuleForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

</html:form>


    <display:table class="list_table" id="contentModule" name="contentModuleList"
                   pagesize="${contentModuleForm.numberofRows}"
                   requestURI="/cms_contentmodule.do?action=${ACTION_LIST}" excludedParams="*" sort="list"
                   defaultsort="1">
        <display:column headerClass="head_cm_template_name" class="cm_template_name" titleKey="default.Name" sortable="true" sortProperty="name">
            <span class="ie7hack">
                <html:link
                           page="/cms_contentmodule.do?action=${ACTION_VIEW}&contentModuleId=${contentModule.id}">${contentModule.name}
                   </html:link>
            </span>
        </display:column>
        <display:column headerClass="head_cm_template_preview" class="cm_template_preview"
                        titleKey="mailing.Preview">
            <div id="img_preview${contentModule.id}">
                <table align="center" style=" border: 1px solid #888; background:white;cursor: pointer"
                       onclick="
                            var imgPreview = document.getElementById('img_preview${contentModule.id}');
                            imgPreview.style.display = 'none';
                            Effect.toggle('frame_preview${contentModule.id}', 'appear');
                            addContentModulePreview('frame_preview${contentModule.id}',${contentModule.id});
                            return false;">
                    <tbody>
                    <tr>
                        <td align="center"
                            style="width: <%=ContentModuleAction.PREVIEW_MAX_WIDTH%>px; height: <%=ContentModuleAction.PREVIEW_MAX_HEIGHT%>px;">
                            <img src="<html:rewrite page="/cms_image?cmId=${contentModule.id}&preview=true"/>"
                                 alt="preview thumbnail"><br>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div id="frame_preview${contentModule.id}" style="overflow: visible; display: none;text-align:left">

            </div>

        </display:column>
        <display:column title="&nbsp;">

            <html:link styleClass="mailing_edit" titleKey="cms.ContentModule.Edit"
                page="/cms_contentmodule.do?action=${ACTION_VIEW}&contentModuleId=${contentModule.id}&mailingId=0"> </html:link>
            <html:link styleClass="mailing_delete" titleKey="cms.ContentModule.Delete"
                page="/cms_contentmodule.do?action=${ACTION_CONFIRM_DELETE}&contentModuleId=${contentModule.id}&fromListPage=true"> </html:link>

        </display:column>
    </display:table>

    <script type="text/javascript">
        table = document.getElementById('contentModule');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#contentModule tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
