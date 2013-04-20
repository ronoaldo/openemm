<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.web.CampaignAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_LIST" value="<%= CampaignAction.ACTION_LIST %>" scope="page"/>

<html:form action="/campaign.do">
    <html:hidden property="campaignID"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="campaign.Campaign"/>:&nbsp;${campaignForm.shortname}<br>
        <bean:message key="campaign.DeleteCampaignQuestion"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='true'; document.campaignForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/campaign.do?action=${campaignForm.previousAction}&campaignID=${campaignForm.campaignID}"/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>

