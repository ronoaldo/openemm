<%-- checked --%>
<%@ page language="java" import="org.agnitas.util.AgnUtils, java.util.*" contentType="text/html; charset=utf-8"
         buffer="64kb" errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<script language="javascript">

    function enableCategory(form, id, value) {
        var name = "user_right";

        if (id != 0) {
            name += id;
        }
        form = document.getElementById(form);
        //elem=document.getElementById('Template.template.delete');
        for (c = 0; c < form.elements.length; c++) {
            elem = form.elements[c];
            if (elem.name.substr(0, name.length) == name) {
                if (!elem.disabled) {
                    elem.checked = value;
                }
            }
        }
        return false;
    }

    function toggleContainer(container) {
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');

        $(container).next().toggle();
    }

    function expandAll() {
        var categories = document.getElementsByClassName('expand_grey_box_top');
        for (var i = 0; i < categories.length; i++) {
            var category = categories[i];
            if (hasClassName(category, 'toggle_closed'))
                toggleContainer(category);
        }
    }

    function collapseAll() {
        var categories = document.getElementsByClassName('expand_grey_box_top');
        for (var i = 0; i < categories.length; i++) {
            var category = categories[i];
            if (hasClassName(category, 'toggle_open'))
                toggleContainer(category);
        }
    }

    function hasClassName(objElement, strClass) {
        // if there is a class
        if (objElement.className) {
            // the classes are just a space separated list, so first get the list
            var arrList = objElement.className.split(' ');

            // get uppercase class for comparison purposes
            var strClassUpper = strClass.toUpperCase();

            // find all instances and remove them
            for (var i = 0; i < arrList.length; i++) {
                // if class found
                if (arrList[i].toUpperCase() == strClassUpper) {
                    // we found it
                    return true;
                }
            }
        }
        // if we got here then the class name is not there
        return false;
    }

    <agn:ShowByPermission token="settings.open">
        Event.observe(window, 'load', function() {
            expandAll();
        });
    </agn:ShowByPermission>
</script>

<html:form styleId="PermissionForm" action="admin">
    <html:hidden property="adminID"/>
    <html:hidden property="companyID"/>
    <html:hidden property="action"/>
    <% String aCategory = new String("");
        boolean isFirst = true;
        LinkedList keyList = new LinkedList();
        ResourceBundle res = ResourceBundle.getBundle("messages");
        Enumeration aEnum = res.getKeys();
        while (aEnum.hasMoreElements()) {
            Object nextElement = aEnum.nextElement();
            if (nextElement instanceof String && ((String)nextElement).startsWith("UserRight.")) {
                keyList.add(nextElement);
            }
        }

        Collections.sort(keyList);
        LinkedList sortedKeyList = new LinkedList();
        String[] orderedKeys = new String[]{"Mailing", "ContentManagementSystem", "Template", "Campaigns", "Subscriber-Editor", "Import", "Target-Groups",
                "Mailinglist", "Forms", "Actions", "General", "Admin", "Charsets"};
        for (int i = 0; i < orderedKeys.length; i++) {
            String orderedKey = orderedKeys[i];
            for (Object currentKey : keyList) {
                if (((String)currentKey).startsWith("UserRight." + orderedKey + ".")){
                    sortedKeyList.add(currentKey);
                }
            }
        }
        keyList.removeAll(sortedKeyList);
        sortedKeyList.addAll(keyList);
        String tmpKey = null;
        String tmpCategory = new String("");
        String tmpValue = null;
        int categoryId = 9;

        int i = 1;
        Set userrights = (Set) request.getAttribute("userrights");
        Set grouprights = (Set) request.getAttribute("grouprights");

    %>

    <div class="admin_rights_control_panel">
        <h3 class="header_coloured">
            <div class="float_left">
                <bean:message key="default.All"/>&nbsp;[<a href=""
                                                           onclick="return enableCategory('PermissionForm', 0, true);"><bean:message
                    key="settings.admin.enable_group"/></a>/<a href=""
                                                               onclick="return enableCategory('PermissionForm', 0, false);"><bean:message
                    key="settings.admin.disable_group"/></a>]
            </div>
            <div class="float_right">
                <a class="blue_link" href="#" onclick="expandAll(); return false;"><bean:message
                        key="settings.admin.expand_all"/></a>&nbsp;/&nbsp;<a class="blue_link" href="#"
                                                                             onclick="collapseAll(); return false;"><bean:message
                    key="settings.admin.collapse_all"/></a>
            </div>
        </h3>
    </div>

    <%
        for (int j = 0; j < sortedKeyList.size(); j++) {
            tmpKey = (String) sortedKeyList.get(j);
            if (tmpKey.startsWith("UserRight.")) {
                tmpKey = tmpKey.substring(10);
                tmpCategory = tmpKey.substring(0, tmpKey.indexOf('.'));
                tmpKey = tmpKey.substring(tmpKey.indexOf('.') + 1);
                if (AgnUtils.allowed(tmpKey, request)) {
                    if (!userrights.contains(tmpKey)) {
                        tmpValue = new String("user");
                    } else {
                        tmpValue = new String("");
                    }
                    if (!aCategory.equals(tmpCategory)) {
                        aCategory = new String(tmpCategory);
                        categoryId++;
    %>
    <% if (!isFirst) { %>
    </div></div><div class="expand_grey_box_bottom"></div></div></div>
    <% } %>
    <tr>
    <td><% if (!isFirst) { %>

    <% } else {
        isFirst = false;
    } %>

    <div class="expand_grey_box_container">
        <div class="expand_grey_box_top toggle_closed" onclick="toggleContainer(this);">
            <div class="expand_grey_box_top_subcontainer">
                <a href="#<%= aCategory %>"><span
                        class="head3"><bean:message key="<%= aCategory %>"/></span></a>&nbsp;[<a href="#<%= aCategory %>"
                                                                                                 onclick=" enableCategory('PermissionForm', <%= categoryId %>, true); event.stopPropagation();"
                                                                                                 style="background:none; padding-left:0px;"><bean:message
                    key="settings.admin.enable_group"/></a>/<a href="#<%= aCategory %>"
                                                               onclick="enableCategory('PermissionForm', <%= categoryId %>, false); event.stopPropagation();"
                                                               style="background:none;padding-left:0px;"><bean:message
                    key="settings.admin.disable_group"/></a>]:
            </div>
        </div>
        <div style="display:none;">
            <div class="expand_grey_box_content">
                <div style="margin-left: 10px">
                    <% } %>
                    <% if (!grouprights.contains(tmpKey)) { %>
                    <input type="checkbox" id='<%= aCategory+"."+tmpKey %>' name="user_right<%= categoryId %><%= i++ %>"
                           value="user__<%= tmpKey %>"<% if(userrights.contains(tmpKey)) { %>
                           checked <% } %>>&nbsp;<bean:message
                        key='<%= new String("UserRight."+tmpCategory+"."+tmpKey) %>'/>&nbsp;&nbsp;<br>
                    <% } else { %>
                    <input type="checkbox" name="user_right<%= i++ %>" value="group__<%= tmpKey %>" checked
                           disabled>&nbsp;<bean:message key='<%= new String("UserRight."+tmpCategory+"."+tmpKey) %>'/>&nbsp;&nbsp;<br>
                    <% } %>
                    <% }
                    }
                    }
                    %>
                </div>
            </div>
            <div class="expand_grey_box_bottom"></div>
        </div>
    </div>

    <agn:ShowByPermission token="admin.change">
        <input type="hidden" name="save" value=""/>

        <div class="button_container" style="padding-top:5px;">
            <div class="action_button" id="save"><a href="#"
                                                        onclick=" document.adminForm.save.value='save'; document.adminForm.submit();return false;"><span><bean:message
                    key="button.Save"/></span></a></div>
        </div>
    </agn:ShowByPermission>
</html:form>

        
