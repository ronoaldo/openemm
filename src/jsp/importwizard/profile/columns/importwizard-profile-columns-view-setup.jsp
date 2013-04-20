<%@ page language="java" import="org.agnitas.web.forms.ImportProfileColumnsForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<%
    ImportProfileColumnsForm aForm = (ImportProfileColumnsForm) session.getAttribute("importProfileColumnsForm");
%>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.ManageColumns"); %>
<% request.setAttribute("agnSubtitleKey", "import.ManageColumns"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% request.setAttribute("agnHighlightKey", "import.ManageColumns"); %>
<% request.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% request.setAttribute("agnSubtitleValue", aForm.getProfile().getName()); %>
<% request.setAttribute("importProfileColumnsForm",aForm);%>
<% request.setAttribute("currentFormName","importProfileColumnsForm");%>
<% request.setAttribute("agnHelpKey", new String("manageFields")); %>

<script type="text/javascript">
    function columnChanged(selectId, rowIndex) {
        var selectElement = document.getElementById(selectId);
        if (selectElement == null) return;
        var selectedDBColumn = selectElement.value;
        var defaultElement = document.getElementById('default.' + selectedDBColumn);
        if (defaultElement != null) {
            var defaultValue = defaultElement.value;
            var columnDefaultField = document.getElementById('id_default_value_' + rowIndex);
            columnDefaultField.value = defaultValue;
        }
    }
</script>