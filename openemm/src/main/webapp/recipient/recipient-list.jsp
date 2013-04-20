<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils"
         buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="Language" value="<%= ((Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)).getLanguage()%>" scope="page"/>

<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('recipientForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'recipient';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;


    Event.observe(window, 'load', function() {
        <agn:ShowByPermission token="settings.open">
                var closed = document.getElementsByClassName('toggle_closed');
                if(closed)
                for(var i=0;i<closed.length;i++){
                    closed[i].addClassName('toggle_open');
                    closed[i].next().show();
                    closed[i].removeClassName('toggle_closed');
                }
        </agn:ShowByPermission>
        <agn:HideByPermission token="settings.open">
            <c:if test="${not recipientForm.advancedSearchVisible}">
                $$('.advanced_search_filter_container').invoke('hide');
            </c:if>
            <c:if test="${not recipientForm.recipientFieldsVisible}">
            	$$('.recipient_fields_select_container').invoke('hide');
        	</c:if>
        </agn:HideByPermission>
        onPageLoad();
        <c:if test="${recipientForm.deactivatePagination}">
             var arrPagingBlock = document.getElementsByClassName('list_result_paging');
                 arrPagingBlock[0].style.visibility = 'hidden';
                 arrPagingBlock[1].style.visibility = 'hidden';
        </c:if>
        document.getElementById("field_0").checked = true;
    });

    function toggleContainer(container, name, className){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        if( document.recipientForm[name].value == 'true') {
            document.recipientForm[name].value = 'false';
        }
        else {
            document.recipientForm[name].value = 'true';
        }
        $(container).next(className).toggle();
    }

    function moveDualList(srcList, destList, moveAll, isAdd) {
        // Do nothing if nothing is selected
        if (srcList.selectedIndex == -1 && !moveAll) {
            return;
        }
        // create array with initial options
        newDestList = new Array(destList.options.length);
        var len = 0;
        for (len = 0; len < destList.options.length; len++) {
            if (destList.options[ len ] != null) {
                newDestList[ len ] = new Option(destList.options[len].text, destList.options[ len ].value, false, false);
            }
        }

        // copy new options to array
        for (var i = 0; i < srcList.options.length; i++) {
            if (srcList.options[i] != null && ( srcList.options[i].selected || moveAll )) {
                // Statements to perform if option is selected

                // Incorporate into new list
                newDestList[ len ] = new Option(srcList.options[i].text, srcList.options[i].value, false, false);
                if (isAdd) {
                    addField(srcList.options[i].value);
                } else {
                    removeField(srcList.options[i].value);
                }
                len++;
            }
        }

        // Populate the destination with the items from the new array
        for (var j = 0; j < newDestList.length; j++) {
            if (newDestList[j] != null) {
                destList.options[j] = newDestList[j];
            }
        }

        // Erase source list selected elements
        for (i = srcList.options.length - 1; i >= 0; i--) {
            if (srcList.options[i] != null && (srcList.options[i].selected || moveAll)) {
                // Erase Source
                srcList.options[i] = null;
            }
        }
    }

    function removeField(field) {
        document.getElementById("field_" + field).checked = false;
    }

    function addField(field) {
    	document.getElementById("field_" + field).checked = true;
    }
</script>

<agn:ShowColumnInfo id="colsel" table="<%=AgnUtils.getCompanyID(request)%>"/>


<html:form action="/recipient.do?action=${ACTION_LIST}" styleId="filterForm">
    <html:hidden property="numberOfRowsChanged"/>
    <html:hidden property="advancedSearchVisible"/>
    <html:hidden property="overview" value="true"/>
 	<html:hidden property="recipientFieldsVisible"/>

    <div id="filterbox_container">
        <c:if test="${Language == 'de'}">
        	<div class="search_label search_label_de"></div>
    	</c:if>
    	<c:if test="${Language != 'de'}">
        	<div class="search_label search_label_en"></div>
    	</c:if>
        <div class="filterbox_form_container">

            <div id="filterbox_top"></div>
            <div id="searchbox_content">
                <div class="search_columns_wrapper">
                    <div class="search_column1">
                        <label><bean:message key="Mailinglist"/>:</label>

                        <html:select styleClass="search_select_short" property="listID" onchange="parametersChanged()">
                            <html:option value="0" key="statistic.All_Mailinglists"/>
                            <c:forEach var="mailinglist" items="${mailinglists}">
                                <html:option value="${mailinglist.id}">
                                    ${mailinglist.shortname}
                                </html:option>
                            </c:forEach>
                        </html:select>
                    </div>

                    <div class="search_column2">
                        <label><bean:message key="target.Target"/>:</label>

                        <html:select styleClass="search_select_short" property="targetID"
                                     onchange="parametersChanged()">
                            <html:option value="0" key="default.All"/>
                            <c:forEach var="target" items="${targets}">
                                <html:option value="${target.id}">
                                    ${target.targetName}
                                </html:option>
                            </c:forEach>
                        </html:select>

                    </div>
                    <div class="search_column4">
                        <label><bean:message key="recipient.RecipientType"/>:</label>

                        <html:select styleClass="search_select_short" property="user_type"
                                     onchange="parametersChanged()">
                            <!-- usr type; 'E' for everybody -->
                            <html:option value="E" key="default.All"/>
                            <html:option value="A" key="recipient.Administrator"/>
                            <html:option value="T" key="recipient.TestSubscriber"/>
                            <html:option value="W" key="recipient.NormalSubscriber"/>
                        </html:select>

                    </div>
                    <div class="search_column5">
                        <label><bean:message key="recipient.RecipientStatus"/>:</label>
                        <html:select styleClass="search_select_short" property="user_status"
                                     onchange="parametersChanged()">
                            <!-- usr status; '0' is for everybody -->
                            <html:option value="0" key="default.All"/>
                            <html:option value="1" key="recipient.Active"/>
                            <html:option value="2" key="recipient.Bounced"/>
                            <html:option value="3" key="recipient.OptOutAdmin"/>
                            <html:option value="4" key="recipient.OptOutUser"/>
                            <html:option value="5" key="recipient.MailingState5"/>
                            <html:option value="6" key="recipient.MailingState6"/>
                            <html:option value="7" key="recipient.MailingState7"/>
                        </html:select>

                    </div>
                </div>
                <div class="filter_button_wrapper">
                    <div class="filterbox_form_button filterbox_form_button_right_corner"><a href="#" onclick="document.recipientForm.submit();">
                        <span><bean:message key="button.OK"/></span></a>
                    </div>
                </div>

            </div>

            <div id="filterbox_bottom"></div>

            <div id="advanced_search_top"></div>
            <div id="advanced_search_content">
                <div class="advanced_search_toggle toggle_closed"
                    onclick="toggleContainer(this, 'advancedSearchVisible', '.advanced_search_filter_container');"><a href="#"><bean:message
                        key="recipient.AdvancedSearch"/></a></div>
                <div class="info_bubble_container">
                    <div id="advancedsearch" class="info_bubble">
                        &nbsp;
                    </div>
                </div>
                <div class="advanced_search_filter_container">
                    <div class="advanced_search_filter">
                        <div class="advanced_search_filter_list">
                            <table border="0" cellspacing="2" cellpadding="0">
                                <c:set var="FORM_NAME" value="recipientForm" scope="page"/>
                                <%@include file="/rules/rule_add.jsp" %>
                            </table>
                        </div>
                    </div>
                    <div class="advanced_search_filter_list">
                        <table border="0" cellspacing="2" cellpadding="0">
                            <%@include file="/rules/rules_list.jsp" %>
                        </table>

                        <div class="advanced_search_filter_button">
                            <input type="hidden" id="Update" name="Update" value=""/>

                            <div class="filterbox_form_button"><a href="#"
                                                                  onclick="parametersChanged(); document.getElementById('Update').value='Update'; document.recipientForm.submit(); return false;"><span><bean:message
                                    key="settings.Update"/></span></a></div>
                        </div>
                    </div>

                </div>
            </div>
            <div id="advanced_search_bottom"></div>

           	<agn:ShowByPermission token="recipient.column.select">
            <div id="recipient_fields_top"></div>
        	<div id="recipient_fields_content">
            	<div class="recipient_fields_toggle toggle_closed" onclick="toggleContainer(this, 'recipientFieldsVisible', '.recipient_fields_select_container')"><a href="#"><bean:message key="recipient.select.fields"/></a></div>
				<div class="recipient_fields_select_container">
					<div class="assistant_step7_form_item">
			            <label><bean:message key="recipient.fields"/>:</label>
			            <div class="target_dualbox_list_container">
			                <bean:message key="recipient.fields.avaliable"/>:<br>
			                <select id="availableFields" name="fieldName" size="6" multiple="multiple">
			                    <c:forEach var="field" items="${fieldsMap}" varStatus="rowCounter">
			                    	<c:set var="fieldSelected" value="${false}"/>
			                    	<c:forEach var="selectedField" items="${recipientForm.selectedFields}" varStatus="rowCounter">
                            			<c:if test="${field.key == selectedField}">
                               				 <c:set var="fieldSelected" value="${true}"/>
                            			</c:if>
                            		</c:forEach>
                            		<c:if test="${!fieldSelected and field.key!='email' and field.key!='EMAIL' and field.key!='customer_id' and field.key!='CUSTOMER_ID'}">
                           				<option title="${field.key}" value="${field.key}"><c:out value="${field.value}"/></option>
                        			</c:if>
			                    </c:forEach>
			                </select>
			            </div>

			            <input type="hidden" name="add_action" value=""/>

			            <div class="target_dualbox_buttons_container">
			                <div class="target_dualbox_single_button_container">
			                    <a href="#" onclick="moveDualList(document.forms[0].fieldName, document.forms[0].selectedFieldName, false, true); return false;" class="target-group-add-button">
			                        <bean:message key="Add"/>
			                    </a>
			                </div>
			                <div class="target_dualbox_buttons_separator">
			                    <a href="#" onclick="moveDualList(document.forms[0].fieldName, document.forms[0].selectedFieldName, true, true); return false;" class="target-group-add-all-button">
			                        <bean:message key="AddAll"/>
			                    </a>
			                </div>
			                <div class="target_dualbox_single_button_container">
			                    <a href="#" onclick="moveDualList(document.forms[0].selectedFieldName, document.forms[0].fieldName, false, false); return false;" class="target-group-remove-button">
			                        <bean:message key="Remove"/>
			                    </a>
			                </div>
			                <div class="target_dualbox_single_button_container">
			                    <a href="#" onclick="moveDualList(document.forms[0].selectedFieldName, document.forms[0].fieldName, true, false); return false;" class="target-group-remove-all-button">
			                        <bean:message key="RemoveAll"/>
			                    </a>
			                </div>
			            </div>

			            <div class="target_dualbox_list_container">
			                <bean:message key="recipient.fields.selected"/>*:<br>
			                <select id="chosenFileds" name="selectedFieldName" size="6" multiple="multiple">
			                    <c:forEach var="selectedField" items="${recipientForm.selectedFields}" varStatus="rowCounter">
			                    	<c:forEach var="field" items="${fieldsMap}" varStatus="rowCounter">
										<c:if test="${field.key == selectedField}">
			                                <option title="${field.key}" value="${field.key}"><c:out value="${field.value}"/></option>
			                            </c:if>
			                        </c:forEach>
			                    </c:forEach>
			                </select><br clear="both"/>
                            *&nbsp;<bean:message key="recipient.fields.gender.as.salutation"/>
            			</div>
	        		</div>

	        		<div class="filterbox_form_button filterbox_form_button_right_corner">
	            		<a href="#"	onclick="document.recipientForm.submit(); return false;">
	                		<span><bean:message key="button.Show"/></span>
	            		</a>
	        		</div>
	        	</div>
        		<div id="recipient_fields_bottom"></div>
    		</div>
    		</agn:ShowByPermission>
		</div>
	</div>

	<div id="fieldspopup" style="visibility: hidden; height:0px;">
    	<c:forEach var="field" items="${fieldsMap}" varStatus="rowCounter">
        	<html:multibox styleId="field_${field.key}" property="selectedFields" style="visibility: hidden;" value="${field.key}">
        		<c:out value="${field.key}"/>
        	</html:multibox>
        	<c:out value="${field.key}"/>
    	</c:forEach>
        <html:multibox styleId="field_0" property="selectedFields" style="visibility: hidden;" value="${DUMMY_RECIPIENT_FIELD}" >
            <c:out value="${DUMMY_RECIPIENT_FIELD}"/>
        </html:multibox>
        <c:out value="null_field"/>
	</div>

    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#" onclick="parametersChanged(); document.recipientForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${recipientForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

</html:form>

<c:if test="${recipientForm.overview}">
<display:table class="list_table" id="recipient" name="recipientList" sort="external" requestURI="/recipient.do?action=${ACTION_LIST}&__fromdisplaytag=true" partialList="true"  size="${recipientForm.numberofRows}" excludedParams="*" >
    <c:if test="${not empty recipient}">
    <c:forEach var="selectedField" items="${recipientForm.selectedFields}" varStatus="rowCounter">
    	<c:forEach var="field" items="${fieldsMap}" varStatus="rowCounter">
			<c:if test="${field.key == selectedField}">
				<c:choose>
					<c:when test="${field.key == 'gender' or field.key == 'GENDER'}">
						<c:set var="gender"><bean:write name="recipient" property="gender"/></c:set>
						<display:column class="recipient_title" headerClass="recipient_title_head header" titleKey="Salutation" sortable="true" sortProperty="gender">
        					<bean:message key="recipient.gender.${gender}.short"/>
    					</display:column>
					</c:when>
					<c:when test="${field.key == 'firstname' or field.key == 'FIRSTNAME'}">
						<display:column class="recipient_firstname" headerClass="recipient_firstname_head header" titleKey="recipient.Firstname" sortable="true" sortProperty="firstname">
                            <span class="ie7hack">
                                <c:set var="firstname"><bean:write name="recipient" property="firstname"/></c:set>
                                ${firstname}
                            </span>
                        </display:column>
					</c:when>
					<c:when test="${field.key == 'lastname' or field.key == 'LASTNAME'}">
						<display:column class="recipient_lastname" headerClass="recipient_lastname_head header" titleKey="recipient.Lastname" sortable="true" sortProperty="lastname">
                            <span class="ie7hack">
                                <c:set var="lastname"><bean:write name="recipient" property="lastname"/></c:set>
                                ${lastname}
                            </span>
                        </display:column>
					</c:when>
					<c:otherwise>
						<display:column class="recipient_title" headerClass="recipient_title_head header"  title="${field.value}" property="${field.key}" sortable="true" sortProperty="${field.key}"/>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</c:forEach>

    <display:column class="recipient_email" headerClass="recipient_email_head header" property="email" titleKey="mailing.E-Mail" sortable="true" paramId="recipientID" paramProperty="customer_id" url="/recipient.do?action=${ACTION_VIEW}"/>
    <display:column class="edit">
    	<c:set var="customer_id"><bean:write name="recipient" property="customer_id"/></c:set>
        <agn:ShowByPermission token="recipient.view">
             <html:link styleClass="mailing_edit" titleKey="recipient.RecipientEdit" page="/recipient.do?action=${ACTION_VIEW}&recipientID=${customer_id}"></html:link>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="recipient.delete">
             <html:link styleClass="mailing_delete" titleKey="recipient.RecipientDelete" page="/recipient.do?action=${ACTION_CONFIRM_DELETE}&recipientID=${customer_id}&fromListPage=true"></html:link>
        </agn:ShowByPermission>
    </display:column>
    </c:if>
</display:table>
</c:if>

<script type="text/javascript">
    table = document.getElementById('recipient');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#recipient tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>


<script language="javascript">

    if ($('advancedsearch')) {
        var helpBalloonAdvancedsearch = new HelpBalloon({
            dataURL: 'help_${helplanguage}/recipient/AdvancedSearchMsg.xml'
        });
        $('advancedsearch').insertBefore(helpBalloonAdvancedsearch.icon, $('advancedsearch').childNodes[0]);
    }

    /*addTitleToOptions();*/

</script>
