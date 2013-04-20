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
 * File Name: fckplugin.js
 * 	This is the sample plugin definition file.
 * 
 * File Authors:
 * 		Frederico Caldeira Knabben (fredck@fckeditor.net)
 */

// Register the related commands.
FCKCommands.RegisterCommand('My_EMM', new FCKDialogCommand( FCKLang['DlgMyEMMTitle'], FCKLang['DlgMyEMMTitle'], FCKConfig.PluginsPath + 'emm/emm.jsp;jsessionid='+FCKConfig.EmmSessionID, 340, 200 ) ) ;

// Create the "Find" toolbar button.
var oEMMItem		= new FCKToolbarButton( 'My_EMM', FCKLang['DlgMyEMMTitle'] ) ;
oEMMItem.IconPath	= FCKConfig.PluginsPath + 'emm/emm.gif' ;

FCKToolbarItems.RegisterItem( 'My_EMM', oEMMItem ) ;			// 'My_EMM' is the name used in the Toolbar config.
