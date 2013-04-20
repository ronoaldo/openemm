<%@ page language="java" import="org.agnitas.util.*,org.agnitas.web.*, org.agnitas.beans.*"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.components.show"/>

<% int tmpMailingID = Integer.parseInt(request.getParameter("mailingID"));
    Company company = ((Admin) session.getAttribute("emm.admin")).getCompany();
%>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="../../../../../${emmLayoutBase.cssURL}/style.css">
    <link type="text/css" rel="stylesheet" href="../../../../../${emmLayoutBase.cssURL}/structure.css">
    <link type="text/css" rel="stylesheet" href="../../../../../${emmLayoutBase.cssURL}/displaytag.css">
</head>

<script type="text/javascript">

    function updateImg() {
        var imageNameValue = document.selform.imgsel.value;
        if (imageNameValue == null || imageNameValue.length == 0){
            document.theimage.style.display = 'none';
            document.getElementById("no_image_message").style.display = '';
        } else {
            document.theimage.src = normalizeName(imageNameValue);
        }
        return 1;
    }

    function submit_image() {
        window.opener.SetUrl(normalizeName(document.selform.imgsel.value));
        window.close();
        return 1;
    }

    function normalizeName(fname) {
        if (fname.substr(0, 4).toLowerCase() != 'http') {
        fname = '<%= company.getRdirDomain() %>/image?ci=<%= company.getId() %>&mi=<%= tmpMailingID %>&name=' + fname;
        }
        // alert(fname);
        return fname;
    }
</script>

<body onload="updateImg()">

<% String query = "from MailingComponent where (comptype=1 or comptype=5) and mailing_id=" +
        tmpMailingID +
        " and company_id=" + company.getId() + " order by comptype desc, compname";
%>
<form name="selform" id="selform" action="">
    <div>
        <div class="float_left fckeditor_select_panel">
            <div class="float_left">
                <bean:message key="mailing.Graphics_Component"/>:&nbsp;
                <select name="imgsel" id="imgsel" onchange="updateImg()" size="1">
                    <agn:HibernateQuery id="comp" query="<%= query %>" maxRows="100">
                        <option value="${comp.componentName}">${comp.componentName}</option>
                    </agn:HibernateQuery>
                </select>
            </div>
            <div class="maildetail_button add_button">
                <a href="#"
                   onclick="submit_image();return false;">
                    <span><bean:message key="Select"/></span>
                </a>
            </div>
        </div>
        <br>
        <div class="dotted_line fckeditor_dotted_line"></div>
        <br>
    </div>
</form>

<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
    <tr height="90%" width="100%">
        <td align="center" valign="center">
            <img src="fckeditor-2.6.6/editor/images/spacer.gif" name="theimage" border="1">
            <div id="no_image_message" style="display:none;"><bean:message key="mailing.Graphics_Component.NoImage"/></div>
        </td>
    </tr>
</table>
</body>
</html>
