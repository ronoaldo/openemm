<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:ShowColumnInfo id="colsel"/>

<script type="text/javascript">
	function submitAction(actionId) {
		document.getElementsByName("action")[0].value = actionId;
		document.targetForm.submit();
	}
</script>

<c:set var="ACTION_VIEW" value="<%= TargetAction.ACTION_VIEW %>" />

<html:form action="/target" focus="shortname">
    <html:hidden property="targetID"/>
    <html:hidden property="action"/>
    <html:hidden property="previousAction" value="${ACTION_VIEW}"/>


    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="shortname" size="42" maxlength="99"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" cols="32" rows="5"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div><script type="text/javascript">
	function submitAction(actionId) {
		document.getElementsByName("action")[0].value = actionId;
		document.targetForm.submit();
	}
</script>
        
        <div class="grey_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="target.TargetDefinition"/>:</h2>

            <table border="0" cellspacing="2" cellpadding="0">
                <!-- list of defined rules -->
                <c:set var="FORM_NAME" value="targetForm" scope="page"/>
                <%@include file="/rules/rules_list.jsp" %>
            </table>
        </div>
        <div class="blue_box_bottom"></div>
    </div>
    <agn:ShowByPermission token="targets.change">
    	<div class="blue_box_container">
        	<div class="blue_box_top"></div>
        	<div class="blue_box_content">
            	<h2 class="blue_box_header"><bean:message key="target.NewRule"/>:</h2>
            	<table border="0" cellspacing="2" cellpadding="0">

                	<!-- new rule to add -->
                	<%@include file="/rules/rule_add.jsp" %>
            	</table>
        	</div>
        	<div class="blue_box_bottom"></div>
    	</div>
    </agn:ShowByPermission>
    <div class="button_container">

        <input type="hidden" id="save" name="save" value=""/>
		<agn:ShowByPermission token="targets.change">
        	<div class="action_button">
            	<a href="#"
               		onclick="submitAction(${ACTION_SAVE}); return false;"><span><bean:message
                    key="button.Save"/></span></a>
        	</div>
        </agn:ShowByPermission>	

        <c:if test="${not empty targetForm.targetID and targetForm.targetID != 0}">
            <input type="hidden" id="delete" name="delete" value=""/>
			<agn:ShowByPermission token="targets.delete">
            	<div class="action_button">
                	<a href="#"
                   		onclick="submitAction(${ACTION_CONFIRM_DELETE}); return false;"><span><bean:message
                        key="button.Delete"/></span></a>
	            </div>
			</agn:ShowByPermission>
            <input type="hidden" id="copy" name="copy" value=""/>
			<agn:ShowByPermission token="targets.change">
            	<div class="action_button">
                	<a href="#"
                   		onclick="submitAction(${ACTION_CLONE}); return false;"><span><bean:message
                        key="button.Copy"/></span></a>
	            </div>
	        </agn:ShowByPermission>
        </c:if>

        <div class="action_button"><bean:message key="target.Target"/>:</div>
    </div>

    <c:if test="${not empty targetForm.targetID and targetForm.targetID != 0}">
        <div class="button_container">
            <div align=right><html:link styleClass="target_view_link"
                                        page="/recipient_stats.do?action=2&mailinglistID=0&targetID=${targetForm.targetID}"><bean:message
                    key="Statistics"/>...</html:link></div>
            <agn:ShowByPermission token="targets.createml">
                <br>

                <div align=right><html:link styleClass="target_view_link"
                                            page="/target.do?action=${ACTION_CREATE_ML}&targetID=${targetForm.targetID}"><bean:message
                        key="target.createMList"/></html:link></div>
            </agn:ShowByPermission>

            <agn:ShowByPermission token="recipient.delete">
                <br>

                <div align=right><html:link styleClass="target_view_link"
                                            page="/target.do?action=${ACTION_DELETE_RECIPIENTS_CONFIRM}&targetID=${targetForm.targetID}"><bean:message
                        key="target.delete.recipients"/></html:link></div>
            </agn:ShowByPermission>
        </div>
    </c:if>
</html:form>
