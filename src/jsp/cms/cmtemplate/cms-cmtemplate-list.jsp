<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.CMTemplateAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://displaytag.sf.net"  prefix="display" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'cmTemplate';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;

    function toggleContainer(container){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        $(container).next().toggle();
    }

    function doOpen(template_id){
        var preview= document.getElementById('cms_preview_content');
        preview.innerHTML = "<iframe width=\"${PREVIEW_WIDTH}\" scrolling=\"auto\" height=\"${PREVIEW_HEIGHT}\" frameBorder=\"0\"" +
                            "src=\"<html:rewrite page="/cms_cmtemplate.do?action=${ACTION_PURE_PREVIEW}"/>&cmTemplateId=" + template_id + "\"" +
                            "style=\"background-color : #FFFFFF;\">     Your Browser does not support IFRAMEs, please update! </iframe>";
        document.getElementById('cms_preview_holder').style.display = 'block';
        document.getElementById('cms_preview_holder').style.width = new Number(${PREVIEW_WIDTH} + 20) + 'px';
        document.getElementById('cms_preview_holder').style.height = new Number(${PREVIEW_HEIGHT} + 20) + "px";
        document.getElementById('cms_modal').style.display = 'block';
    }

    function doClose(){
      document.getElementById('cms_preview_content').innerHTML='';
      document.getElementById('cms_preview_holder').style.display='none';
      document.getElementById('cms_modal').style.display='none';
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

    function addTemplatePreview(container, template_id) {
        var preview = document.getElementById(container);
        preview.innerHTML = "<a onclick=\"  var imgPreview = document.getElementById('" + container + "');" +
                            "imgPreview.style.display = 'none'; Effect.toggle('img_preview" + template_id + "', 'appear');" +
                            "return false;\"  href=\"#\"><bean:message key="hidePreview" bundle="cmsbundle"/></a><br>" +
                            "<iframe width=\"${PREVIEW_WIDTH}\" scrolling=\"auto\" height=\"${PREVIEW_HEIGHT}\" border=\"0\"" +
                            "src=\"<html:rewrite page="/cms_cmtemplate.do?action=${ACTION_PURE_PREVIEW}"/>&cmTemplateId=" + template_id + "\"" +
                            "style=\"background-color : #FFFFFF;\">     Your Browser does not support IFRAMEs, please update! </iframe>";
    }

    function parametersChanged() {
        document.getElementsByName('cmTemplateForm')[0].numberOfRowsChanged.value = true;
    }
</script>
 <div id="cms_modal" onclick="doClose()"></div>
 <div id="cms_preview_holder">
   <div id="cms_preview_content"></div>
   <div>
       <a href="#" onclick="doClose()" id="cms_close">
           <img src="${emmLayoutBase.imagesURL}/closelabel.gif" />
       </a>
   </div>
  </div>
<html:form action="/cms_cmtemplate" enctype="multipart/form-data">
    <input type="hidden" name="action" id="action">
    <html:hidden property="numberOfRowsChanged"/>
    <div style="margin-left:28px;">
        <div id="advanced_search_top"></div>
        <div id="advanced_search_content">
            <div class="advanced_search_toggle cms_new_element toggle_open" id="new_cm_template"
                 onclick="toggleContainer(this);"><a href="#"><bean:message key="cms.NewCMtemplate"/></a></div>
            <div>
                <jsp:include page="cms-cmtemplate-upload.jsp"/>
            </div>
        </div>
        <div id="advanced_search_bottom"></div>
    </div>

    <div id="cms_preview_mode">
                <label for="preview_mode"><bean:message key="cms.showTemplatePreview"/>:</label>
                <html:select styleId="preview_mode" property="previewMode" size="1">
                        <html:option value="${PREVIEW_MODE_COLUMN}"><bean:message key="cms.inColumn"/></html:option>
                        <html:option value="${PREVIEW_MODE_POPUP}"><bean:message key="cms.inPopup"/></html:option>
                </html:select>
    </div>

    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#" onclick="parametersChanged(); document.getElementById('action').value='1'; document.cmTemplateForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>

        <logic:iterate collection="${cmTemplateForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

    </html:form>

    <%--<ajax:displayTag id="cmTemplateTable" ajaxFlag="displayAjax">--%>
        <display:table class="list_table" id="cmTemplate" name="cmTemplateList"
                       pagesize="${cmTemplateForm.numberofRows}"
                       requestURI="/cms_cmtemplate.do?action=${ACTION_LIST}" excludedParams="*" sort="list"
                       defaultsort="1">
            <display:column headerClass="head_cm_template_name" class="cm_template_name" titleKey="default.Name" sortable="true" sortProperty="name">
                <span class="ie7hack">
                    <html:link
                            page="/cms_cmtemplate.do?action=${ACTION_VIEW}&cmTemplateId=${cmTemplate.id}">${cmTemplate.name}
                    </html:link>
                </span>
            </display:column>
            <logic:equal name="cmTemplateForm" property="previewMode" value="${PREVIEW_MODE_POPUP}">
                <display:column headerClass="head_cm_template_preview" class="cm_template_name" titleKey="Description" sortable="true" sortProperty="description">
                    <span class="ie7hack">
                        <html:link
                                page="/cms_cmtemplate.do?action=${ACTION_VIEW}&cmTemplateId=${cmTemplate.id}">${cmTemplate.description}
                        </html:link>
                    </span>
                </display:column>
            </logic:equal>
            <logic:notEqual name="cmTemplateForm" property="previewMode" value="${PREVIEW_MODE_POPUP}">
                <display:column headerClass="head_cm_template_preview" class="cm_template_preview"
                                titleKey="mailing.Preview">
                <div id="img_preview${cmTemplate.id}">
                    <table align="center" style=" border: 1px solid #888888; background:white;cursor: pointer"
                           onclick="
                                var imgPreview = document.getElementById('img_preview${cmTemplate.id}');
                                imgPreview.style.display = 'none';
                                Effect.toggle('frame_preview${cmTemplate.id}', 'appear');
                                addTemplatePreview('frame_preview${cmTemplate.id}',${cmTemplate.id});
                                return false;">
                        <tbody>
                        <tr>
                            <td align="center" style="width:<%=CMTemplateAction.PREVIEW_MAX_WIDTH%>px;
                                                     height: <%=CMTemplateAction.PREVIEW_MAX_HEIGHT%>px;">
                                <img src="<html:rewrite page="/cms_image?cmTemplateId=${cmTemplate.id}&preview=true"/>"
                                     alt="preview thumbnail"><br>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div id="frame_preview${cmTemplate.id}" style="overflow: visible; display: none;text-align:left">

                </div>
            </display:column>
            </logic:notEqual>
            <display:column headerClass="head_cm_template_edit" class="head_cm_template_edit" title="&nbsp;">
                <logic:equal name="cmTemplateForm" property="previewMode" value="${PREVIEW_MODE_POPUP}">
                  <a href="#" onclick="doOpen(${cmTemplate.id})" class="mailing_preview" />
                </logic:equal>
                <html:link styleClass="mailing_edit" titleKey="cms.CMTemplate.Edit"
                       page="/cms_cmtemplate.do?action=${ACTION_VIEW}&cmTemplateId=${cmTemplate.id}"> </html:link>
                <html:link styleClass="mailing_delete" titleKey="cms.CMTemplate.Delete"
                       page="/cms_cmtemplate.do?action=${ACTION_CONFIRM_DELETE}&cmTemplateId=${cmTemplate.id}&fromListPage=true"> </html:link>

            </display:column>
        </display:table>
    <%--</ajax:displayTag>--%>


<script type="text/javascript">
    table = document.getElementById('cmTemplate');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#cmTemplate tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>


