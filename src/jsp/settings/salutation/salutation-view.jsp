<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.SalutationAction" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/salutation">
    <html:hidden property="salutationID"/>
    <html:hidden property="action"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_center_column">
                <c:if test="${tmpSalutationID!=0}">
                    <div class="blue_box_form_item">
                        <label for="mailing_name">ID:</label>
                        ${tmpSalutationID}
                    </div>
                </c:if>
                <div class="blue_box_form_item">
                    <label for="mailing_name"><bean:message key="Description"/>:</label>
                    <html:text styleId="mailing_name" property="shortname" size="32"/>
                </div>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">

            <div class="blue_box_left_column">
                <div class="blue_box_form_item salutation_form_item">
                    <label for="salMale">GENDER=0 (<bean:message
                            key="recipient.Male"/>):</label>
                    <html:text styleId="salMale" property="salMale" maxlength="199"/>

                </div>
                <div class="blue_box_form_item salutation_form_item">
                    <label for="salFemale">GENDER=1 (<bean:message
                            key="recipient.Female"/>):</label>
                    <html:text styleId="salFemale" property="salFemale" maxlength="99"/>
                </div>
                <div class="blue_box_form_item salutation_form_item">
                    <label for="salUnknown">GENDER=2 (<bean:message
                            key="recipient.Unknown"/>):</label>
                    <html:text styleId="salUnknown" property="salUnknown" maxlength="99"/>
                </div>

            </div>
        </div>
        <div class="blue_box_bottom"></div>
    </div>

    <p>

    <div class="button_container">

        <input type="hidden" id="save" name="save" value=""/>

        <div class="action_button">
            <a href="#"
               onclick="document.getElementById('save').value='save'; document.salutationForm.submit(); return false;"><span><bean:message
                    key="button.Save"/></span></a>
        </div>


        <div class="action_button">
            <html:link
                    page='<%= new String("/salutation.do?action=" + SalutationAction.ACTION_LIST) %>'>
                <span><bean:message key="button.Cancel"/></span>
            </html:link>
        </div>
    </div>
</html:form>