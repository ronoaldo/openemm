<%@ page language="java" import="org.agnitas.dao.MailingDao,org.agnitas.dao.TitleDao" contentType="text/html; charset=utf-8"%>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<agn:CheckLogon/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta content="noindex, nofollow" name="robots">
		<script type="text/javascript">

var oEditor = window.parent.InnerDialogLoaded() ;

function OnLoad()
{
	// First of all, translate the dialog box texts
	oEditor.FCKLanguageManager.TranslatePage( document ) ;

	window.parent.SetAutoSize( true ) ;
	window.parent.SetOkButton( true ) ;
	
	updateTagParameters(document.getElementById('tagsel').value);
}

function hideAll() {
    document.getElementById('param1').style.visibility='hidden';
    document.getElementById('param2').style.visibility='hidden';
    document.getElementById('param3').style.visibility='hidden';
    
    document.getElementById('paramlabel1').style.visibility='hidden';
    document.getElementById('paramlabel2').style.visibility='hidden';
    document.getElementById('paramlabel3').style.visibility='hidden';
    
    document.getElementById('columns').style.visibility='hidden';
    document.getElementById('types').style.visibility='hidden';
    
    return;
}

function updateTagParameters(input) {
    hideAll();
    var parameters=getTagParameters(input);
    if(parameters) {
        for (i=0; i < parameters.length; ++i) {
            //alert(parameters[i]);
            var parname='param'+(i+1);
            var parlabel='paramlabel'+(i+1);
            if(parameters[i]=='column') {
                document.getElementById('columns').style.visibility='visible';
            } else {
                if(parameters[i]=='type') {
                    document.getElementById('types').style.visibility='visible';
                } else {
                    document.getElementById(parname).style.visibility='visible';
                }
            }
            document.getElementById(parlabel).firstChild.data=parameters[i]+":";
            document.getElementById(parlabel).style.visibility='visible';
        }
    }
    return true;
}

function buildTagParameters(input) {
    var parameters=getTagParameters(input);
    var params='';
    if(parameters) {
        for (i=0; i < parameters.length; ++i) {
            var parname='par'+(i+1);
            if(parameters[i]=='column') {
                parname='colsel';
            }
            if(parameters[i]=='type') {
               parname='typesel';
            }
            var parvalue=document.getElementById(parname).value;
            // alert(parname+": "+parvalue);
            params=params+" "+parameters[i]+'="'+parvalue+'"';
        }
    }

    return '['+document.getElementById('tagsel').options[document.getElementById('tagsel').selectedIndex].text+params+']';
}

function Ok() { // ok-button hit
    oEditor.FCK.InsertHtml(buildTagParameters(document.getElementById('tagsel').value));
    return true;
}


function getTagParameters(input) {
    // alert(input);
    var parameters=input.match(/\{[^}]*\}/g);
    if(parameters) {
        for (i=0; i < parameters.length; ++i) {
            parameters[i]=parameters[i].substr(1, parameters[i].length-2);
        }
    }
    return parameters;
}

		</script>
	</head>
	
<%
    ApplicationContext aContext = WebApplicationContextUtils.getWebApplicationContext(application);
    MailingDao mailingDao = (MailingDao) aContext.getBean("MailingDao");
    TitleDao titleDao = (TitleDao) aContext.getBean("TitleDao");
    List<org.agnitas.beans.Title> titles = titleDao.getTitles(AgnUtils.getCompanyID(request));
    pageContext.setAttribute("titles",titles);
    List<Map<String, String>> tags = mailingDao.getTags(AgnUtils.getCompanyID(request));
    pageContext.setAttribute("tags",tags);
%>
	
	<body onload="OnLoad()" scroll="no" style="OVERFLOW: hidden">
		<table cellSpacing="3" cellPadding="2" width="100%" border="0">
			<tr>
				<td noWrap><label for="tagsel" fckLang="DlgMyEmmTag">Tag:</label>
				</td>
				<td width="100%">  
                                    <select name="tagsel" id="tagsel" onchange="updateTagParameters(document.getElementById('tagsel').value)" size="1">
                                        <c:forEach var="tag" items="${tags}">
                                            <c:forEach var="tagM" items="${tag}">
                                               <option value='<c:out value="${tagM.value}"/>'>${tagM.key}</option>
                                            </c:forEach>
                                        </c:forEach>
                                    </select>
				</td>
			</tr>
			<tr>
				<td vAlign="top" nowrap>
                                    <div id="paramlabel1" style="position:relative; top:0px; left:0px; visibility:hidden">
                                        Parameter 1:
                                    </div>
				</td>
				<td vAlign="top">
				    <div id="cntnr" style="position:relative; top:0px; left:0px">
				       <div id="param1" style="position:absolute; top:0px; left:0px; visibility:hidden">
                                         <input type="text" id="par1" name="par1" size="30">
                                       </div>
                                       <div id="columns" style="position:absolute; top:0px; left:0px; visibility:hidden">
                                         <select id="colsel" name="colsel" fckLang="DlgColumnSelection" size="1">
                                            <agn:ShowColumnInfo id="colsel">
                                                <option value='<%= pageContext.getAttribute("_colsel_column_name") %>'><%= pageContext.getAttribute("_colsel_shortname") %></option>
                                            </agn:ShowColumnInfo>
                                         </select>
                                       </div>
                                       <div id="types" style="position:absolute; top:0px; left:0px; visibility:hidden">
                                         <select id="typesel" name="typesel" size="1">
                                            <c:forEach var="title" items="${titles}">
                                               <option value="${title.id}">${title.description}</option>
                                            </c:forEach>
                                         </select>
                                       </div>
                                    </div>
				</td>
			</tr>
			
			<tr>
				<td vAlign="top" nowrap>
				    <div id="paramlabel2" style="position:relative; top:0px; left:0px; visibility:hidden">
                                    Parameter 2:
                                    </div>
				</td>
				<td vAlign="top">
                                    <div id="param2" style="position:relative; top:0px; left:0px; visibility:hidden">
                                    <input type="text" id="par2" name="par2" size="30">
                                    </div>
				</td>
			</tr>
			
			<tr>
				<td vAlign="top" nowrap>
                                    <div id="paramlabel3" style="position:relative; top:0px; left:0px; visibility:hidden">
                                    Parameter 3:
                                    </div>
				</td>
				<td vAlign="top">
                                    <div id="param3" style="position:relative; top:0px; left:0px; visibility:hidden">
                                    <input type="text" id="par3" name="par3" size="30">
                                    </div>
				</td>
			</tr>

            <tr>
				<td vAlign="top" nowrap>
                                    <div id="paramlabel4" style="position:relative; top:0px; left:0px; visibility:hidden">
                                    Parameter 4:
                                    </div>
				</td>
				<td vAlign="top">
                                    <div id="param4" style="position:relative; top:0px; left:0px; visibility:hidden">
                                    <input type="text" id="par4" name="par4" size="26">
                                    </div>
				</td>
			</tr>

		</table>
	</body>
</html>
