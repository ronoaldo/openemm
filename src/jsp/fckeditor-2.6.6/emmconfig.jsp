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

<% int tmpMailingID=Integer.parseInt(request.getParameter("mailingID"));%>

<!-- EDITOR BEHAVIOUR -->

<!-- true shows extra "Paste from Word" window (IE only) -->
FCKConfig.AutoDetectPasteFromWord = true ;

<!-- true keeps HTML structure after paste from Word -->
FCKConfig.CleanWordKeepsStructure = true ;

FCKConfig.DocType = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">' ;

<!-- true removes all formatting info -->
FCKConfig.ForcePasteAsPlainText	= false ;

<!-- true inserts html, head and body tags -->
FCKConfig.FullPage = false ;

<!-- cursor focus in editing area -->
FCKConfig.StartupFocus	= true ;

FCKConfig.MaxUndoLevels = 15 ;


<!-- STYLES -->

<!-- CSS for FCKEditor -->
FCKConfig.EditorAreaCSS = FCKConfig.BasePath + 'css/fck_editorarea.css' ;

FCKConfig.FontColors = '000000,993300,333300,003300,003366,000080,333399,333333,800000,FF6600,808000,808080,008080,0000FF,666699,808080,FF0000,FF9900,99CC00,339966,33CCCC,3366FF,800080,999999,FF00FF,FFCC00,FFFF00,00FF00,00FFFF,00CCFF,993366,C0C0C0,FF99CC,FFCC99,FFFF99,CCFFCC,CCFFFF,99CCFF,CC99FF,FFFFFF' ;
FCKConfig.FontFormats	= 'p;div;pre;address;h1;h2;h3;h4;h5;h6' ;
FCKConfig.FontNames		= 'Arial;Comic Sans MS;Courier New;Tahoma;Times New Roman;Verdana' ;
FCKConfig.FontSizes		= '6/xx-small (6px);8/x-small (8px);10/small (10px);12/medium (12px);16/large (16px);20/x-large (20px);24/xx-large (24px)' ;

FCKConfig.StylesXmlPath		= FCKConfig.EditorPath + 'fckstyles.xml' ;


<!-- HTML OUTPUT -->
FCKConfig.EnterMode = 'br' ;
FCKConfig.ShiftEnterMode = 'br' ;

<!-- true fills empty blocks with '&nbsp;' -->
FCKConfig.FillEmptyBlocks = true ;

<!-- true outputs '&' instead of '&amp;' -->
FCKConfig.ForceSimpleAmpersand	= true ;

<!-- true inserts line breaks after certain tags and indents -->
FCKConfig.FormatOutput = true ;

<!-- true formats the source code view nicely (disabled in EMM because of extra HTML code tab) -->
FCKConfig.FormatSource = true ;

<!-- true converts special characters to HTML entities -->
FCKConfig.ProcessHTMLEntities = true ;

<!-- true shows border for tables with border=0 -->
FCKConfig.ShowBorders = true ;

<!-- number of '&nbsp' for a tab -->
FCKConfig.TabSpaces	= 0 ;


<!-- USER INTERFACE -->

<!-- options of context menu -->
FCKConfig.ContextMenu = ['Generic','Link','Anchor','Image','Flash','Select','Textarea','Checkbox','Radio','TextField','HiddenField','ImageButton','Button','BulletedList','NumberedList','TableCell','Table','Form'] ;

<!-- do not hide Advanced tab in flash properties window -->
FCKConfig.FlashDlgHideAdvanced = false ;

<!-- do not hide Link tab in image properties window -->
FCKConfig.ImageDlgHideLink = false ;

<!-- do not hide Advanced tab in image properties window -->
FCKConfig.ImageDlgHideAdvanced = false ;

<!-- do not hide Advanced tab in link dialog window -->
FCKConfig.LinkDlgHideAdvanced = false ;

<!-- do not hide Target tab in Link dialog window -->
FCKConfig.LinkDlgHideTarget = false ;

FCKConfig.SkinPath = FCKConfig.BasePath + 'skins/default/' ;

FCKConfig.SmileyColumns = 8 ;
FCKConfig.SmileyWindowWidth = 320 ;
FCKConfig.SmileyWindowHeight = 240 ;

<!-- toolbar can be hidden -->
FCKConfig.ToolbarCanCollapse = true ;

<!-- toolbar is displayed at startup -->
FCKConfig.ToolbarStartExpanded	= true ;

<!--  features available in toolbar "emm" -->
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

<!--  features available in toolbar "Basic" -->
FCKConfig.ToolbarSets["Basic"] = [
	['Bold','Italic','-','OrderedList','UnorderedList','-','Link','Unlink','-','About']
] ;

<!-- features available in toolbar "Default"
FCKConfig.ToolbarSets["Default"] = [
['Source','DocProps','-','Save','NewPage','Preview','-','Templates'],
['Cut','Copy','Paste','PasteText','PasteWord','-','Print','SpellCheck'],
['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],
'/',
['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
['OrderedList','UnorderedList','-','Outdent','Indent','Blockquote'],
['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
['Link','Unlink','Anchor'],
['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak'],
'/',
['Style','FontFormat','FontName','FontSize'],
['TextColor','BGColor'],
['FitWindow','ShowBlocks','-','About'] // No comma for the last row.
] ;
//-->


<!-- ADVANCED -->
FCKConfig.Debug = false ;

FCKConfig.PluginsPath = FCKConfig.BasePath + 'plugins/' ;
FCKConfig.Plugins.Add( 'emm', 'en,de' ) ;

<!-- protect source code matching regexp by hiding -->
FCKConfig.ProtectedSource.Add( /<img[^>]*src="\[agn[^>]*>/gi  );

FCKConfig.SmileyImages	= ['regular_smile.gif','sad_smile.gif','wink_smile.gif','teeth_smile.gif','confused_smile.gif','tounge_smile.gif','embaressed_smile.gif','omg_smile.gif','whatchutalkingabout_smile.gif','angry_smile.gif','angel_smile.gif','shades_smile.gif','devil_smile.gif','cry_smile.gif','lightbulb.gif','thumbs_down.gif','thumbs_up.gif','heart.gif','broken_heart.gif','kiss.gif','envelope.gif'] ;
FCKConfig.SmileyPath	= FCKConfig.BasePath + 'images/smiley/msn/' ;

<!-- use open source (LGPL) spell checker SpellerPages
FCKConfig.SpellChecker = 'SpellerPages'
FCKConfig.SpellerPagesServerScript = 'server-scripts/spellchecker.php'
//-->

FCKConfig.TemplatesXmlPath	= FCKConfig.EditorPath + 'fcktemplates.xml' ;

FCKConfig.EmmSessionID = "<%= session.getId() %>" ;


<!-- FILE BROWSER AND UPLOADER -->

<!-- disable built-in resource browser for link window-->
FCKConfig.LinkBrowser = false ;

<!--  enable built-in resource browser for image properties window-->
FCKConfig.ImageBrowser = true ;

<!-- URL for Browse Server button in image dialog -->
FCKConfig.ImageBrowserURL = '<html:rewrite page='<%= "/fckeditor-2.6.6/editor/filemanager/browser/emm/browser.jsp?mailingID="+tmpMailingID %>'/>' ;

<!-- size of built-in resource browser in image properties window -->
FCKConfig.ImageBrowserWindowWidth = screen.width * 0.7 ;	// 70% ;
FCKConfig.ImageBrowserWindowHeight = screen.height * 0.7 ;	// 70% ;

<!-- disable bulit-in resource browser for flash properties window -->
FCKConfig.FlashBrowser = false ;

<!-- disable Upload tab in link window -->
FCKConfig.LinkUpload = false ;

<!-- disable Upload tab in image properties window -->
FCKConfig.ImageUpload = false ;

<!-- disable Upload tab for flash properties window -->
FCKConfig.FlashUpload = false ;


<!-- DEPRECATED
FCKConfig.IEForceVScroll = false ;
FCKConfig.IgnoreEmptyParagraphValue = true ;

FCKConfig.DisableImageHandles = false ;
FCKConfig.DisableTableHandles = false ;
//-->

if( window.console ) window.console.log( 'Config is loaded!' ) ;	// @Packager.Compactor.RemoveLine