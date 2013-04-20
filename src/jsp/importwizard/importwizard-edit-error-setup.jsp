<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.service.impl.CSVColumnState" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>


<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnSubtitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.Wizard"); %>
<% request.setAttribute("agnHelpKey", new String("importStep3")); %>
<% request.setAttribute("dateColumnType", CSVColumnState.TYPE_DATE); %>

<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('newImportWizardForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<script src="js/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>
<agn:CheckLogon/>