<%-- checked --%>
<%@ page language="java"
         import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*, java.text.*, java.sql.*, javax.sql.*, org.springframework.context.*, org.springframework.web.context.support.WebApplicationContextUtils"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<script type="text/javascript" src="${emmLayoutBase.jsURL}/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="${emmLayoutBase.jsURL}/jquery.tablesorter.js"></script>
<script type="text/javascript" src="${emmLayoutBase.jsURL}/empfaengerdetail.js"></script>
<agn:ShowByPermission token="settings.open">
<script type="text/javascript">
    jQuery(document).ready(function($){
          $('.toggle_closed').each(function(){
              $(this).removeClass('toggle_closed');
              $(this).addClass('toggle_open');
              $(this).next().toggle();
          });
    });
</script>
</agn:ShowByPermission>
<html:form action="/recipient">
<html:hidden property="recipientID"/>
<html:hidden property="user_type"/>
<html:hidden property="user_status"/>
<html:hidden property="listID"/>
<html:hidden property="targetID"/>
<html:hidden property="action"/>

<div class="contentbox_left_column">
    <div class="recipient_detail_form_item">
        <label for="empfaenger_detail_anrede"><bean:message key="recipient.Salutation"/>:</label>
        <html:select styleId="empfaenger_detail_anrede" property="gender" size="1">
            <html:option value="0"><bean:message key="recipient.gender.0.short"/></html:option>
            <html:option value="1"><bean:message key="recipient.gender.1.short"/></html:option>
            <agn:ShowByPermission token="recipient.gender.extended">
                <html:option value="3"><bean:message key="recipient.gender.3.short"/></html:option>
                <html:option value="4"><bean:message key="recipient.gender.4.short"/></html:option>
                <html:option value="5"><bean:message key="recipient.gender.5.short"/></html:option>
            </agn:ShowByPermission>
            <html:option value="2"><bean:message key="recipient.gender.2.short"/></html:option>
        </html:select>
    </div>

    <div class="recipient_detail_form_item">
        <label for="empfaenger_detail_title"><bean:message key="recipient.Title"/>:</label>
        <html:text styleId="empfaenger_detail_title" styleClass="empfaenger_detail_input_text" property="title"/>
    </div>
    <div class="recipient_detail_form_item">
        <label for="empfaenger_detail_firstname"><bean:message key="recipient.Firstname"/>:</label>
        <html:text styleId="empfaenger_detail_firstname" styleClass="empfaenger_detail_input_text"
                   property="firstname"/>
    </div>
    <div class="recipient_detail_form_item">
        <label for="empfaenger_detail_lastname"><bean:message key="recipient.Lastname"/>:</label>
        <html:text styleId="empfaenger_detail_lastname" styleClass="empfaenger_detail_input_text" property="lastname"/>
    </div>

    <div class="recipient_detail_form_item">
        <label for="empfaenger_detail_email"><bean:message key="mailing.E-Mail"/>:</label>
        <html:text styleId="empfaenger_detail_email" styleClass="empfaenger_detail_input_text" property="email"/>
    </div>

    <div class="recipient_detail_form_item">
        <label><bean:message key="mailing.Mailtype"/>:</label>

        <div class="recipient_detail_form_item_radio">
            <html:radio styleId="recipient_detail_form_item_radio_0" property="mailtype" value="0"/>
            <label for="recipient_detail_form_item_radio_0">Text</label>
        </div>

        <div class="recipient_detail_form_item_radio">
            <html:radio styleId="recipient_detail_form_item_radio_1" property="mailtype" value="1"/>
            <label for="recipient_detail_form_item_radio_1">HTML</label>
        </div>

        <div class="recipient_detail_form_item_radio">
            <html:radio styleId="recipient_detail_form_item_radio_2" property="mailtype" value="2"/>
            <label for="recipient_detail_form_item_radio_2">Offline - HTML</label>
        </div>

    </div>

    <div class="dotted_line"></div>

    <h3><bean:message key="recipient.More_Profile_Data"/>:</h3>

    <agn:ShowColumnInfo id="agnTbl" table="<%= AgnUtils.getCompanyID(request) %>" hide="bounceload">
        <%
            String colName = (String) pageContext.getAttribute("_agnTbl_column_name");

            if (colName == null) {
                colName = (String) pageContext.getAttribute("_agnTbl_column");
            }

            Set disabled = new HashSet();

            disabled.add("email");
            disabled.add("customer_id");
            disabled.add("title");
            disabled.add("gender");
            disabled.add("mailtype");
            disabled.add("firstname");
            disabled.add("lastname");

            if (!disabled.contains(colName.toLowerCase())) {
                String colDate = new String("column(" + colName + "_DAY_DATE)");
                String colMonth = new String("column(" + colName + "_MONTH_DATE)");
                String colYear = new String("column(" + colName + "_YEAR_DATE)");
                String colLabel = new String("column(" + colName + ")");
        %>
        <c:choose>
            <c:when test="${_agnTbl_data_type == 'DATE'}">
                <c:choose>
                    <c:when test="${_agnTbl_editable == 0}">
                        <div class="recipient_detail_form_item">
                            <label title="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</label>
                            <html:text property="<%= colDate %>" styleClass="empfaenger_detail_day"/>.
                            <html:text property="<%= colMonth %>" styleClass="empfaenger_detail_month"/>.
                            <html:text property="<%= colYear %>" styleClass="empfaenger_detail_year"/>
                        </div>
                    </c:when>
                    <c:when test="${_agnTbl_editable == 1}">
                        <div class="recipient_detail_form_item">
                            <label title="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</label>
                            <html:text property="<%= colDate %>" styleClass="empfaenger_detail_day" readonly="true"/>.
                            <html:text property="<%= colMonth %>" styleClass="empfaenger_detail_month" readonly="true"/>.
                            <html:text property="<%= colYear %>" styleClass="empfaenger_detail_year" readonly="true"/>
                        </div>
                    </c:when>
                    <c:when test="${_agnTbl_editable == 2}">
                        <html:hidden property="<%= colDate %>"/>
                        <html:hidden property="<%= colMonth %>"/>
                        <html:hidden property="<%= colYear %>"/>
                    </c:when>
                </c:choose>
            </c:when>
            <c:when test="${_agnTbl_data_type == 'VARCHAR'}">
                <c:choose>
                    <c:when test="${_agnTbl_editable == 0}">
                        <div class="recipient_detail_form_item">
                            <label title="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</label>
                            <html:text styleClass="empfaenger_detail_input_text" property="<%= colLabel %>"
                                       maxlength='<%= (String) pageContext.getAttribute("_agnTbl_data_length") %>'/>
                        </div>
                    </c:when>
                    <c:when test="${_agnTbl_editable == 1}">
                        <div class="box_form_item recipient_form_item">
                            <label title="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</label>
                            <html:text styleClass="empfaenger_detail_input_text" property="<%= colLabel %>" size="40" readonly="true"/>
                        </div>
                    </c:when>
                    <c:when test="${_agnTbl_editable == 2}">
                        <html:hidden property="<%= colLabel %>"/>
                    </c:when>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${_agnTbl_editable == 0}">
                        <div class="recipient_detail_form_item">
                            <label title="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</label>
                            <html:text styleClass="empfaenger_detail_input_text" property="<%= colLabel %>" />
                        </div>
                    </c:when>
                    <c:when test="${_agnTbl_editable == 1}">
                        <div class="box_form_item recipient_form_item">
                            <label title="<%= (String) pageContext.getAttribute("_agnTbl_shortname") %>"><%= (String) pageContext.getAttribute("_agnTbl_shortname") %>:&nbsp;</label>
                            <html:text styleClass="empfaenger_detail_input_text" property="<%= colLabel %>" size="40" readonly="true"/>
                        </div>
                    </c:when>
                    <c:when test="${_agnTbl_editable == 2}">
                        <html:hidden property="<%= colLabel %>"/>
                    </c:when>
                </c:choose>
            </c:otherwise>
        </c:choose>
            <% } %>
    </agn:ShowColumnInfo>
</div>

<div class="contentbox_right_column">
    <h3><bean:message key="recipient.Mailinglists"/>:</h3>

    <%
        RecipientForm recipient = (RecipientForm) request.getAttribute("recipient");
        Recipient cust = (Recipient) request.getAttribute("cust");
        BindingEntry tmpStatusEntry = null;
        int tmpUserStatus;
        String tmpUserType = null;
        String tmpUserRemark = null;
        java.util.Date tmpUserDate = null;
        DateFormat aFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        boolean aMType = false;
        int k = 0;

// just for debugging:
// please clean me up asap:
        Map MTL = new HashMap();
        Integer mailingListId;

// for agn:ShowByPermission keys
        String[] ES = {"email"};

        cust.setCompanyID(AgnUtils.getCompanyID(request));
        cust.setCustomerID(recipient.getRecipientID());

        Map allCustLists = cust.getAllMailingLists();
    %>




    <c:forEach var="mailinglist" items="${mailinglists}">

        <div class="recipient_detail_mailinglist_container recipient_detail_mailinglist_nowidelayout">
            <div class="recipient_detail_mailinglist_toggle toggle_closed"><a
                    href="#${mailinglist.shortname}">${mailinglist.shortname}</a></div>
            <div class="recipient_detail_mailinglist_content">

                <%
                    mailingListId = ((Mailinglist) pageContext.getAttribute("mailinglist")).getId();
                    if (allCustLists.get(mailingListId) != null) {
                        MTL = (Map) (allCustLists.get(mailingListId));
                    } else {
                        MTL = new HashMap();
                        allCustLists.put(mailingListId, MTL);
                    }

                    for (k = 0; k < ES.length; k++) {
                        if (ES[k] == null) {
                            continue;
                        }
                        tmpStatusEntry = ((BindingEntry) (MTL.get(new Integer(k))));
                        if (tmpStatusEntry == null) {
                            tmpStatusEntry = new org.agnitas.beans.impl.BindingEntryImpl();
                            tmpStatusEntry.setCustomerID(recipient.getRecipientID());
                            tmpStatusEntry.setMailinglistID(mailingListId);
                        }
                        tmpUserType = tmpStatusEntry.getUserType();
                        tmpUserStatus = tmpStatusEntry.getUserStatus();
                        tmpUserRemark = tmpStatusEntry.getUserRemark();
                        tmpUserDate = tmpStatusEntry.getChangeDate();
                        int mti = ((Mailinglist) pageContext.getAttribute("mailinglist")).getId();
                        recipient.setBindingEntry(mti, tmpStatusEntry);
                %>

                <div class="recipient_detail_mailinglist_content_form_item">

                    <div class="recipient_detail_mailinglist_status">
                        <html:checkbox styleClass="recipient_detail_mailinglist_checkbox"
                                       styleId="recipient_detail_mailinglist_checkbox_email${mailinglist.id}"
                                       property='bindingEntry[${mailinglist.id}].userStatus' value="1"/>
                        <input type="hidden"
                               name='<%= "__STRUTS_CHECKBOX_bindingEntry["+mti+"].userStatus" %>'
                               value="<%= ((tmpUserStatus == BindingEntry.USER_STATUS_ACTIVE)?3:tmpUserStatus) %>">

                        <label class="recipient_detail_mailinglist_content_form_item_label"
                               for="recipient_detail_mailinglist_checkbox_email${mailinglist.id}">
                            <bean:message key='<%= new String(\"mailing.MediaType.\"+k) %>'/>
                        </label>

                        <br>
                        <div class="recipient_detail_mailinglist_status">
                            <div class="recipient_detail_mailinglist_status_container">
                                <label class="recipient_detail_mailinglist_remark_text">
                                    <bean:message key="recipient.Status"/>:&nbsp;</label>
                                <% if (tmpUserStatus > 0 && tmpUserStatus <= 7) { %>
                                <label class="recipient_detail_mailinglist_remark_text"><bean:message
                                        key='<%= "recipient.MailingState"+tmpUserStatus %>'/></label>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <div class="recipient_detail_mailinglist_remark">
                        <html:select property='<%= \"bindingEntry[\"+mti+\"].userType\"%>' size="1">
                            <html:option value="A"><bean:message key="recipient.Administrator"/></html:option>
                            <html:option value="T"><bean:message key="recipient.TestSubscriber"/></html:option>
                            <html:option value="W"><bean:message key="recipient.NormalSubscriber"/></html:option>
                        </html:select>

                        <br>

                        <div class="recipient_detail_mailinglist_remark_container">
                            <label class="recipient_detail_mailinglist_remark_text">&nbsp;&nbsp;<bean:message
                                    key="recipient.Remark"/>:&nbsp;<%= tmpUserRemark %>
                            </label>
                            <% if (tmpUserDate != null) { %>
                            <br><label class="recipient_detail_mailinglist_remark_text">
                            &nbsp;&nbsp;<%= aFormat.format(tmpUserDate) %>
                        </label>
                            <% } %>
                        </div>
                    </div>

                </div>
                <%
                    }
                %>

            </div>
        </div>
    </c:forEach>

</div>

<div class="recipient_detail_button_container">
    <agn:ShowByPermission token="recipient.change">
        <input type="hidden" id="name" name="save" value=""/>

        <div class="action_button"><a href="#"
                                          onclick="document.recipientForm.save.value='save'; document.recipientForm.submit();return false;"><span><bean:message
                key="button.Save"/></span></a></div>
    </agn:ShowByPermission>

    <agn:ShowByPermission token="recipient.delete">
        <c:if test="${recipientForm.recipientID != 0}">
            <div class="action_button">
                <a href="<html:rewrite page='<%= new String("/recipient.do?action=" + RecipientAction.ACTION_CONFIRM_DELETE+ "&recipientID="+ recipient.getRecipientID() +"&fromListPage=false")%>'/>">
                    <span><bean:message key="button.Delete"/></span>
                </a>
            </div>
        </c:if>
    </agn:ShowByPermission>

    <div class="action_button">
        <a href="<html:rewrite page='<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&overview=true&user_type=" +  recipient.getUser_type()  + "&user_status=" + recipient.getUser_status() + "&listID=" + recipient.getListID() + "&targetID=" + recipient.getTargetID())%>'/>">
            <span><bean:message key="button.Cancel"/></span>
        </a>
    </div>

    <div class="action_button"><bean:message key="Recipient"/>:</div>
</div>

</html:form>
