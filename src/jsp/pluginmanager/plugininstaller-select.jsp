<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html:form action="/pluginInstallerUpload?action=upload" enctype="multipart/form-data">
    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div>
                <bean:message key="pluginmanager.installer.select_file"/>
                <html:file property="pluginFile"/>
            </div>
            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <a href="#" onclick="document.pluginInstallerSelectForm.submit(); return false;">
                        <span><bean:message key="button.Upload"/></span>
                    </a>
                </div>
            </div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>
</html:form>