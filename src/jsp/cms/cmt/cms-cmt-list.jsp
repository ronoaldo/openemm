<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.cms.web.ContentModuleTypeAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'cmt';
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

    function addCMTPreview(container, cmt_id) {
        var preview = document.getElementById(container);
        preview.innerHTML = "<a onclick=\" var imgPreview = document.getElementById('" + container + "');" +
                            "imgPreview.style.display = 'none';  Effect.toggle('img_preview" + cmt_id + "', 'appear');" +
                            "return false;\" href=\"#\"><bean:message key="hidePreview" bundle="cmsbundle"/></a><br>" +
                            "<iframe width=\"${PREVIEW_WIDTH}\" scrolling=\"auto\" height=\"${PREVIEW_HEIGHT}\" border=\"0\"" +
                            "src=\"<html:rewrite page="/cms_cmt.do?action=${ACTION_PURE_PREVIEW}"/>&cmtId=" + cmt_id + "\"" +
                            "style=\"background-color : #FFFFFF;\"> Your Browser does not support IFRAMEs, please update! </iframe>";
    }
</script>


<html:form action="/cms_cmt">
    <input type="hidden" name="action" id="action">

    <div style="margin-left:28px;">
        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
    <div class="advanced_search_toggle cms_new_element toggle_open" id="new_cm_template" onclick="toggleContainer(this);"><a href="#"><bean:message key="cms.NewContentModuleType"/></a></div>
    <div>
        <jsp:include page="cms-cmt-new.jsp"/>
    </div>
    </div>
        <div id="advanced_search_bottom"></div>
    </div>

    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#" onclick="document.getElementById('action').value='1'; document.contentModuleTypeForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${contentModuleTypeForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

</html:form>

                <display:table class="list_table" id="cmt" name="cmtList"
                               pagesize="${contentModuleTypeForm.numberofRows}"
                               requestURI="/cms_cmt.do?action=${ACTION_LIST}" excludedParams="*" sort="list"
                               defaultsort="1">
                    <display:column headerClass="head_cm_template_name" class="cm_template_name" titleKey="default.Name" sortable="true" sortProperty="name">
                        <span class="ie7hack">
                            <html:link
                                       page="/cms_cmt.do?action=${ACTION_VIEW}&cmtId=${cmt.id}">${cmt.name}
                               </html:link>
                        </span>
                    </display:column>
                    <display:column headerClass="head_cm_template_preview" class="cm_template_preview"
                                    titleKey="mailing.Preview">
                        <div id="img_preview${cmt.id}">
                            <table align="center" style=" border: 1px solid #888; background:white;cursor: pointer"
                                   onclick="
                                        var imgPreview = document.getElementById('img_preview${cmt.id}');
                                        imgPreview.style.display = 'none';
                                        Effect.toggle('frame_preview${cmt.id}', 'appear');
                                        addCMTPreview('frame_preview${cmt.id}',${cmt.id});
                                        return false;">
                                <tbody>
                                <tr>
                                    <td align="center"
                                        style="width:<%=ContentModuleTypeAction.PREVIEW_MAX_WIDTH%>px; height: <%=ContentModuleTypeAction.PREVIEW_MAX_HEIGHT%>px;">
                                        <img src="<html:rewrite page="/cms_image?cmtId=${cmt.id}&preview=true"/>" alt="preview thumbnail"><br>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <div id="frame_preview${cmt.id}" style="overflow: visible; display: none;text-align:left;">

                        </div>
                    </display:column>
                    <display:column class="edit_3icons" headerClass="edit_3icons" title="&nbsp;">

                        <html:link styleClass="mailing_edit" titleKey="cms.ContentModuleType.Edit"
                            page="/cms_cmt.do?action=${ACTION_VIEW}&cmtId=${cmt.id}"> </html:link>
                        <logic:equal name="cmt" property="readOnly" value="false">
                            <html:link styleClass="mailing_delete" titleKey="cms.ContentModuleType.Delete"
                                page="/cms_cmt.do?action=${ACTION_CONFIRM_DELETE}&cmtId=${cmt.id}&fromListPage=true"> </html:link>
                        </logic:equal>
                        <html:link styleClass="mailing_new_module" titleKey="cms.ContentModuleType.NewModule"
                                page="/cms_contentmodule.do?action=${ACTION_NEW_CM}&cmtId=${cmt.id}&contentModuleId=0"> </html:link>
                    </display:column>
                </display:table>

    <script type="text/javascript">
        table = document.getElementById('cmt');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#cmt tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
