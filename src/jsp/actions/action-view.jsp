<%--checked--%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.actions.*, java.util.*, org.springframework.web.context.support.*, org.agnitas.web.forms.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% int tmpActionID=((EmmActionForm)session.getAttribute("emmActionForm")).getActionID(); %>

<html:form action="/action" method="post">
    <html:hidden property="action"/>
    <html:hidden property="actionID"/>


    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                    <%--<div class="blue_box_form_item">--%>
                <div class="action_name_box">
                    <label for="mailing_name" class="action_name_label"><bean:message key="default.Name"/>:</label>
                    <html:text styleId="mailing_name" property="shortname" maxlength="99" size="42"/>
                </div>

                <div class="action_name_box">
                    <agn:ShowByPermission token="actions.set_usage">
                        <label class="action_name_label"><bean:message key="action.Usage"/>:</label>
                        <html:select property="type" size="1" styleClass="namebox_select">
                            <html:option value="0"><bean:message key="action.Type.link"/></html:option>
                            <html:option value="1"><bean:message key="action.Type.form"/></html:option>
                            <html:option value="9"><bean:message key="action.Type.all"/></html:option>
                        </html:select>
                    </agn:ShowByPermission>
                </div>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <logic:present name="emmActionForm" property="actions">
            <h2 class="action_steps_header"><bean:message key="action.Steps"/>:</h2>

    <% int index=0;
       String[] classNames=null;
       String className=null;

       org.springframework.context.ApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application); %>
    <logic:iterate id="op" name="emmActionForm" property="actions">

            <html:errors property="<%= Integer.toString(index) %>"/>
            <%
                request.setAttribute("op", pageContext.getAttribute("op"));
                request.setAttribute("opIndex", new Integer(index));
                classNames=wac.getBeanNamesForType(pageContext.getAttribute("op").getClass());
                className=classNames[0];
                index++;
                
            %>
        <div class="blue_box_container">
            <div class="blue_box_top"></div>
            <div class="blue_box_content">
                <div class="action_step_header">
                    <label class="action_step_header"><%= index %>.&nbsp;<bean:message
                            key='<%= new String(\"action.op.\"+className) %>'/></label>
                </div>


                <jsp:include page='<%= new String(\"ops/\"+className+\".jsp\") %>'/>
            </div>
            <div class="blue_box_bottom"></div>
        </div>

    </logic:iterate>
    </logic:present>
    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <span class="head3"><bean:message key="action.Add_Step"/>:</span><br><br>

            <div class="action_new_type_box">
                <bean:message key="default.Type"/>:&nbsp;
                <html:select property="newModule" size="1">
                    <logic:iterate id="aop" name="oplist" scope="session">
                        <html:option value="${aop.value}"><bean:write name="aop" property="key"/></html:option>
                    </logic:iterate>
                </html:select>
                <input type="hidden" id="add" name="add" value=""/>
            </div>
            <agn:ShowByPermission token="actions.change">
                <div class="action_button add_actiontype_button">
                    <a href="#"
                       onclick="document.getElementById('add').value='add'; document.emmActionForm.submit(); return false;"><span><bean:message
                            key="button.Add"/></span></a>
                </div>
            </agn:ShowByPermission>
        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <div class="button_container">
        <agn:ShowByPermission token="actions.change">
        	<div class="action_button">
            	<a href="#" onclick="document.emmActionForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        	</div>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="actions.delete">
        	<logic:notEqual name="emmActionForm" property="actionID" value="0">
        		<div class="action_button">
            		<a href="<html:rewrite page='<%= new String("/action.do?action=" + EmmActionAction.ACTION_CONFIRM_DELETE + "&actionID=" + tmpActionID + "&fromListPage=false") %>'/>"><span><bean:message key="button.Delete"/></span></a>
        		</div>
        	</logic:notEqual>
        </agn:ShowByPermission>
    </div>
</html:form>