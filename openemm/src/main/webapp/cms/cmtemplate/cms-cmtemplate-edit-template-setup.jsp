<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.CMTemplateAction, org.agnitas.cms.web.forms.CMTemplateForm, org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.cms.utils.CmsUtils" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="/WEB-INF/taglibs.jsp" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="Mailings" scope="request"/>
<c:set var="sidemenu_sub_active" value="ContentManagement" scope="request"/>
<c:set var="agnTitleKey" value="ContentManagement" scope="request"/>
<c:set var="agnSubtitleKey" value="cms.CMTemplates" scope="request"/>
<c:set var="agnSubtitleValue" value="${cmTemplateForm.name}" scope="request"/>
<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request"/>
<c:set var="agnHighlightKey" value="cms.CMTemplates" scope="request"/>
<c:set var="agnNavHrefAppend" value="&cmTemplateId=${cmTemplateForm.cmTemplateId}" scope="request"/>
<c:set var="agnHelpKey" value="cmTemplateView" scope="request"/>

<c:set var="ACTION_EDIT_TEMPLATE" value="<%= CMTemplateAction.ACTION_EDIT_TEMPLATE %>" scope="request"/>
<c:set var="ACTION_DELETE_IMAGE_TEMPLATE" value="<%= CMTemplateAction.ACTION_DELETE_IMAGE_TEMPLATE %>" scope="request"/>
<c:set var="ACTION_SAVE_TEMPLATE" value="<%= CMTemplateAction.ACTION_SAVE_TEMPLATE %>" scope="request"/>

<c:set var="PREVIEW_URL"
       value="/cms_cmtemplate.do?action=${ACTION_PURE_PREVIEW}&cmTemplateId=${cmTemplateForm.cmTemplateId}" scope="request"/>
 <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7">
<script type="text/javascript">
    <!--
    function isChange(item) {
        var checkBox = document.getElementById(item);
        var numDiv = checkBox.name.substring(checkBox.name.indexOf(".") + 1, checkBox.name.lastIndexOf("."));
        var tRadioUpload = document.getElementById("imageUploadOrExternal." + numDiv + ".upload");
        var tRadioExternal = document.getElementById("imageUploadOrExternal." + numDiv + ".external");
        var tFile = document.getElementById("imageUploadOrExternal." + numDiv + ".file");
        var tUrl = document.getElementById("imageUploadOrExternal." + numDiv + ".url");
        var tText = document.getElementById("name." + numDiv + ".text");
        if (checkBox.checked == true) {
            tRadioUpload.removeAttribute("disabled");
            tRadioExternal.removeAttribute("disabled");
            tFile.removeAttribute("disabled");
            tUrl.removeAttribute("disabled");
            tText.removeAttribute("disabled");
        } else {
            tRadioUpload.setAttribute("disabled", "true");
            tRadioExternal.setAttribute("disabled", "true");
            tFile.setAttribute("disabled", "true");
            tUrl.setAttribute("disabled", "true");
            tText.setAttribute("disabled", "true");
        }
    }
    //-->
</script>
