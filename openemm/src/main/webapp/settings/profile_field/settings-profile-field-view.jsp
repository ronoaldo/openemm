<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<html:form action="/profiledb">
    <html:hidden property="companyID"/>
    <html:hidden property="action"/>
    <html:hidden property="oldStyle"/>
    <html:hidden property="fieldType" styleId="hiddenFieldType"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="settings.FieldName"/>:</label>
                <html:text styleId="mailing_name" property="shortname" maxlength="99" size="32"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="2" cols="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">

            <c:choose>
                <c:when test="${not empty TMP_FIELDNAME}">

                    <div class="profile_filed_detail_form_item">
                        <label><bean:message key="settings.FieldNameDB"/>:</label>
                        <c:out value="${TMP_FIELDNAME}"/>
                    </div>
                    <div class="profile_filed_detail_form_item">
                        <html:hidden property="fieldname"/>

                        <label><bean:message key="default.Type"/>:&nbsp;</label>
                        <bean:message key="settings.fieldType.${profileFieldForm.fieldType}"/>
                    </div>


                    <c:if test="${profileFieldForm.fieldType == 'VARCHAR'}">

                        <div class="profile_filed_detail_form_item">

                            <label><bean:message key="settings.Length"/>:&nbsp;</label>
                            <c:out value="${profileFieldForm.fieldLength}"/>
                        </div>
                    </c:if>


                    <div class="profile_filed_detail_form_item">
                        <label for="fieldDefault"><bean:message
                                key="settings.Default_Value"/>:&nbsp;</label>
                        <html:text property="fieldDefault" size="32" styleId="fieldDefault"/>
                    </div>


                    <div class="profile_filed_detail_form_item">
                        <label><bean:message key="settings.NullAllowed"/>:&nbsp;</label>
                        <c:choose>
                            <c:when test="${profileFieldForm.fieldNull}">
                                <bean:message key="default.Yes"/>
                            </c:when>
                            <c:otherwise>
                                <bean:message key="default.No"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>

                    <div class="profile_filed_detail_form_item">
                        <label><bean:message key="settings.FieldNameDB"/>:&nbsp;</label>
                        <html:text property="fieldname" size="32"/>
                    </div>


                    <div class="profile_filed_detail_form_item">
                        <label for="fieldType"><bean:message key="default.Type"/>:&nbsp;</label>
                        <html:select property="fieldType" size="1" styleId="fieldType">
                            <html:option value="INTEGER"><bean:message key="settings.fieldType.INTEGER"/></html:option>
                            <html:option value="VARCHAR"><bean:message key="settings.fieldType.VARCHAR"/></html:option>
                            <html:option value="DATE"><bean:message key="settings.fieldType.DATE"/></html:option>
                        </html:select>

                    </div>


                    <div class="profile_filed_detail_form_item">
                        <label for="fieldLenght"><bean:message key="settings.Length"/>:&nbsp;</label>
                        <html:text property="fieldLength" size="5"
                                   styleId="fieldLenght"/>&nbsp;&nbsp;<bean:message
                            key="settings.profile.hint"/>
                    </div>


                    <div class="profile_filed_detail_form_item">
                        <label for="fieldDefault"><bean:message
                                key="settings.Default_Value"/>:&nbsp;</label>
                        <html:text property="fieldDefault" size="32" styleId="fieldDefault"/>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="blue_box_bottom"></div>
    </div>


    <div class="button_container" style="padding-top:5px;">
        <input type="hidden" name="save" value=""/>

        <div class="action_button">
            <a href="#"
               onclick="setFieldType(); document.profileFieldForm.save.value='save'; document.profileFieldForm.submit();return false;">
                <span><bean:message key="button.Save"/></span>
            </a>
        </div>
        <c:if test="${not empty param.fieldname}">
            <div class="action_button">
                <html:link page="/profiledb.do?action=${ACTION_CONFIRM_DELETE}&fieldname=${TMP_FIELDNAME}&fromListPage=false">
                    <span><bean:message key="button.Delete"/></span>
                </html:link>
            </div>
        </c:if>
        <div class="action_button">
            <html:link page="/profiledb.do?action=${ACTION_LIST}">
                <span><bean:message key="button.Cancel"/></span>
            </html:link>
        </div>
    </div>

<script type="text/javascript">
    function setFieldType() {
        if (document.getElementById('fieldType')!=null) {
            document.getElementById('hiddenFieldType').value=document.getElementById('fieldType').value;
        }
    }
</script>

</html:form>
