<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
 --%><%@ page language="java" import="org.agnitas.util.*, java.util.*, org.agnitas.web.*" contentType="text/javascript; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn"%>

<agn:CheckLogon />

/*
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2005 Frederico Caldeira Knabben
 *
 * Licensed under the terms of the GNU Lesser General Public License:
 * 		http://www.opensource.org/licenses/lgpl-license.php
 *
 * For further information visit:
 * 		http://www.fckeditor.net/
 *
 * File Name: fckconfig.js
 * 	Editor configuration settings.
 * 	See the documentation for more info.
 *
 * File Authors:
 * 		Frederico Caldeira Knabben (fredck@fckeditor.net)
 */

FCKConfig.EditorAreaCSS = FCKConfig.BasePath + 'css/fck_editorarea.css' ;

FCKConfig.DocType = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">' ;

FCKConfig.FullPage = false ;

FCKConfig.Debug = false ;

FCKConfig.SkinPath = FCKConfig.BasePath + 'skins/default/' ;

FCKConfig.PluginsPath = FCKConfig.BasePath + 'plugins/' ;

FCKConfig.EmmSessionID = "<%= session.getId() %>";

FCKConfig.Plugins.Add( 'emm', 'en,de' ) ;

FCKConfig.ProcessHTMLEntities	= false ;
FCKConfig.StartupFocus	= true ;
FCKConfig.ForcePasteAsPlainText	= false ;
FCKConfig.ForceSimpleAmpersand	= true ;
FCKConfig.TabSpaces		= 0 ;
FCKConfig.ShowBorders	= true ;
FCKConfig.UseBROnCarriageReturn	= false ;
FCKConfig.ToolbarStartExpanded	= true ;
FCKConfig.ToolbarCanCollapse	= true ;
FCKConfig.IEForceVScroll = false ;
FCKConfig.IgnoreEmptyParagraphValue = true ;

FCKConfig.ToolbarSets["emm"] = [
	['Cut','Copy','Paste','PasteText','PasteWord'],
        ['Undo','Redo','-','Find','Replace','-','RemoveFormat'],
        ['TextColor','BGColor'],
	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
	['Image','Table','Rule','SpecialChar', 'My_EMM'],
	['OrderedList','UnorderedList','-','Outdent','Indent'],
	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
	['Link','Unlink','Anchor'],
	['FontName','FontSize']
] ;

FCKConfig.ToolbarSets["Basic"] = [
	['Bold','Italic','-','OrderedList','UnorderedList','-','Link','Unlink','-','About']
] ;

FCKConfig.ContextMenu = ['Generic','Link','Anchor','Image','Flash','Select','Textarea','Checkbox','Radio','TextField','HiddenField','ImageButton','Button','BulletedList','NumberedList','TableCell','Table','Form'] ;

FCKConfig.FontColors = '000000,993300,333300,003300,003366,000080,333399,333333,800000,FF6600,808000,808080,008080,0000FF,666699,808080,FF0000,FF9900,99CC00,339966,33CCCC,3366FF,800080,999999,FF00FF,FFCC00,FFFF00,00FF00,00FFFF,00CCFF,993366,C0C0C0,FF99CC,FFCC99,FFFF99,CCFFCC,CCFFFF,99CCFF,CC99FF,FFFFFF' ;

FCKConfig.FontNames		= 'Arial;Comic Sans MS;Courier New;Tahoma;Times New Roman;Verdana' ;
FCKConfig.FontSizes		= '6/xx-small (6px);8/x-small (8px);10/small (10px);12/medium (12px);16/large (16px);20/x-large (20px);24/xx-large (24px)' ;
FCKConfig.FontFormats	= 'p;div;pre;address;h1;h2;h3;h4;h5;h6' ;

FCKConfig.StylesXmlPath		= FCKConfig.EditorPath + 'fckstyles.xml' ;
FCKConfig.TemplatesXmlPath	= FCKConfig.EditorPath + 'fcktemplates.xml' ;

FCKConfig.SpellChecker			= 'ieSpell' ;	// 'ieSpell' | 'SpellerPages'
FCKConfig.IeSpellDownloadUrl	= 'http://www.iespell.com/rel/ieSpellSetup211325.exe' ;

FCKConfig.MaxUndoLevels = 15 ;

FCKConfig.DisableImageHandles = false ;
FCKConfig.DisableTableHandles = false ;

FCKConfig.LinkDlgHideTarget		= false ;
FCKConfig.LinkDlgHideAdvanced	= false ;

FCKConfig.ImageDlgHideLink		= false ;
FCKConfig.ImageDlgHideAdvanced	= false ;

FCKConfig.FlashDlgHideAdvanced	= false ;

FCKConfig.LinkBrowser = false ;
FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/asp/connector.asp' ;
//FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/asp/connector.asp&ServerPath=/CustomFiles/' ;
// ASP.Net		// FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/aspx/connector.aspx' ;
// ColdFusion	// FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/cfm/connector.cfm' ;
// Perl			// FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/perl/connector.cgi' ;
// PHP			// FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=connectors/php/connector.php' ;
// PHP - mcpuk	// FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/mcpuk/browser.html?Connector=connectors/php/connector.php' ;
FCKConfig.LinkBrowserWindowWidth	= screen.width * 0.7 ;	// 70%
FCKConfig.LinkBrowserWindowHeight	= screen.height * 0.7 ;	// 70%

FCKConfig.ImageBrowser = true ;
<%--FCKConfig.ImageBrowserURL = '<html:rewrite page='<%= "/fckeditor-2.6.6/editor/filemanager/browser/emm/browser.jsp?mailingID="+tmpMailingID %>'/>' ;--%>
// ASP.Net		// FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Image&Connector=connectors/aspx/connector.aspx' ;
// ColdFusion	// FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Image&Connector=connectors/cfm/connector.cfm' ;
// Perl			// FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Image&Connector=connectors/perl/connector.cgi' ;
// PHP			// FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Image&Connector=connectors/php/connector.php' ;
// PHP - mcpuk	// FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/mcpuk/browser.html?Type=Image&Connector=connectors/php/connector.php' ;
FCKConfig.ImageBrowserWindowWidth  = screen.width * 0.7 ;	// 70% ;
FCKConfig.ImageBrowserWindowHeight = screen.height * 0.7 ;	// 70% ;

FCKConfig.FlashBrowser = false ;
FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/asp/connector.asp' ;
// ASP.Net		// FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/aspx/connector.aspx' ;
// ColdFusion	// FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/cfm/connector.cfm' ;
// Perl			// FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/perl/connector.cgi' ;
// PHP			// FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/php/connector.php' ;
// PHP - mcpuk	// FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/mcpuk/browser.html?Type=Flash&Connector=connectors/php/connector.php' ;
FCKConfig.FlashBrowserWindowWidth  = screen.width * 0.7 ;	//70% ;
FCKConfig.FlashBrowserWindowHeight = screen.height * 0.7 ;	//70% ;

FCKConfig.LinkUpload = false ;
FCKConfig.LinkUploadURL = FCKConfig.BasePath + 'filemanager/upload/asp/upload.asp' ;
// PHP // FCKConfig.LinkUploadURL = FCKConfig.BasePath + 'filemanager/upload/php/upload.php' ;
FCKConfig.LinkUploadAllowedExtensions	= "" ;			// empty for all
FCKConfig.LinkUploadDeniedExtensions	= ".(php|php3|php5|phtml|asp|aspx|ascx|jsp|cfm|cfc|pl|bat|exe|dll|reg|cgi)$" ;	// empty for no one

FCKConfig.ImageUpload = false ;
<%--FCKConfig.ImageUploadURL = '<html:rewrite page='<%= "/mcomponents.do?action="+ MailingComponentsAction.ACTION_SAVE_COMPONENTS + "&mailingID="+tmpMailingID %>'/>' ;--%>
// PHP // FCKConfig.ImageUploadURL = FCKConfig.BasePath + 'filemanager/upload/php/upload.php?Type=Image' ;
FCKConfig.ImageUploadAllowedExtensions	= ".(jpg|gif|jpeg|png)$" ;		// empty for all
FCKConfig.ImageUploadDeniedExtensions	= "" ;							// empty for no one

FCKConfig.FlashUpload = false ;
FCKConfig.FlashUploadURL = FCKConfig.BasePath + 'filemanager/upload/asp/upload.asp?Type=Flash' ;
// PHP // FCKConfig.FlashUploadURL = FCKConfig.BasePath + 'filemanager/upload/php/upload.php?Type=Flash' ;
FCKConfig.FlashUploadAllowedExtensions	= ".(swf|fla)$" ;		// empty for all
FCKConfig.FlashUploadDeniedExtensions	= "" ;					// empty for no one

FCKConfig.SmileyPath	= FCKConfig.BasePath + 'images/smiley/msn/' ;
FCKConfig.SmileyImages	= ['regular_smile.gif','sad_smile.gif','wink_smile.gif','teeth_smile.gif','confused_smile.gif','tounge_smile.gif','embaressed_smile.gif','omg_smile.gif','whatchutalkingabout_smile.gif','angry_smile.gif','angel_smile.gif','shades_smile.gif','devil_smile.gif','cry_smile.gif','lightbulb.gif','thumbs_down.gif','thumbs_up.gif','heart.gif','broken_heart.gif','kiss.gif','envelope.gif'] ;
FCKConfig.SmileyColumns = 8 ;
FCKConfig.SmileyWindowWidth		= 320 ;
FCKConfig.SmileyWindowHeight	= 240 ;
FCKConfig.ProtectedSource.Add( /<img[^>]*src="\[agn[^>]*>/gi  );




if( window.console ) window.console.log( 'Config is loaded!' ) ;	// @Packager.Compactor.RemoveLine