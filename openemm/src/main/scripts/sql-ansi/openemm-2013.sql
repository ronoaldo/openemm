


DROP TABLE IF EXISTS admin_group_permission_tbl;
CREATE TABLE admin_group_permission_tbl (
  admin_group_id integer NOT NULL default '4',
  security_token varchar(255)  NOT NULL default '',
  UNIQUE KEY unique_admin_group_idx (admin_group_id,security_token),
  KEY admin_group_idx_8 (admin_group_id)  
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO admin_group_permission_tbl (admin_group_id, security_token) VALUES (4,'action.getcustomer'),
	(4,'action.op.ActivateDoubleOptIn'),
	(4,'action.op.ExecuteScript'),
	(4,'action.op.GetArchiveList'),
	(4,'action.op.GetArchiveMailing'),
	(4,'action.op.GetCustomer'),
	(4,'action.op.SendMailing'),
	(4,'action.op.ServiceMail'),
	(4,'action.op.SubscribeCustomer'),
	(4,'action.op.UnsubscribeCustomer'),
	(4,'action.op.UpdateCustomer'),
	(4,'actions.change'),
	(4,'actions.delete'),
	(4,'actions.set_usage'),
	(4,'actions.show'),
	(4,'admin.change'),
	(4,'admin.delete'),
	(4,'admin.new'),
	(4,'admin.show'),
	(4,'adminlog.show'),
	(4,'blacklist'),
	(4,'campaign.change'),
	(4,'campaign.delete'),
	(4,'campaign.new'),
	(4,'campaign.show'),
	(4,'campaign.stat'),
	(4,'charset.use.gb2312'),
	(4,'charset.use.iso_8859_1'),
	(4,'charset.use.iso_8859_15'),
	(4,'charset.use.utf_8'),
	(4,'cms.central_content_management'),
	(4,'cms.mailing_content_management'),
	(4,'forms.change'),
	(4,'forms.delete'),
	(4,'forms.view'),
	(4,'import.mode.add'),
	(4,'import.mode.add_update'),
	(4,'import.mode.blacklist'),
	(4,'import.mode.bounce'),
	(4,'import.mode.doublechecking'),
	(4,'import.mode.null_values'),
	(4,'import.mode.only_update'),
	(4,'import.mode.remove_status'),
	(4,'import.mode.unsubscribe'),
	(4,'mailing.archived'),
	(4,'mailing.attachments.show'),
	(4,'mailing.change'),
	(4,'mailing.components.change'),
	(4,'mailing.components.show'),
	(4,'mailing.content.show'),
	(4,'mailing.copy'),
	(4,'mailing.default_action'),
	(4,'mailing.delete'),
	(4,'mailing.graphics_upload'),
	(4,'mailing.new'),
	(4,'mailing.send.admin'),
	(4,'mailing.send.admin.options'),
	(4,'mailing.send.show'),
	(4,'mailing.send.test'),
	(4,'mailing.send.world'),
	(4,'mailing.show'),
	(4,'mailing.show.charsets'),
	(4,'mailing.show.types'),
	(4,'mailinglist.change'),
	(4,'mailinglist.delete'),
	(4,'mailinglist.new'),
	(4,'mailinglist.show'),
	(4,'mailinglist.recipients.delete'),
	(4,'mailloop.change'),
	(4,'mailloop.delete'),
	(4,'pluginmanager.show'),
	(4,'profileField.show'),
	(4,'recipient.change'),
	(4,'recipient.delete'),
	(4,'recipient.new'),
	(4,'recipient.show'),
	(4,'recipient.view'),
	(4,'settings.show'),
	(4,'stats.clean'),
	(4,'stats.domains'),
	(4,'stats.ip'),
	(4,'stats.mailing'),
	(4,'stats.rdir'),
	(4,'targets.change'),
	(4,'targets.createml'),
	(4,'targets.delete'),
	(4,'targets.show'),
	(4,'template.change'),
	(4,'template.components.show'),
	(4,'template.delete'),
	(4,'template.new'),
	(4,'template.show'),
	(4,'update.show'),
	(4,'use_charset_iso_8859_1'),
	(4,'userlog.show'),
	(4,'wizard.export'),
	(4,'wizard.import'),
	(4, 'recipient.column.select');



DROP TABLE IF EXISTS admin_group_tbl;
CREATE TABLE admin_group_tbl (
  admin_group_id integer NOT NULL default '0',
  company_id integer NOT NULL default '0',
  shortname varchar(255)  NOT NULL default '',
  description varchar(255)  NOT NULL default '',
  PRIMARY KEY  (admin_group_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO admin_group_tbl (admin_group_id, company_id, shortname, description) VALUES (4,1,'Standard','Standard'),
	(0,0,'Dummy','Dummy');



DROP TABLE IF EXISTS admin_permission_tbl;
CREATE TABLE admin_permission_tbl (
  admin_id integer NOT NULL default '0',
  security_token varchar(255)  NOT NULL default '',
  UNIQUE KEY admin_permission_unique_idx (admin_id,security_token),
  KEY admin_idx_133 (admin_id)  
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS admin_tbl;
CREATE TABLE admin_tbl (
  admin_id integer NOT NULL auto_increment,
  username varchar(20)  NOT NULL default '',
  company_id integer NOT NULL default '0',
  fullname varchar(255)  NOT NULL default '',
  timestamp timestamp NOT NULL default CURRENT_TIMESTAMP ,
  admin_country varchar(2)  NOT NULL default '',
  admin_lang varchar(2)  NOT NULL default '',
  admin_lang_variant varchar(2)  NOT NULL default '',
  admin_timezone varchar(255)  NOT NULL default '',
  layout_id integer NOT NULL default '0',
  creation_date timestamp NOT NULL default '0000-00-00 00:00:00',
  pwd_change timestamp NOT NULL default '0000-00-00 00:00:00',
  admin_group_id integer NOT NULL default '0',
  pwd_hash varbinary(200) NOT NULL default '',
  preferred_list_size smallint(5) unsigned NOT NULL default '0',
  default_import_profile_id integer NOT NULL default '0',
  PRIMARY KEY  (admin_id),
  UNIQUE KEY username (username)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ;


INSERT INTO admin_tbl (admin_id, username, company_id, fullname, timestamp, admin_country, admin_lang, admin_lang_variant, admin_timezone, layout_id, creation_date, pwd_change, admin_group_id, pwd_hash, preferred_list_size, default_import_profile_id) VALUES (1,'admin',1,'Administrator',CURRENT_TIMESTAMP,'EN','en','','Europe/Berlin',0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,4,'9BD796996FCDF40AD3D86025C03F2C9E',0,0);



DROP TABLE IF EXISTS bounce_collect_tbl;
CREATE TABLE bounce_collect_tbl (
  mailtrack_id integer NOT NULL auto_increment,
  customer_id integer default NULL,
  mailing_id integer default NULL,
  company_id integer default NULL,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  status_id integer default NULL,
  PRIMARY KEY  (mailtrack_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS bounce_tbl;
CREATE TABLE bounce_tbl (
  bounce_id int(10) NOT NULL auto_increment,
  company_id int(10) default NULL,
  customer_id int(10) default NULL,
  detail int(10) default NULL,
  mailing_id int(10) default NULL,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  dsn int(10) default NULL,
  PRIMARY KEY  (bounce_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS campaign_tbl;
CREATE TABLE campaign_tbl (
  campaign_id integer NOT NULL auto_increment,
  company_id integer NOT NULL default '0',
  shortname varchar(255)  NOT NULL default '',
  description varchar(255)  NOT NULL default '',
  PRIMARY KEY  (campaign_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS click_stat_colors_tbl;
CREATE TABLE click_stat_colors_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL,
  range_start int(10) NOT NULL,
  range_end int(10) NOT NULL,
  color varchar(6)  NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ;


INSERT INTO click_stat_colors_tbl (id, company_id, range_start, range_end, color) VALUES (1,1,0,1,'F4F9FF'),
	(2,1,1,2,'D5E6FF'),
	(3,1,2,3,'E1F7E1'),
	(4,1,3,5,'FEFECC'),
	(5,1,5,10,'FFE4BA'),
	(6,1,10,100,'FFCBC3');



DROP TABLE IF EXISTS company_tbl;
CREATE TABLE company_tbl (
  company_id integer NOT NULL default '0',
  shortname varchar(255)  NOT NULL default '',
  description varchar(255)  NOT NULL default '',
  status varchar(10)  NOT NULL default '',
  timestamp timestamp NOT NULL default CURRENT_TIMESTAMP ,
  creator_company_id integer NOT NULL default '0',
  xor_key varchar(20)  NOT NULL default '',
  creation_date timestamp NOT NULL default '0000-00-00 00:00:00',
  notification_email varchar(255)  NOT NULL default '',
  rdir_domain varchar(255)  NOT NULL default '',
  mailloop_domain varchar(200)  NOT NULL default '',
  mailtracking integer unsigned NOT NULL default '0',
  max_login_fails int(3) NOT NULL default '3',
  login_block_time int(5) NOT NULL default '300',
  uid_version int(2) default NULL,
  max_recipients integer default '10000',
  PRIMARY KEY  (company_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO company_tbl (company_id, shortname, description, status, timestamp, creator_company_id, xor_key, creation_date, notification_email, rdir_domain, mailloop_domain, mailtracking, max_login_fails, login_block_time, uid_version, max_recipients) VALUES (1,'Agnitas Admin','Agnitas','active',CURRENT_TIMESTAMP,1,'',CURRENT_TIMESTAMP,'','http://localhost:8080','',1,3,300,NULL,10000);



DROP TABLE IF EXISTS component_tbl;
CREATE TABLE component_tbl (
  component_id int(10) unsigned NOT NULL auto_increment,
  mailing_id int(10) unsigned NOT NULL default '0',
  company_id int(10) unsigned NOT NULL default '0',
  emmblock longtext ,
  binblock longblob,
  comptype int(10) unsigned NOT NULL default '0',
  target_id int(10) unsigned NOT NULL default '0',
  mtype varchar(200)  default NULL,
  compname varchar(200)  NOT NULL default '',
  url_id int(1) unsigned NOT NULL default '0',
  description varchar(200)  default NULL,
  PRIMARY KEY  (component_id)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ;


INSERT INTO component_tbl (component_id, mailing_id, company_id, emmblock, binblock, comptype, target_id, mtype, compname, url_id, description) VALUES (1,1,1,'**********************************************************************\r\n[agnDYN name=\"0.1.1 Header-Text\"/]\r\n**********************************************************************\r\n[agnDYN name=\"0.2 date\"/]\r\n\r\n[agnTITLE type=1],\r\n\r\n[agnDYN name=\"0.3 Intro-text\"/]\r\n[agnDYN name=\"0.4 Greeting\"/]\r\n\r\n----------------------------------------------------------------------[agnDYN name=\"1.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"1.0 Headline ****\"]\r\n\r\n[agnDYN name=\"1.1 Sub-headline\"][agnDVALUE name=\"1.1 Sub-headline\"/]\r\n[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/][agnDYN name=\"1.3 Link-URL\"]\r\n\r\n[agnDYN name=\"1.4 Link-Text\"/]\r\n[agnDVALUE name=\"1.3 Link-URL\"][/agnDYN name=\"1.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"2.0 Headline ****\"]\r\n\r\n[agnDYN name=\"2.1 Sub-headline\"][agnDVALUE name=\"2.1 Sub-headline\"/]\r\n[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/][agnDYN name=\"2.3 Link-URL\"]\r\n\r\n[agnDYN name=\"2.4 Link-Text\"/]\r\n[agnDVALUE name=\"2.3 Link-URL\"][/agnDYN name=\"2.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"3.0 Headline ****\"]\r\n\r\n[agnDYN name=\"3.1 Sub-headline\"][agnDVALUE name=\"3.1 Sub-headline\"/]\r\n[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/][agnDYN name=\"3.3 Link-URL\"]\r\n\r\n[agnDYN name=\"3.4 Link-Text\"/]\r\n[agnDVALUE name=\"3.3 Link-URL\"][/agnDYN name=\"3.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"3.0 Headline ****\"]\r\n\r\nImpressum\r\n\r\nSie möchten Ihre Daten ändern?\r\n[agnDYN name=\"9.0 change-profil-URL\"/]\r\n\r\nUm den Newsletter abzubestellen, klicken Sie bitte hier:\r\n[agnDYN name=\"9.1 unsubscribe-URL\"/]\r\n\r\n[agnDYN name=\"9.2 imprint\"/]',NULL,0,0,'text/plain','agnText',0,NULL),
	(2,1,1,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table bgcolor=\"#808080\" width=\"684\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr>\r\n    <td>[agnDYN name=\"0.1 Header-image\"]\r\n    	<table width=\"680\" border=\"0\"  bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td><img src=\"[agnDVALUE name=\"0.1 Header-image\"]\" width=\"680\" height=\"80\" alt=\"[agnDYN name=\"0.1.1 Header-Text\"/]\" border=\"0\"></td>\r\n            </tr>\r\n        </table>[/agnDYN name=\"0.1 Header-image\"]\r\n        <table width=\"680\" border=\"0\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td><td align=\"right\"><div style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 10px;\">[agnDYN name=\"0.2 date\"/]</div></td><td width=\"10\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td>\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n                 <tr><td><p><b>[agnTITLE type=1],</b></p><p>[agnDYN name=\"0.3 Intro-text\"/]</p><p>[agnDYN name=\"0.4 Greeting\"/]</p></td></tr>\r\n                 <tr><td><hr noshade></td></tr>\r\n              </table>[agnDYN name=\"1.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"1.5 Image-URL\"]<td>[agnDYN name=\"1.3 Link-URL\"]<a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[/agnDYN name=\"1.3 Link-URL\"]<img src=\"[agnDVALUE name=\"1.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"1.3 Link-URL\"]</a><!-- [agnDVALUE name=\"1.3 Link-URL\"] -->[/agnDYN name=\"1.3 Link-URL\"]</td>[/agnDYN name=\"1.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"1.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"1.1 Sub-headline\"]<b>[agnDVALUE name=\"1.1 Sub-headline\"/]</b><br>[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/]</p>[agnDYN name=\"1.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[agnDYN name=\"1.4 Link-Text\"/]</a></p>[/agnDYN name=\"1.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"1.7 Image-URL-1\"]<td>[agnDYN name=\"1.6 Link-URL\"]<a href=\"[agnDVALUE name=\"1.6 Link-URL\"]\">[/agnDYN name=\"1.6 Link-URL\"]<img src=\"[agnDVALUE name=\"1.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"1.6 Link-URL\"]</a><!-- [agnDVALUE name=\"1.6 Link-URL\"] -->[/agnDYN name=\"1.6 Link-URL\"]</td>[/agnDYN name=\"1.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"2.5 Image-URL\"]<td>[agnDYN name=\"2.3 Link-URL\"]<a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[/agnDYN name=\"2.3 Link-URL\"]<img src=\"[agnDVALUE name=\"2.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"2.3 Link-URL\"]</a><!-- [agnDVALUE name=\"2.3 Link-URL\"] -->[/agnDYN name=\"2.3 Link-URL\"]</td>[/agnDYN name=\"2.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"2.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"2.1 Sub-headline\"]<b>[agnDVALUE name=\"2.1 Sub-headline\"/]</b><br>[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/]</p>[agnDYN name=\"2.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[agnDYN name=\"2.4 Link-Text\"/]</a></p>[/agnDYN name=\"2.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"2.7 Image-URL-1\"]<td>[agnDYN name=\"2.6 Link-URL\"]<a href=\"[agnDVALUE name=\"2.6 Link-URL\"]\">[/agnDYN name=\"2.6 Link-URL\"]<img src=\"[agnDVALUE name=\"2.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"2.6 Link-URL\"]</a><!-- [agnDVALUE name=\"2.6 Link-URL\"] -->[/agnDYN name=\"2.6 Link-URL\"]</td>[/agnDYN name=\"2.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"3.5 Image-URL\"]<td>[agnDYN name=\"3.3 Link-URL\"]<a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[/agnDYN name=\"3.3 Link-URL\"]<img src=\"[agnDVALUE name=\"3.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"3.3 Link-URL\"]</a><!-- [agnDVALUE name=\"3.3 Link-URL\"] -->[/agnDYN name=\"3.3 Link-URL\"]</td>[/agnDYN name=\"3.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"3.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"3.1 Sub-headline\"]<b>[agnDVALUE name=\"3.1 Sub-headline\"/]</b><br>[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/]</p>[agnDYN name=\"3.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[agnDYN name=\"3.4 Link-Text\"/]</a></p>[/agnDYN name=\"3.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"3.7 Image-URL-1\"]<td>[agnDYN name=\"3.6 Link-URL\"]<a href=\"[agnDVALUE name=\"3.6 Link-URL\"]\">[/agnDYN name=\"3.6 Link-URL\"]<img src=\"[agnDVALUE name=\"3.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"3.6 Link-URL\"]</a><!-- [agnDVALUE name=\"3.6 Link-URL\"] -->[/agnDYN name=\"3.6 Link-URL\"]</td>[/agnDYN name=\"3.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" bgcolor=\"#D3D3D3\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr><td><h1>Impressum</h1>\r\n                 	 <p>Sie m&ouml;chten Ihre Daten &auml;ndern?<br><a href=\"[agnDYN name=\"9.0 change-profil-URL\"/]\">Newsletter-Profil &auml;ndern</a></p>\r\n                 	 <p>Um den Newsletter abzubestellen, klicken Sie bitte hier:<br><a href=\"[agnDYN name=\"9.1 unsubscribe-URL\"/]\">Newsletter abbestellen</a></p>\r\n                         <p>[agnDYN name=\"9.2 imprint\"/]</p></td></tr>\r\n              </table>              \r\n              </td>\r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\"><img src=\"[agnIMAGE name=\"clear.gif\"]\" width=\"8\" height=\"8\"></td>\r\n            </tr>            \r\n        </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,0,0,'text/html','agnHtml',0,NULL),
	(3,1,1,'R0lGODdhAQABAIgAAP///wAAACwAAAAAAQABAAACAkQBADs=','47494638376101000100880000FFFFFF0000002C00000000010001000002024401003B',5,0,'image/gif','clear.gif',0,NULL),
	(4,1,1,'/9j/4AAQSkZJRgABAQEAYABgAAD/4QBmRXhpZgAASUkqAAgAAAAEABoBBQABAAAAPgAAABsBBQAB\r\nAAAARgAAACgBAwABAAAAAgAAADEBAgAQAAAATgAAAAAAAABgAAAAAQAAAGAAAAABAAAAUGFpbnQu\r\nTkVUIHYzLjIyAP/bAEMAAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/bAEMBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/AABEIAFACqAMBIgACEQEDEQH/xAAf\r\nAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEF\r\nEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJ\r\nSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\r\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEB\r\nAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIy\r\ngQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNk\r\nZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfI\r\nycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/AP3v+D/wf+Kf/BU74p/t\r\njfF/4v8A7Y37YfwK+E3wK/bD+PP7HnwF+Av7Hnx58Vfs0aJpWifs0eKv+FeeLvif8T/F3w7+weNP\r\niT45+JXjSw13VrCw1bXf+EY8HeGP7MsLDTLi/uHfSfoP/hzT8Pv+kgX/AAWG/wDFnn7Tv/zVUf8A\r\nBGn/AJJ9/wAFAv8AtMN/wU9/9ad8VV+wjsqKzuyoiKWd3IVVVQSzMxICqoBJJIAAJJxQB+Pf/Dmn\r\n4ff9JAv+Cw3/AIs8/ad/+aqj/hzT8Pv+kgX/AAWG/wDFnn7Tv/zVV9P/ABP/AOCin7NHw6mvNO0G\r\n/wDif8fvEFi08N14e/ZT+B/xh/ahvrK+hwradqt38C/BPjvSNFv0naK3uLPVtSs7uyknia8ggjcP\r\nX5h/HH/g4Z+EXwFaa9+LP7LP7aPwF8HwypG3xD+Pn7H37TPgTwmqMWYTyzX/AML7CZIpoyhiVPNn\r\nRhIJISRsAB9O/wDDmn4ff9JAv+Cw3/izz9p3/wCaqj/hzT8Pv+kgX/BYb/xZ5+07/wDNVXBfs+/8\r\nFkPh1+1P4dl8Wfs/eM/gr8VtFsmt/wC14vDOoa4uvaF9pMn2eHxJ4Z1HVLTxN4WubtYZWtIfEOia\r\nfNcIjTQwzRAk/XHhz9u6BpVj8XeApYoC0Qa78OaolxKi9JmXTtTjtkkbPzxKdUiH/LN348wgHgn/\r\nAA5p+H3/AEkC/wCCw3/izz9p3/5qqP8AhzT8Pv8ApIF/wWG/8WeftO//ADVV+nHw8+MXw9+KELN4\r\nS1+C5vYkMlzot4psdatYw5QySafPiSWAEKTc2hubZd8avMsjbK9OoA/Hn/hzT8Pv+kgX/BYb/wAW\r\neftO/wDzVV4N8XfgZ8W/+CYPxE/ZK+NXwT/bJ/bD+N/ww+LH7X/7PP7J/wC0B8Bv2x/j74q/aX8N\r\na/4P/af+Iek/CLQfiH8OPE/xEXUfG3w2+IHwz8ZeIvDviJIdB1+Lw54q0O31PSdX'TJK'+ofv/X5\r\nRf8ABXb/AJJR+xt/2ld/4Jb/APraXwloA/V2iiigAooooAKKKKACiiigAooooAKKKKACiiigAooo\r\noAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiig\r\nAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAC\r\niiigAooooA/Hn/gjT/yT7/goF/2mG/4Ke/8ArTviqv2BngguoJra5hiuLa4ikguLeeNJoJ4JkMcs\r\nM0UgaOWKWNmSSN1ZHRirAqSK/H7/AII0/wDJPv8AgoF/2mG/4Ke/+tO+Kq/YagBqqqKqIqoiKFRF\r\nAVVVQAqqoACqoAAAAAAAAxVPVI9Mm03UYtaSxl0eWxu49Wj1Rbd9Nk'7eRb9NRS7BtXsXtTKt2t'\r\n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\r\n+p6H+zn+'8O'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\r\nbh9ZmuJowXiGnR2s8lwjoJFiQSBGR42b8uf+ConjTwr8QvgJ+xL4w8E69p3ibwxq/wDwVd/4Jh/2\r\nZrelTfaLC9/s/wDbh+F+l3vkTYXf9m1Cyu7SXgbZreRf4c1FXBY2hGtKvhMVRjh8QsJXlVw9WnGh\r\ni3Gc1haznCKp4hxp1JKjNxqctOcuW0JNVCtRqez5K1KftqbrUuSpCXtaK5L1adm+emvaU71I3iva\r\nQ196N/2EooorlNQooooAKKKKACiiigAooooAKKKKACiiigAooooAKinghuYZra5hiuLe4ikgngnj\r\nSWGeGVDHLDNFIGSSKRGZJI3VkdGKsCCRUtFAHg3wp/ZX/Zh+A+va54q+B37OPwG+DPifxNZtp3iT\r\nxH8KfhB8Pvh3r3iDT2vE1FrHXNY8IeHtH1DVrNtQjjv2tr+4uITeRpdFPPVXHvNFFABRRRQAUUUU\r\nAFFFFABXK6L4E8D+G/EfjLxh4d8G+FNB8W/EW80XUfiD4p0Xw9pGl+I/HeoeHNEtPDPh6+8Za3Y2\r\ncGp+J7zQfDdhY+H9Fudbur6fS9EsrTSbF4LC2ht06qigAooooAKKKKACiiigAooooAKKKKACiiig\r\nAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAC\r\niiigAooooAKKKKACiiigD+R/xF4b/aH8W/8ABIj/AILM6F+zJD8Rr74h3n/BXX/goA3iDRfg7JdR\r\nfFvxJ8GoP26ref48+Gvhs1iPt03ivXfg5H420+zsNOI1TVrae80jSRJql/ZxSc34K0r/AIJ5eL/2\r\n0f8Agmhc/wDBD34VXPw++JngP4uSTftmeKPhF8Jvir8IvBXhv9iIfDnxMPiV4I/a41nxXoHhrTPE\r\nvxI8Q+Lf+ELt/h7afEC48S/ESDxrpN/ex31lqJ068uv0F+Bnxd+In/BMH4t/tk/BL41fslftf/Fj\r\n4YfG/wDbD+Pv7Y/wG/aA/ZP/AGefiH+0/wCENf8ADX7S/ipfiJ4n+HHxD0H4RaT4i8Z/DP4gfDbx\r\ntqOv6DCniLw7b6H4q8ORaZq+k6nIVeXUPqL/AIe7fCj/AKM2/wCCrv8A4q3/AG0v/nS0AfiL/wAE\r\n3fhL/wAFcPjH8Bv2bf2vPEf7T3jLwv8ADuzHxt8YfGLVfiH+2b+0H8WvHXxj+Huhj4x+DbbwF/wy\r\n74w+Beh/Dn4S+Io9bsPDN7pPj7Qvjtq2seH7bw9Dr2nWt5qWsyWGm+Ff8E9v2p/H/jD9kzxT4p+N\r\nf7avxvuf2uD+w3+0F4q+HHgmy/bP/a8+IPi7xL8ZdH/Z3+K+v6pN8TPg346+Bnw6+GHwy8S+ENK0\r\n1vGnhLT/AAZ8SviMun+MtBtJdP12C+07S4da/ov/AOHu3wo/6M2/4Ku/+Kt/20v/AJ0tH/D3b4Uf\r\n9Gbf8FXf/FW/7aX/AM6WgD8ev2W/2if+CnHgX49/8EZf2Xv2sNW+JHjzwV8UNZ174u6T+1/4V1zX\r\ndM8O/tB/BrxJ+xd8S/Glv8Ef2q/D/wDabz2Pxt+D3xf1T4fW9tqWsG50L4oadb6DrtoLbxHF4ktt\r\nW/Vj9mP4p+Gvh74XsbHxj8Ybvw1Z2nxA8fapf/DtP2e/EniF7jS7/wAc67fQCXx5p3h3VXvP7bs5\r\notRt7+yl/wBCsru1sY4ibQyS9b/w92+FH/Rm3/BV3/xVv+2l/wDOlo/4e7fCj/ozb/gq7/4q3/bS\r\n/wDnS17GW5pHA4fGYadCdWGLq4Sq5U6tCnOEsJ9Z5VbEYPG0pxn9YbknSUk4RtKzkn8/m+QxzXHZ\r\nZjZV4Q/s2GMiqFSjWqUq8sVUwNWLquhjMJJwpSwK5qMnOlXVTlrRdOMoVOX+J+ga7q/7RPxk/aJ+\r\nEH26/wDF3we8DfAX4jeC9MtZb/S7P4g/D+48Pa1dfEfwY9kYEnuRqnhKbSLpbRLGXVbS8Gk2MFtD\r\nd6rDHLR8FfCnxp8Pfj58Vtd8ei4v/HXxG/YR+Jvj/wCJutWyXsnh+Px/4o+Jc0knh3SJpg9tZ23h\r\n3w3pmh6FZ6bDMVFtpBvIlMU5c9t/w92+FH/Rm3/BV3/xVv8Atpf/ADpaP+Hu3wo/6M2/4Ku/+Kt/\r\n20v/AJ0tfQR43xccu/s2OCoezll1PLJ15TviZYWjRw8KNH2qpJqhDEUq2NlR1jPE4jnk7UqaXhQ4\r\nHjGWIm8zm/rGa4vN3RjhKccJRxeLqV+epQoqs6kaqw1WGDVSeIqWoUnGnGmq1WMvLvg/8D/ic3g3\r\n4X/ArQXuIP2ff2gPAfgX4r/FjVL/AFHV49R8HS+ELPRYPjH8PtJmEhj0S48fa5c+GfMVmtrmyF14\r\nlsRbvaWF19i+ffjRp9/pf/BOX/gmxY6nZXenX0H/AAVd/wCCe/n2d9bTWl1D5v8AwUj8NzR+bb3C\r\nRzR+ZDJHKm9BvjdHXKspP2n/AMPdvhR/0Zt/wVd/8Vb/ALaX/wA6WvmH46/HX4m/8FLPib+yL8Bf\r\ngL+yL+2H8Lvh98Lv2w/2b/2svj/8f/2sv2b/AIjfsx+AvCvgL9mP4jab8XtM8C+BdM+L2m+FvF/x\r\nJ+I/xJ8X+FvD/ha0tPC3h++0nwvpN9f69r1/FbRKqY57xlis9wOIwFbCUaEMRmlDNqlSnO9Spi6N\r\nPM8O6tV+zh7WtWwmNwuHq1pWco5bRkop1JKPdkPCccir4Osswq4xYPL8bl1ONahGEo0sZWyzEuFO\r\ncKjVPD0cRgMTVo0HCbh9fqU41VSoU4P9+6KKK+NPrwooooAKKKKACiiigAooooAKKKKACiiigAoo\r\nooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiii\r\ngAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKA\r\nCiiigAooooA//9k=','FFD8FFE000104A46494600010101006000600000FFE1006645786966000049492A000800000004001A010500010000003E0000001B010500010000004600000028010300010000000200000031010200100000004E00000000000000600000000100000060000000010000005061696E742E4E45542076332E323200FFDB00430001010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101FFDB00430101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101FFC0001108005002A803012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00FDEFF83FF07FE29FFC153BE29FED8DF17FE2FF00ED8DFB61FC0AF84DF02BF6C3F8F3FB1E7C05F80BFB1E7C79F157ECD1A2695A27ECD1E2AFF8579E2EF89FF13FC5DF0EFEC1E34F893E39F895E34B0D7756B0B0D5B5DFF8463C1DE18FECCB0B0D32E2FEE1DF49FA0FFE1CD3F0FBFE9205FF000586FF00C59E7ED3BFFCD551FF000469FF00927DFF000502FF00B4C37FC14F7FF5A77C555FB08ECA8ACEECA888A59DDC85555504B3331202AA8049248000249C5007E3DFFC39A7E1F7FD240BFE0B0DFF008B3CFDA77FF9AAA3FE1CD3F0FBFE9205FF000586FF00C59E7ED3BFFCD557D3FF0013FF00E0A29FB347C3A9AF34ED06FF00E27FC7EF1058B4F0DD787BF653F81FF187F6A1BEB2BE870ADA76AB77F02FC13E3BD2345BF49DA2B7B8B3D5B52B3BBB292789AF2082370F5F987F1C7FE0E19F845F015A6BDF8B3FB2CFEDA3F017C1F0CA91B7C43F8F9FB1F7ED33E04F09AA316613CB35FF00C2FB099229A3286254F36746120921246C001F4EFF00C39A7E1F7FD240BFE0B0DFF8B3CFDA77FF009AAA3FE1CD3F0FBFE9205FF0586FFC59E7ED3BFF00CD55705FB3EFFC1643E1D7ED4FE1D97C59FB3F78CFE0AFC56D16C9ADFF00B5E2F0CEA1AE2EBDA17DA4C9F6787C49E19D4754B4F13785AE6ED6195AD21F10E89A7CD7088D3430CD1024FD71E1CFDBBA069563F17780A58A02D106BBF0E6A89712A2F499974ED4E3B6491B3F3C4A754887FCB377E3CC201E09FF000E69F87DFF004902FF0082C37FE2CF3F69DFFE6AA8FF008734FC3EFF00A4817FC161BFF1679FB4EFFF003555FA71F0F3E317C3DF8A10B3784B5F82E6F624325CE8B78A6C75AB58C3943249A7CF89258010A4DCDA1B9B65DF1ABCCB236CAF4EA00FC79FF8734FC3EFFA4817FC161BFF001679FB4EFF00F3555E0DF177E067C5BFF8260FC44FD92BE357C13FDB27F6C3F8DFF0C3E2C7ED7FFB3CFEC9FF00B407C06FDB1FE3EF8ABF697F0D6BFE0FFDA7FE21E93F08B41F887F0E3C4FF111751F1B7C36F881F0CFC65E22F0EF88921D075F8BC39E2AD0EDF53D2757D314C92BEA1FBFF5F945FF000576FF009251FB1B7FDA577FE096FF00FADA5F096803F5768A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A00FC79FF8234FFC93EFF82817FDA61BFE0A7BFF00AD3BE2AAFD819E082EA09ADAE618AE2DAE22920B8B79E349A09E0990C72C33452068E58A58D99248DD591D18AB02A48AFC7EFF008234FF00C93EFF0082817FDA61BFE0A7BFFAD3BE2AAFD86A006AAAA2AA22AA22285445015555400AAAA000AAA0000000000003154F548F4C9B4DD462D692C65D1E5B1BB8F568F545B77D364D31EDE45BF4D452EC1B57B17B532ADDADC836ED6E641303196ABD5F8EFF00F0563F8EFADF85BC39E0CF81FE19D46E34D6F1CDB5F789BC713DA5C3DBDD5D785ECE76D2F47D019A29773E97AE6A6355BAD5A378E2F3BFB06C6D5659AD2E752B66F7F86320AFC4D9E60325A1515178BA92F6B5DA52F6187A30956C4565072873CA14A1270A7CD1739F2C2EAF73C5E21CEF0FC3B93E3737C4D39D5A784843968D36A33AD5AB55850A3494A5A454AAD48F3CED274E9A9D4509F2F2BFE6ABFE0ABFFF0004EFF813E10FDAC7E1FF00ED87FF000463F8A1F0F7F67DFDA1F4CF145DC3F1C3C01A3DA788F41FD9ABC5FA749135CCDAC78793C25E1BD63405FED9BAB0B7D23C71E08F0DE9175F0EBC6963AA5B7896C6EFC3FE28D1B559BC4DFB0FF0DBC4BAA78C7E1FF83BC51AE5869DA56BBAD6836573AFE95A3EA33EADA4E99E218435978834FD2B53BBB1D2EFB50D2ACF5BB5BFB7D2F50BFD2749BDD474E8ED6FEE34BB16BAFB347F9BB5F66FECFDE22D02CFC0B7961AAF883C37A45EDBF89F519123D6BC47A2E8D3CB6171A6E8A60315B6A77B66F2C29771EA07ED1189373C8D13B2886351FAEF1CF85795E41C370C7E4BFDA78CCC70F8AC353C47B492AFF58A35A32A75250C3D1A3174DC6B7B3A917072E4839C67CF75387E55C19E25E639D7103C16732CBF0982C4E1ABCB0D1A54A54A34B134631AB18CF115B112B4274215DCA551C94AB28429AA7CEA2BE9ED1B59D57C3DAAD86B7A25FDCE99AB699731DDD85FDA48639EDA78CFCAE8DC86560592589C345344CF0CC8F13BA37ECEFC01F8B69F17BC0D0EAF74B043E22D2A71A5788ED60431442F9214962BEB78999CC76BA842DE6C4A1DD5264B9815BF7240FE747C71FB587ECADF0CB5C3E19F889FB4EFECEBE07F118B4B7BF6D0BC55F1C7E17E85ABA595D9905ADD3E9FA8F8A6DEE920B8F2A43048D1059550B2165C1AFD58FF008273F8AAD3C7563AAF8FBC07AC5978D3E0FF008EBC3AF7FE18F887E12BFB5F11FC3CF145FF00873C4773A05C9F0EF8BF477BDF0F6B53E9F7CBAF6957A34BD52736D7BA6EA165749F69B1952DBF059C274A73A75212A75212709C271709C271769467192528CA2D34E2D269AB3573F6CA75215611A94A70A94E6B9A13A725384A2F671945B8C93EE9B47EA357E517FC15DBFE4947EC6DFF00695DFF00825BFF00EB697C25AFD5DAFCA2FF0082BB7FC928FD8DBFED2BBFF04B7FFD6D2F84B5259FABB4514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145007E3CFF00C11A7FE49F7FC140BFED30DFF053DFFD69DF1557EC357E3CFF00C11A7FE49F7FC140BFED30DFF053DFFD69DF1557EC35007E49FF00C1593C07A57ED17F03BC5FFB207C445BC8FE0EFC74F0AE843C5B77E1DB97D27C6305D7847E2068DE31B16D0B5D923BCB2B068F54F0CE82D309B4BBF2F6E6EA22B179F1C8BF891FB2FF00FC135BF678FD927C09ACFC3BF85FA87C4BBFF0F6B9E2DBCF1A5D9F19F8AEC35CD422D66FF47D0F43BA16D796BE1FD28C766F63E1ED376DB491CA239D66951D7CF75AFE9F7F6AFF0085177F117C0906ADA159BDE789FC1D3CD7D656D0286B8D434ABB1147AC58C2814B4B3AAC16D7F6F1EE058D9CB0C4AF2DC2A9FC85AEECBB33CC329C5471B96632BE071508CE11AF87A8E9D450A8B9670BADE325BC5DD3B276BA4D726330182CC68FD5F1F84C3E370FCF1A8E862A8D3AF45CE37E594A9548CA1271BB71E68BE5766ACD26BE40F8BDF097C2DE0AF0741E20D11F52176DE26D2F47912F2E96E226B7BED2F5FBD7750B0C4564497498403F302B23F4C0CFCCD5FA23F17B47BFD73E19F8D6CF46D17FE121F1041A0DEEA9E1BD1A39ED2CEEB54F1068F1FF006A695A459EA1A84D05869B71AEDCDA2E82751BD905AD9DBEA9713CFF00BB4247C99FF0407FDA4BF63EFF0082827C43F8A9A0FC47D275FF00875FB54FC03F184D3597ECC5F11EFF004B8CEADE13D32DEDED2EBC6E2C2E74CB1BFF00146A7E14F1947AA68BE2AF09C4908F04CF63A1DEF88ED750B7F13594765FD03C1FE2B65F97F0B6225C459862F1F9E61B1589F63869AA957138DA328D29E1DC710E9FB0A74D549CE949D4A8EA5354E751539C7962FF000CE2FF000DB31CC78969BC832FC160F2AAF86C2FB6AB4BD8E130983AA9D5A755BC3C5C65524E34A35251C2D1A8DB9C5D45073E797E897C27FF0082147FC13B7F695F861E02F8C1FB647ECA9E1EF8A5F1B3C57E1E8AF2F3C41AC78CFE2D7876FECFC273DEDEDE783B469F4EF0A78FFC3DA4C72DAE87776D773E349B5BB86E6FE7B4BB33CD6A6E25FD97FD9BFF0066DF825FB22FC18F06FECF5FB3A7812CFE19FC1BF87CBAEAF83BC0FA7EABE20D6ACB425F13789758F186BAB6FA8F8A757D735B99751F12F88359D5A5177A9DC08EE2FE648045008E14F71A2BF01CDF33AF9CE698FCD712A2ABE3F155B15523149460EACDCA34E364AF1A71E5A716D733514E4DC9B6FF006FCAB010CAF2CCBF2DA73F690C060B0D83551C541D5FABD1852755C13928CAAB8BA925CD2B4A4F57B857E517FC15DBFE4947EC6DFF00695DFF00825BFF00EB697C25AFD5DAFCA2FF0082BB7FC928FD8DBFED2BBFF04B7FFD6D2F84B5E71DE7EAED14514005145140057E3AFF00C15DFF006B5FDACBF65087F624BDFD917C296BF12BC5BF133F6A6D5BC37E3EF83CDA6E8F75ACFC65F85BE04FD9DBE37FC6BF17FC34F07EA7AADBCF2F87BC73E25B3F8642DBC19AAE9924178DE261A5584A2EEC2F2F34FBBFD8AAFCFEFDB1BE027C4DF8BDFB407FC1367C7BE04D1AD354F0CFECE7FB5C78AFE2C7C58BDB8D634BD365D0BC11AA7ECA7FB44FC2DB2D4ACECF50BAB7BAD6EE24F1A7C41F0B69ADA769115E5FC705F4BA84902D8D95DCF080784EB7FB7BDE7C51FDA7FF00E09067F673F1ED86ABFB347EDCBE14FDAEBC5FE3281B44D0AF350F10D97C2FF81DA178CBC13A65D5FDCDBDF6AFE10F10782FC6375AAE9DE2BD174AD46CAEEDF5CB0D47C3BAFACCDA7C96D1FA87C38FF82A87ECDFF137C6DE07D0F48F0B7C7BD13E18FC59F1EC9F0B3E0AFED39E2FF841ADF86FF66CF8CFF119B52D5F49D2BC29F0F3E215EDC9D46F1FC59A8683ABDB780BC45AEF86740F067C429AD218BC11E25D7E5D57454D4BF3FF00C55FF04A7F8D7E10FF0082ABFC11F8B9F06EEF48B6FF00827DEAD07ED99F12BC7FE07D3358B4F0E78A7F678FDA1BF69DF81F77F0DBE246B7F0EA2FED2B0D5AE7C13F1AB5DB7F0D78E1748F0CC7772F82BE257FC26FAEC29A0691AEDBF99E25FB337FC1307E30780740FD92FF0066FF001D7EC4DA8CB17ECE5F15BE164DE36FDA3FC6BFF050DFDA2FC79FB2FF0089BE1DFECFDE27B4F117823E23FC1FFD99346FDA634BF1068BF17FC4171E14F09EB9E17F0378CBE0DE8BF0ABE1978C9DE694F887C3FA74162403F6525FF82997ECAD67E09FD95BC73AC6BDE2ED06D3F6C4FDA0EEFF0065FF00841A0EB1E0DD52D7C591FC62D33C5DE30F87BAEE81E33D050CD71E16B1F0EF8FFC15A9782B5CD72E9E7D2AC7C43A86816A6E5E0D66D2E5A87C49FF00829A7ECFBE02F14F887E1E787FC39F193E34FC53D1FE32F8ABE03E8DF0B7E09FC3F5F1978D3C75F103E1E7C33F047C58F8A50F83A3BDD6B42D066D13E16785BE2178661F1EEBFAFEBDE1ED3748F10DDBF87609AFB5544B797F2CFE35FF00C12B7F690F885F1A7FE0A1771A6E99A48F84707847E2BFC7AFF82795EC7E2DF0F41E228BF6CCFDA0F5FF00D9FBE3BF8F64BA49E686F7C17A7F863F687FD96F45BDD3F59D727B3D36E348F8D3E2BFB1CCD047A99B6EF3C4DFB007C5E93F64BFD91B47F8C3FB24F82FF6A3F8983E287C72FDA5BF6B2D2BE1C7C6097E047ED43F0C3F687FDA62F7C51F1275EF127ECADFB40E83F147E0FF0086F4C4F02F8C7C61A8FC34F1925E7C41D1EDFC79E07D0FC2579A64B70DA5323807D6371FB7D6A1F167F680FF00826CE9FF00052EFC61E10F86BFB407C6DFDB1FE11FC7AF877F13FE1AB783FE21E97E26FD9E7E02FC4ED76E3C15E22D2BC51A649ADF85B5AF077C4FF05C2F737BE18D49B4BD7ED2056B4D5F5BF0FDFC32CFE8FF000E3FE0AA1FB37FC4DF1B781F43D23C2DF1EF44F863F167C7B27C2CF82BFB4E78BFE106B7E1BFD9B3E33FC466D4B57D274AF0A7C3CF8857B72751BC7F166A1A0EAF6DE02F116BBE19D03C19F10A6B4862F0478975F9755D15352F84BE11FEC45FB71F88FC4BFB126A1FB426A5E39D6BC25F0B7F686FDBDFC412BFC41F8DBA0FC4FF008E9F01BF66CF8EDFB2EF8E7E13FC1CF04FC40F8BF617D69AAFC5DF89563E37F12EA576DE26F0D6A9E32BCF0CE8DAF687A3CDE2CBDB1F09C7A927CFFF00B337FC1307E30780740FD92FF66FF1D7EC4DA8CB17ECE5F15BE164DE36FDA3FC6BFF00050DFDA2FC79FB2FF89BE1DFECFDE27B4F117823E23FC1FF00D99346FDA634BF1068BF17FC4171E14F09EB9E17F0378CBE0DE8BF0ABE1978C9DE694F887C3FA74162403D3E7FF82D37C718BE06FED41F1513F676F15A6AFF0009FF00E0ABDF0C3F623F07E9379F0AFC4115A45F087C71F17FE19FC37D406B70278E85DDEFC70D2F49D4BC50D7F671DDDAE85A6FC40F19FC2FD19F4ABAD2F559205FD13F883FF0555F807F0F75EF166912FC2FFDA8BC67A5FC22D1BC33ACFED31E34F871F023C41E35F087ECA43C4BE0CD23E2249A37C77BED26F1F52B0F14784FC09AE697E2DF1F785FE1EE93F10F5EF04683771DFF0088AC2C616527F39BC69FB0D7ED956BF087FE0A0BF0B340F82565E28BCF117FC1593E13FF00C147FE016BB69F14BE1D699A5FC74F06DA7ED17F02FE33F89FE17DB43AC6B967A8FC38F18785F45F855A869175ABF8FAD74CD0354D535289B449EFED2D84F75E7FE38FF827B7ED0FA1F8DBF6C6D6A2FD89FE277ED01A9FED9FE2FBFF00DA17C01AAD97FC14ABE2BFC0DF855F05BE227C60F86FE10F0CF8FBE077ED45F0D7E1F7ED0DF0CA0F14782FE19F8BF40BFB81E34F81DE17F8AB7FF10BE1E5DDB786ACFF00B08D8693A5E8E01FB27E2AFF00828D7C05D13F68AF037ECBFE11D03E30FC6AF89DE3BF0C7C1BF8896F37C0DF86DA8FC49F057867E11FC70F107893C35E12F8CFE34F1BE93769E1EF0E7C29B1D4BC39BFC41E2EB9BC92DAD34FD6B49D4F4D8756B18F5B9F46ADFF000528F8F7F137F672F801E05F1EFC27D66D342F136B9FB5C7EC4FF09F51BDBCD1F4BD7229BC11F19FF6ADF849F0B7E20E9AB67ABDADE5AC571AB782FC57ADE9B6DA8C512DFE973DCC7A869D3DB5F5B5BCF1F8DFECBBFB1478E7E0A7ED63F1635BBEF0DE91E18F815A8FFC138FF613FD933C11A8FC3CF19788F4F8ED3C47FB3F6A3FB47E9FE33D0BC212EABE30D6FE3078774BF0D787FC7DE0D6F09F8AFC47E25BEF144B1DD433FF00C25BAAF88F4CD4B515E17F6DAFD82BC77A77EC9D7FE10FD970FC7DFDA27E225B7ED41FB15FC723E08F8F1FB59FC40F8997FA9E87FB39FED31F0EFE2C789B4AF06F8B7F694F88FADE85E07B9D43C31E1FD6BCD86CF52D22D75CBF874B5BF5BC9ACAC16200FD4BF8DDF1B3E18FECE5F0A7C69F1B3E3278A2DFC1BF0D7E1FE971EADE27F104F67A8EA4F6D15CDF5A693A6D958693A3DA6A1AC6B5ACEB5ACEA1A7689A0E87A3D85F6AFADEB5A8D8695A659DD5F5E5BC127C9DF0B7FE0A4DF023C77E2BF1CF80BE21F84BE387ECB5E3AF01FC21F11FED0977E13FDA9FE185E7C29D47C41F01BC1D76961E30F8B3E15BE8F52F10E81ABF86FC1D773D8C5E32D29B58B5F1AF83C6ABA3CDE28F0AE9106AB612DC7CC3FB48E93FB74FEDD5FB3F7C43F84977FB1A5EFECA9E38F076B9F057E3F7C1EF13FC4DFDA2BE0F78F7C07F113E28FECF5F1F3E187C63F0FF00C20F16DAFC1BD5FC55E26D07C39F10A0F065FE87ABF89AE34B9F4FD1ADA7376F6F7D3A43653F897ED6DFB3E7ED0BFB7C3F8FBE26FED35F0C6EFF00E09FDF047E077EC11FB71FC26875BF157C4EF007C54F88DACF8E3F6A1F879E0CD1FC57F102283E07EB9E3AF0FE8FF08BE0F7857E1C5EEA6D2DFEAD6DF10FC6DAA6A515BDBF83746D3ED276B900FB97E0FF00FC1537E037C5FF00891F04BE190F861FB50FC2DD4BF6964F11EA5FB3DF88BE347C07F11FC3AF067C5EF087853E18EBDF1735AF1C786FC43A95CCF1695A25B783B425BBFEC8F1A41E15F1C97D6F4199BC209A65ECBA8DAC5F0E3FE0AB5FB34FC4CF1778074CD2BC31F1F7C3FF000B3E3078FA1F857F047F69CF1B7C1AF10F853F671F8CFF00126FB56D4B43D0BC23E00F1DEA52A6B135C78BF55D1F53B1F01EBBE23F0AF877C21E3DB9B6860F06F88B5C9F53D1E3D43F31EF3E2D7ED01FB4CFC7CFF824AFC0DF8DFF000A3E157C23D13C47A27ED27771F8DFE13FED05E17F8D33FC58D1B50FF827C7C6FF008743E357C16B3F0968F05C786BE05349E34DFA66BDF119F42F135E6BDE28F0368D078784967AA5DBFAAE8DFB357EDE1F11FF0066DFD88FFE09E1F147F673F0CF827C19FB2E7C50FD90EEBE2B7ED6DA57C64F02DF7C37F1EFC2EFD87BC79E06F1B782EFFE0DFC38B4D435FF008C69E3FF008C0DF0AFC21A76A7A1FC49F06784BC3BE084D6FC4777FF00091EB6D61A6DADD007E84FFC14B3F6B3F107EC8DF013C27E21F066BFF0E7C15E38F8C3F1C3E18FC03F0B7C4AF8C1298FE157C245F1DDE6A5AB78DBE2F78F50EA7A2457DA2FC31F85FE15F1DF8D2DB48BAD7346B1D6F5BD1B48D1EFF52B7B3BE9CB7C13E07FDBC3F681D17F655FDBD3E2CFC18FDA33E11FFC14925FD9D7E14D9F8C3E1D6A965F0AB57F849F1EBC27F136E3FE12D1E28F0C7C5FFD9E7C25A3E833F883E14E8BE1DD174EF895F0DFC4BE19D3FC29E2AF88DA369FE32F0A680DE34BBB7B0F13D8FDE5FF000526FD973C69FB4DFC25F83FA97C33D27C37E2CF89BFB2EFED49F047F6BCF017C38F18EA56FA0F85FE2D6B1F05B55D51F53F85DAA78A2EB4BD6A0F0A5C78CBC25E22F1269DE1EF10DD69971A5D8F8ABFB0975F7B3F0E4FABDFDAFE747ED2BFB0DFED6BFB78B7ED8BF1B759F835E1DFD9B3C47F127F665F80DFB37FC36FD9BBE2E7C4EF0AF8B23F8DF6FF0006FF00691B1FDA57C6DA87ED01E21F817A8F8D3C19E17F09FC48D2EDEE3F67BF0CD8E93E27F883ABC1E0EF1178DF5BF1559E91A76A36BE18BB00B7FB377FC146BE20786FC4FF00B516A93FED69F0E7FE0A71FB36FC0BFD80756FDB3FC47F1DBE127C3CF067C367F873F127C3D26B7AAD97ECFF007DABFC36BAD67C0974FF00153C07A1F88BC5DE16D0F5BB73F12BE1EFFC215AC5A78CDB5A4D56C5EDBD93E197C6BFDBCFE0978FBF612F1B7ED43F18FC0BF18BE1C7EDEB7F3F803C6BF0ABC3DF06340F84CDFB31FC6CF147C09F16FC7CF86FE1FF0085BE2B9FC5526BFE33F054927C3FF16FC25D6ACFE2B5E6B3E32D5759BCF0AF8A6C2FF458975DD0D381D6FF00631F8EBFB66FC6DF1978CBC5DFB32E9BFF0004EEF86B3FFC13DFF69CFD8A75E8E2F1B7C24F1F7C45F8C9AA7ED3165E0ED3B46F3BC3DF03FC43E23F87F0FC22FD9FD7C33AE788FC2775E27D7747F1D6AFE2AF13C56BA7F86F41D024D704BD97837E1E7EDE7F197C5FFB08F877F682FD996DFE1AF81BF605B8D67E2FFC4BF1745F18BE13FC4A9BF6A9F8EBE04F80BF103E02FC27D3FE04D8C7AF5C788FC3FE19D76EFE21F887E2BEADE20F8E6DF0B35BD1B51B7F0B786A74BE9C6B1E22B200FB6FE14FFC144FF667F8D7AE7C0CF07FC39D73C51AF78FBE3BCBF15174EF8743C2D7969E3AF8630FC11BBBDD1FE2A5DFC73F0F5DCB15CFC24B7F0878AED20F044DFF0009535B4FAD78C356D1F45F0CC1AD497CB2A7E397ECE1FF000530FDA7BC71F1C7F662B2D7BF685F863F127E347C7BFDAB3E247C0CFDA13FE096FE1DF85DE18D07E25FEC55F0C3C1D2FC46975FF893AF78C6D758B9F8A7657BF06B4AF0D780B52F1B789BE28DB9F87FF150F8CEE6D3E1B5AE9579A9F84ADE4F77FD963F623FDB33E02FED45ABFEDB1E26F0F7C37D47E20FFC142BFE123D07F6EBF86BE00B5F875A45EFECAF6F669A9DF7ECD3E24F839E3695BC3937C41B3F85BA4CB2783FF695B5835DBED57E2EF8EB5E3F187C2FA7EBDA86830D95EF927C27FD873F6AEB0F849FB02FEC6F79FB1A7C36F8457FFB1B7ED19F043E2678FBF6F1F0F7C52F86B7FE1FF1D685F02FC5ABE2BF17F8FF00E12F8656FF00C59F1F354F8A5FB53DAC1A8683F11747F8C5E11F0EE83A63FC41F1B5CEA9AF6BF0DBE90F2807AB7C53FDB03F6D8B2F829FB4BFFC148FC13F153C1DA67ECDDFB2DFC71F8DDE14D3FF0064CBFF0082F68D6FF19BE017ECCFF19356F831F183C7FAF7C69D7B51D3FC79E12F889ACCDE10F88FE33F026A5E1DB34F87BA0D9681E19D2FC4BE1AF11A5DF8835187ED8F8BDFF054DF803F097E20FC6EF87107C3BFDA4FE2D6B1FB3345E18D63F68AD53E097C15D63E227873E0DF817C63F0DFC31F153C3BF11FC4DABD95FDA2EB3E19D4FC23E23B9BBB5D2BC090F8BFC7D707C25E33B9B7F054DA5686752B9F84FE237ECA7FB71CDF027F6AEFF8268F84BE09699AAFC09FDA63E3E7C64D6FC27FB62DDFC59F03DEF83FE1A7ECDDFB54FC59D6BE337C69F0D78D7E10789F5FFF0085AFAB7C53F075CF8D3E24780FC1FA0783FC3377F0F3C4D6DACF853C4977E2DF0C1B7D7B4C8D9A57C47FDAAFC2BFB6DFFC1647E1A7ECCFFB2968BF1DBFE12BF177ECCDA4F873C4B27C52F877F0B3C3FF000D3C75ADFEC2BF05F40D365F8B4BE29BBB0F14EA3F0A61B086C754B76F859A678EFC59A6CBA5F88B4FB5F064726B965A83007E9778CFFE0A41FB25FC3B93C612F8DBE21BF87342F0D7ECD3A07ED71A0F8BAFF4C98F84FE2B7C0CF10DC47A7DA788FE106B16EF3A78EF58875BD43C31E1D9BC1D63143E2CB9D73C73E06B2D2B47D47FE129D2E59BEC7F06F8917C65E11F0B78B9742F1278613C53E1ED1BC449E1BF196932681E2ED0135AD3ADF514D1BC53A0CB24D3689E21D356E459EB3A44F23DC699A8C3716539F3A0703F9C5F891FF0498FDA7BC7FE06FD8E3E05687ADF837C1FE17FF8249FECDFF04358FD92FE296BB67E0DF171FDA87F6D5F05687E101A95AFC41F0BEA16FABEADE0EFD97B4BB1F87769E13D5F40BA93C31E2DD67C4DE3883C6361FDA117C2CF09DEDD7F447F0BFC41E33F15FC39F047893E237806E7E1678FF5BF0BE8DA8F8D3E1C5DEBFA078AE5F0578A2E6C617D77C389E27F0B5F6A5A07886D74CD48DCDB58EB5A6DD793A9D925BDE3DB58CF34B636E01DDD145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451401F8F3FF0469FF927DFF0502FFB4C37FC14F7FF005A77C555FB0D5F8F3FF0469FF927DFF0502FFB4C37FC14F7FF005A77C555FAE1A66BFA16B52EA1068FAD693AB4FA4DC7D8F54874CD46CEFE5D36EC8622D7508ED6695ECEE08473E45CAC72E15BE5F94E0035ABE4BF8C3FB26F84FE22DE5DF88BC3779FF08878A6EB74B746383ED1A16AD72CFB9AE2F6C94A4D697528CA49776122C6C7F7D358DC4E5E47FAD28A00FC7AF117EC8BF1AF42964167A1587896D91884BBD0B57B260EBFBD21BEC9A9C9A6DF0609102CA2D9C6E9638D1A472C17F1FF00F6AEFF008211693FB487C4ED27F68AF047873E367ECB7FB55787351B6D6F44FDA13F67FBC7F08F8B27D7AC123834ED63C49676ED0C5AAEA9616CAF6F0EBFA45E786BC5D3C060B2BEF12DDE99676DA7C7FD82D7CD371FB63FECBB693CF6B75F1C3C076D756D3496F736D71AA3433DBCF0BB473413C3242B24534522B4724722ABC6EACACA18115DD82CB332CC9D48E5D9763B3074545D5582C262314E929B6A0EA2A14EA3829B8C945CADCCE2D2BD99CF88C661309C9F5AC561F0DED39B93EB15E951E7E5B73727B49479B979A3CD6BDB995ED747E307C00F82FF00F070D7C36B1B0F0C6A3FB6EFECFBF1BFC39611A5A5AF8ABF698FD8EECED7C656F601B6DAA6A33FC28FDA27E19EA1E22BEB7B471E76A7AA35E6A379716C8752BB92E27B895BF53BE19FC15FDB73506B6BFF00DA2BF6CEF0FCF2C52979FC2DFB307ECFBE11F853E1BD461276B59EA9ADFC64D63F688F199B39E166477F0D6AFE11D6ADA60B7165AEC0C1513EA1D27E2E7C35D73C5EBE00D2FC63A35C78D5F40B4F142785DA692DB5A93C3F7D69657D6BAA4763771413496F25AEA16931D8AD246AF2096346B7B858897E2EFC3483C55E29F034BE32D1478BFC13E1C9BC5FE2AF0E09DDF56D13C336F6DA75E4DACDE5A246D20B24B6D5B4D977C4246617B02AA976DA13CB3328CDD3797E35548E1D62E50784AEA6B0929C69C714E2E9F32C3CAA4A34D566BD9B9CA3152E6690FEB386E573FAC50E5556545CBDAD3E555A37E6A4DF359558D9F3536F9959DD2B33BFB6B78ED2DADED6269DE2B6822B78DAE6E6E6F6E5A386358D1AE2F2F259EF2EE7655065B9BA9E6B99E42D2CF2C92BBBB7E54FFC15DBFE4947EC6DFF00695DFF00825BFF00EB697C25AFD1FD07E29FC3BF14781EEFE257877C63A16B5E03B0B3D6350BDF1469D78973A55A5A787D6E1F599AE268C178869D1DACF25C23A09162412046478D9BF2E7FE0A89E34F0AFC42F809FB12F8C3C13AF69DE26F0C6AFF00F055DFF8261FF666B7A54DF68B0BDFECFF00DB87E17E977BE44D85DFF66D42CAEED25E06D9ADE45FE1CD455C1636846B4ABE131546387C42C257955C3D5A71A18B719CD616B39C22A9E21C69D492A3371A9CB4E72E5B424D542B51A9ECF92B529FB6A6EB52E4A9097B5A2B92F569D9BE7A6BDA53BD48DE2BDA435F7A37FD84A28A2B94D428A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A002A29E086E619ADAE618AE2DEE229209E09E349619E1950C72C3345206492291199248DD591D18AB0209152D1401E0DF0A7F657FD987E03EBDAE78ABE077ECE3F01BE0CF89FC4D66DA77893C47F0A7E107C3EF877AF78834F6BC4D45AC75CD63C21E1ED1F50D5ACDB508E3BF6B6BFB8B884DE4697453CF5571EF3451400514514005145140051451400572BA2F813C0FE1BF11F8CBC61E1DF06F85341F16FC45BCD1751F883E29D17C3DA4697E23F1DEA1E1CD12D3C33E1EBEF196B7636706A7E27BCD07C376163E1FD16E75BBABE9F4BD12CAD349B1782C2DA1B74EAA8A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A00FE47FC45E1BFDA1FC5BFF000488FF0082CCE85FB3243F11AFBE21DE7FC15D7FE0A00DE20D17E0EC97517C5BF127C1A83F6EAB79FE3CF86BE1B3588FB74DE2BD77E0E47E36D3ECEC34E2354D5ADA7BCD2349126A97F67149CDF82B4AFF00827978BFF6D1FF0082685CFF00C10F7E155CFC3EF899E03F8B924DFB6678A3E117C26F8ABF08BC15E1BFD8887C39F130F895E08FDAE359F15E81E1AD33C4BF123C43E2DFF842EDFE1EDA7C40B8F12FC4483C6BA4DFDEC77D65A89D3AF2EBF417E067C5DF889FF04C1F8B7FB64FC12F8D5FB257ED7FF163E187C6FF00DB0FE3EFED8FF01BF680FD93FF00679F887FB4FF008435FF000D7ED2FE2A5F889E27F871F10F41F845A4F88BC67F0CFE207C36F1B6A3AFE830A788BC3B6FA1F8ABC391699ABE93A9C855E5D43EA2FF0087BB7C28FF00A336FF0082AEFF00E2ADFF006D2FFE74B401F88BFF0004DDF84BFF000570F8C7F01BF66DFDAF3C47FB4F78CBC2FF000EECC7C6DF187C62D57E21FED9BFB41FC5AF1D7C63F87BA18F8C7E0DB6F017FC32EF8C3E05E87F0E7E12F88A3D6EC3C337BA4F8FB42F8EDAB6B1E1FB6F0F43AF69D6B79A96B325869BE15FF04F6FDA9FC7FE30FD933C53E29F8D7FB6AFC6FB9FDAE0FEC37FB4178ABE1C7826CBF6CFFDAF3E20F8BBC4BF19747FD9DFE2BEBFAA4DF133E0DF8EBE067C3AF861F0CBC4BE10D2B4D6F1A784B4FF00067C4AF88CBA7F8CB41B4974FD760BED3B4B875AFE8BFF00E1EEDF0A3FE8CDBFE0ABBFF8AB7FDB4BFF009D2D1FF0F76F851FF466DFF055DFFC55BFEDA5FF00CE96803F1EBF65BFDA27FE0A71E05F8F7FF0465FD97BF6B0D5BE2478F3C15F14359D7BE2EE93FB5FF8575CD774CF0EFED07F06BC49FB177C4BF1A5BFC11FDAAFC3FF00DA6F3D8FC6DF83DF17F54F87D6F6DA96B06E742F8A1A75BE83AEDA0B6F11C5E24B6D5BF563F663F8A7E1AF87BE17B1B1F18FC61BBF0D59DA7C40F1F6A97FF0ED3F67BF127885EE34BBFF001CEBB7D0097C79A77877557BCFEDBB39A2D46DEFECA5FF0042B2BBB5B18E226D0C92F5BFF0F76F851FF466DFF055DFFC55BFEDA5FF00CE968FF87BB7C28FFA336FF82AEFFE2ADFF6D2FF00E74B5EC65B9A470387C661A74275618BAB84AAE54EAD0A7384B09F59E556C460F1B4A719FD61B92749493846D2B3927F3F9BE431CD71D9663655E10FECD86322A854A35AA52AF2C554C0D58BAAE863309270A52C0AE6A3273A55D54E5AD174E32854E5FE27E81AEEAFFB44FC64FDA27E107DBAFF00C5DF07BC0DF017E23782F4CB596FF4BB3F883F0FEE3C3DAD5D7C47F063D918127B91AA784A6D22E96D12C65D56D2F06936305B4377AAC31CB47C15F0A7C69F0F7E3E7C56D77C7A2E2FFC75F11BF611F89BE3FF0089BAD5B25EC9E1F8FC7FE28F89734927877489A60F6D676DE1DF0DE99A1E8567A6C331516DA41BC894C53973DB7FC3DDBE147FD19B7FC1577FF156FF00B697FF003A5A3FE1EEDF0A3FE8CDBFE0ABBFF8AB7FDB4BFF009D2D7D0478DF171CBBFB36382A1ECE59753CB275E53BE26585A3470F0A347DAAA49AA10C452AD8D951D633C4E239E4ED4A9A5E14381E3196226F339BFAC66B8BCDDD18E129C70947178BA95F9EA50A2AB3A91AAB0D56183552788A96A149C69C69AAD5632F2EF83FF03FE2737837E17FC0AD05EE20FD9F7F680F01F817E2BFC58D52FF0051D5E3D47C1D2F842CF4583E31FC3ED2661218F44B8F1F6B973E19F3159ADAE6C85D7896C45BBDA585D7D8BE7DF8D1A7DFE97FF04E5FF826C58EA76577A75F41FF00055DFF00827BF9F677D6D35A5D43E6FF00C148FC37347E6DBDC247347E6432472A6F41BE37475CAB293F69FF00C3DDBE147FD19B7FC1577FF156FF00B697FF003A5AF987E3AFC75F89BFF052CF89BFB22FC05F80BFB22FED87F0BBE1F7C2EFDB0FF66FFDACBE3FFC7FFDACBF66FF0088DFB31F80BC2BE02FD98FE2369BF17B4CF02F8174CF8BDA6F85BC5FF127E23FC49F17F85BC3FE16B4B4F0B787EFB49F0BE937D7FAF6BD7F15B44AA98E7BC658ACF70388C056C251A10C46694336A9529CEF52A62E8D3CCF0EEAD57ECE1ED6B56C26370B87AB5A56728E5B464A29D4928F7643C271C8ABE0EB2CC2AE3160F2FC6E5D4E35A846128D2C656CB312E14E70A8D53C3D1C460313568D0709B87D7EA538D554A85383FDFBA28A2BE34FAF0A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2803FFFD9',5,0,'image/jpeg','logo.jpg',0,NULL),
	(5,2,1,'**********************************************************************\r\n[agnDYN name=\"0.1.1 Header-Text\"/]\r\n**********************************************************************\r\n[agnDYN name=\"0.2 date\"/]\r\n\r\n[agnTITLE type=1],\r\n\r\n[agnDYN name=\"0.3 Intro-text\"/]\r\n[agnDYN name=\"0.4 Greeting\"/]\r\n\r\n----------------------------------------------------------------------[agnDYN name=\"1.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"1.0 Headline ****\"]\r\n\r\n[agnDYN name=\"1.1 Sub-headline\"][agnDVALUE name=\"1.1 Sub-headline\"/]\r\n[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/][agnDYN name=\"1.3 Link-URL\"]\r\n\r\n[agnDYN name=\"1.4 Link-Text\"/]\r\n[agnDVALUE name=\"1.3 Link-URL\"][/agnDYN name=\"1.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"2.0 Headline ****\"]\r\n\r\n[agnDYN name=\"2.1 Sub-headline\"][agnDVALUE name=\"2.1 Sub-headline\"/]\r\n[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/][agnDYN name=\"2.3 Link-URL\"]\r\n\r\n[agnDYN name=\"2.4 Link-Text\"/]\r\n[agnDVALUE name=\"2.3 Link-URL\"][/agnDYN name=\"2.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"3.0 Headline ****\"]\r\n\r\n[agnDYN name=\"3.1 Sub-headline\"][agnDVALUE name=\"3.1 Sub-headline\"/]\r\n[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/][agnDYN name=\"3.3 Link-URL\"]\r\n\r\n[agnDYN name=\"3.4 Link-Text\"/]\r\n[agnDVALUE name=\"3.3 Link-URL\"][/agnDYN name=\"3.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"3.0 Headline ****\"]\r\n\r\nImprint\r\n\r\nYou want to change your profile-data?\r\n[agnDYN name=\"9.0 change-profil-URL\"/]\r\n\r\nPlease click her to unsubscribe:\r\n[agnDYN name=\"9.1 unsubscribe-URL\"/]\r\n\r\n[agnDYN name=\"9.2 imprint\"/]',NULL,0,0,'text/plain','agnText',0,NULL),
	(6,2,1,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table bgcolor=\"#808080\" width=\"684\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr>\r\n    <td>[agnDYN name=\"0.1 Header-image\"]\r\n    	<table width=\"680\" border=\"0\"  bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td><img src=\"[agnDVALUE name=\"0.1 Header-image\"]\" width=\"680\" height=\"80\" alt=\"[agnDYN name=\"0.1.1 Header-Text\"/]\" border=\"0\"></td>\r\n            </tr>\r\n        </table>[/agnDYN name=\"0.1 Header-image\"]\r\n        <table width=\"680\" border=\"0\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td><td align=\"right\"><div style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 10px;\">[agnDYN name=\"0.2 date\"/]</div></td><td width=\"10\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td>\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n                 <tr><td><p><b>[agnTITLE type=1],</b></p><p>[agnDYN name=\"0.3 Intro-text\"/]</p><p>[agnDYN name=\"0.4 Greeting\"/]</p></td></tr>\r\n                 <tr><td><hr noshade></td></tr>\r\n              </table>[agnDYN name=\"1.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"1.5 Image-URL\"]<td>[agnDYN name=\"1.3 Link-URL\"]<a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[/agnDYN name=\"1.3 Link-URL\"]<img src=\"[agnDVALUE name=\"1.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"1.3 Link-URL\"]</a><!-- [agnDVALUE name=\"1.3 Link-URL\"] -->[/agnDYN name=\"1.3 Link-URL\"]</td>[/agnDYN name=\"1.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"1.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"1.1 Sub-headline\"]<b>[agnDVALUE name=\"1.1 Sub-headline\"/]</b><br>[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/]</p>[agnDYN name=\"1.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[agnDYN name=\"1.4 Link-Text\"/]</a></p>[/agnDYN name=\"1.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"1.7 Image-URL-1\"]<td>[agnDYN name=\"1.6 Link-URL\"]<a href=\"[agnDVALUE name=\"1.6 Link-URL\"]\">[/agnDYN name=\"1.6 Link-URL\"]<img src=\"[agnDVALUE name=\"1.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"1.6 Link-URL\"]</a><!-- [agnDVALUE name=\"1.6 Link-URL\"] -->[/agnDYN name=\"1.6 Link-URL\"]</td>[/agnDYN name=\"1.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"2.5 Image-URL\"]<td>[agnDYN name=\"2.3 Link-URL\"]<a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[/agnDYN name=\"2.3 Link-URL\"]<img src=\"[agnDVALUE name=\"2.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"2.3 Link-URL\"]</a><!-- [agnDVALUE name=\"2.3 Link-URL\"] -->[/agnDYN name=\"2.3 Link-URL\"]</td>[/agnDYN name=\"2.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"2.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"2.1 Sub-headline\"]<b>[agnDVALUE name=\"2.1 Sub-headline\"/]</b><br>[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/]</p>[agnDYN name=\"2.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[agnDYN name=\"2.4 Link-Text\"/]</a></p>[/agnDYN name=\"2.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"2.7 Image-URL-1\"]<td>[agnDYN name=\"2.6 Link-URL\"]<a href=\"[agnDVALUE name=\"2.6 Link-URL\"]\">[/agnDYN name=\"2.6 Link-URL\"]<img src=\"[agnDVALUE name=\"2.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"2.6 Link-URL\"]</a><!-- [agnDVALUE name=\"2.6 Link-URL\"] -->[/agnDYN name=\"2.6 Link-URL\"]</td>[/agnDYN name=\"2.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"3.5 Image-URL\"]<td>[agnDYN name=\"3.3 Link-URL\"]<a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[/agnDYN name=\"3.3 Link-URL\"]<img src=\"[agnDVALUE name=\"3.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"3.3 Link-URL\"]</a><!-- [agnDVALUE name=\"3.3 Link-URL\"] -->[/agnDYN name=\"3.3 Link-URL\"]</td>[/agnDYN name=\"3.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"3.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"3.1 Sub-headline\"]<b>[agnDVALUE name=\"3.1 Sub-headline\"/]</b><br>[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/]</p>[agnDYN name=\"3.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[agnDYN name=\"3.4 Link-Text\"/]</a></p>[/agnDYN name=\"3.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"3.7 Image-URL-1\"]<td>[agnDYN name=\"3.6 Link-URL\"]<a href=\"[agnDVALUE name=\"3.6 Link-URL\"]\">[/agnDYN name=\"3.6 Link-URL\"]<img src=\"[agnDVALUE name=\"3.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"3.6 Link-URL\"]</a><!-- [agnDVALUE name=\"3.6 Link-URL\"] -->[/agnDYN name=\"3.6 Link-URL\"]</td>[/agnDYN name=\"3.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" bgcolor=\"#D3D3D3\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr><td><h1>Imprint</h1>\r\n                 	 <p>You want do change your profile-data?<br><a href=\"[agnDYN name=\"9.0 change-profil-URL\"/]\">change profile</a></p>\r\n                 	 <p>Please click her to unsubscribe:<br><a href=\"[agnDYN name=\"9.1 unsubscribe-URL\"/]\">unsubscribe newsletter</a></p>\r\n                         <p>[agnDYN name=\"9.2 imprint\"/]</p></td></tr>\r\n              </table>              \r\n              </td>\r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\"><img src=\"[agnIMAGE name=\"clear.gif\"]\" width=\"8\" height=\"8\"></td>\r\n            </tr>            \r\n        </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,0,0,'text/html','agnHtml',0,NULL),
	(7,2,1,'R0lGODdhAQABAIgAAP///wAAACwAAAAAAQABAAACAkQBADs=','47494638376101000100880000FFFFFF0000002C00000000010001000002024401003B',5,0,'image/gif','clear.gif',0,NULL),
	(8,2,1,'/9j/4AAQSkZJRgABAQEAYABgAAD/4QBmRXhpZgAASUkqAAgAAAAEABoBBQABAAAAPgAAABsBBQAB\r\nAAAARgAAACgBAwABAAAAAgAAADEBAgAQAAAATgAAAAAAAABgAAAAAQAAAGAAAAABAAAAUGFpbnQu\r\nTkVUIHYzLjIyAP/bAEMAAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/bAEMBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/AABEIAFACqAMBIgACEQEDEQH/xAAf\r\nAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEF\r\nEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJ\r\nSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\r\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEB\r\nAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIy\r\ngQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNk\r\nZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfI\r\nycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/AP3v+D/wf+Kf/BU74p/t\r\njfF/4v8A7Y37YfwK+E3wK/bD+PP7HnwF+Av7Hnx58Vfs0aJpWifs0eKv+FeeLvif8T/F3w7+weNP\r\niT45+JXjSw13VrCw1bXf+EY8HeGP7MsLDTLi/uHfSfoP/hzT8Pv+kgX/AAWG/wDFnn7Tv/zVUf8A\r\nBGn/AJJ9/wAFAv8AtMN/wU9/9ad8VV+wjsqKzuyoiKWd3IVVVQSzMxICqoBJJIAAJJxQB+Pf/Dmn\r\n4ff9JAv+Cw3/AIs8/ad/+aqj/hzT8Pv+kgX/AAWG/wDFnn7Tv/zVV9P/ABP/AOCin7NHw6mvNO0G\r\n/wDif8fvEFi08N14e/ZT+B/xh/ahvrK+hwradqt38C/BPjvSNFv0naK3uLPVtSs7uyknia8ggjcP\r\nX5h/HH/g4Z+EXwFaa9+LP7LP7aPwF8HwypG3xD+Pn7H37TPgTwmqMWYTyzX/AML7CZIpoyhiVPNn\r\nRhIJISRsAB9O/wDDmn4ff9JAv+Cw3/izz9p3/wCaqj/hzT8Pv+kgX/BYb/xZ5+07/wDNVXBfs+/8\r\nFkPh1+1P4dl8Wfs/eM/gr8VtFsmt/wC14vDOoa4uvaF9pMn2eHxJ4Z1HVLTxN4WubtYZWtIfEOia\r\nfNcIjTQwzRAk/XHhz9u6BpVj8XeApYoC0Qa78OaolxKi9JmXTtTjtkkbPzxKdUiH/LN348wgHgn/\r\nAA5p+H3/AEkC/wCCw3/izz9p3/5qqP8AhzT8Pv8ApIF/wWG/8WeftO//ADVV+nHw8+MXw9+KELN4\r\nS1+C5vYkMlzot4psdatYw5QySafPiSWAEKTc2hubZd8avMsjbK9OoA/Hn/hzT8Pv+kgX/BYb/wAW\r\neftO/wDzVV4N8XfgZ8W/+CYPxE/ZK+NXwT/bJ/bD+N/ww+LH7X/7PP7J/wC0B8Bv2x/j74q/aX8N\r\na/4P/af+Iek/CLQfiH8OPE/xEXUfG3w2+IHwz8ZeIvDviJIdB1+Lw54q0O31PSdX'TJK'+ofv/X5\r\nRf8ABXb/AJJR+xt/2ld/4Jb/APraXwloA/V2iiigAooooAKKKKACiiigAooooAKKKKACiiigAooo\r\noAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiig\r\nAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAC\r\niiigAooooA/Hn/gjT/yT7/goF/2mG/4Ke/8ArTviqv2BngguoJra5hiuLa4ikguLeeNJoJ4JkMcs\r\nM0UgaOWKWNmSSN1ZHRirAqSK/H7/AII0/wDJPv8AgoF/2mG/4Ke/+tO+Kq/YagBqqqKqIqoiKFRF\r\nAVVVQAqqoACqoAAAAAAAAxVPVI9Mm03UYtaSxl0eWxu49Wj1Rbd9Nk'7eRb9NRS7BtXsXtTKt2t'\r\n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\r\n+p6H+zn+'8O'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\r\nbh9ZmuJowXiGnR2s8lwjoJFiQSBGR42b8uf+ConjTwr8QvgJ+xL4w8E69p3ibwxq/wDwVd/4Jh/2\r\nZrelTfaLC9/s/wDbh+F+l3vkTYXf9m1Cyu7SXgbZreRf4c1FXBY2hGtKvhMVRjh8QsJXlVw9WnGh\r\ni3Gc1haznCKp4hxp1JKjNxqctOcuW0JNVCtRqez5K1KftqbrUuSpCXtaK5L1adm+emvaU71I3iva\r\nQ196N/2EooorlNQooooAKKKKACiiigAooooAKKKKACiiigAooooAKinghuYZra5hiuLe4ikgngnj\r\nSWGeGVDHLDNFIGSSKRGZJI3VkdGKsCCRUtFAHg3wp/ZX/Zh+A+va54q+B37OPwG+DPifxNZtp3iT\r\nxH8KfhB8Pvh3r3iDT2vE1FrHXNY8IeHtH1DVrNtQjjv2tr+4uITeRpdFPPVXHvNFFABRRRQAUUUU\r\nAFFFFABXK6L4E8D+G/EfjLxh4d8G+FNB8W/EW80XUfiD4p0Xw9pGl+I/HeoeHNEtPDPh6+8Za3Y2\r\ncGp+J7zQfDdhY+H9Fudbur6fS9EsrTSbF4LC2ht06qigAooooAKKKKACiiigAooooAKKKKACiiig\r\nAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAC\r\niiigAooooAKKKKACiiigD+R/xF4b/aH8W/8ABIj/AILM6F+zJD8Rr74h3n/BXX/goA3iDRfg7JdR\r\nfFvxJ8GoP26ref48+Gvhs1iPt03ivXfg5H420+zsNOI1TVrae80jSRJql/ZxSc34K0r/AIJ5eL/2\r\n0f8Agmhc/wDBD34VXPw++JngP4uSTftmeKPhF8Jvir8IvBXhv9iIfDnxMPiV4I/a41nxXoHhrTPE\r\nvxI8Q+Lf+ELt/h7afEC48S/ESDxrpN/ex31lqJ068uv0F+Bnxd+In/BMH4t/tk/BL41fslftf/Fj\r\n4YfG/wDbD+Pv7Y/wG/aA/ZP/AGefiH+0/wCENf8ADX7S/ipfiJ4n+HHxD0H4RaT4i8Z/DP4gfDbx\r\ntqOv6DCniLw7b6H4q8ORaZq+k6nIVeXUPqL/AIe7fCj/AKM2/wCCrv8A4q3/AG0v/nS0AfiL/wAE\r\n3fhL/wAFcPjH8Bv2bf2vPEf7T3jLwv8ADuzHxt8YfGLVfiH+2b+0H8WvHXxj+Huhj4x+DbbwF/wy\r\n74w+Beh/Dn4S+Io9bsPDN7pPj7Qvjtq2seH7bw9Dr2nWt5qWsyWGm+Ff8E9v2p/H/jD9kzxT4p+N\r\nf7avxvuf2uD+w3+0F4q+HHgmy/bP/a8+IPi7xL8ZdH/Z3+K+v6pN8TPg346+Bnw6+GHwy8S+ENK0\r\n1vGnhLT/AAZ8SviMun+MtBtJdP12C+07S4da/ov/AOHu3wo/6M2/4Ku/+Kt/20v/AJ0tH/D3b4Uf\r\n9Gbf8FXf/FW/7aX/AM6WgD8ev2W/2if+CnHgX49/8EZf2Xv2sNW+JHjzwV8UNZ174u6T+1/4V1zX\r\ndM8O/tB/BrxJ+xd8S/Glv8Ef2q/D/wDabz2Pxt+D3xf1T4fW9tqWsG50L4oadb6DrtoLbxHF4ktt\r\nW/Vj9mP4p+Gvh74XsbHxj8Ybvw1Z2nxA8fapf/DtP2e/EniF7jS7/wAc67fQCXx5p3h3VXvP7bs5\r\notRt7+yl/wBCsru1sY4ibQyS9b/w92+FH/Rm3/BV3/xVv+2l/wDOlo/4e7fCj/ozb/gq7/4q3/bS\r\n/wDnS17GW5pHA4fGYadCdWGLq4Sq5U6tCnOEsJ9Z5VbEYPG0pxn9YbknSUk4RtKzkn8/m+QxzXHZ\r\nZjZV4Q/s2GMiqFSjWqUq8sVUwNWLquhjMJJwpSwK5qMnOlXVTlrRdOMoVOX+J+ga7q/7RPxk/aJ+\r\nEH26/wDF3we8DfAX4jeC9MtZb/S7P4g/D+48Pa1dfEfwY9kYEnuRqnhKbSLpbRLGXVbS8Gk2MFtD\r\nd6rDHLR8FfCnxp8Pfj58Vtd8ei4v/HXxG/YR+Jvj/wCJutWyXsnh+Px/4o+Jc0knh3SJpg9tZ23h\r\n3w3pmh6FZ6bDMVFtpBvIlMU5c9t/w92+FH/Rm3/BV3/xVv8Atpf/ADpaP+Hu3wo/6M2/4Ku/+Kt/\r\n20v/AJ0tfQR43xccu/s2OCoezll1PLJ15TviZYWjRw8KNH2qpJqhDEUq2NlR1jPE4jnk7UqaXhQ4\r\nHjGWIm8zm/rGa4vN3RjhKccJRxeLqV+epQoqs6kaqw1WGDVSeIqWoUnGnGmq1WMvLvg/8D/ic3g3\r\n4X/ArQXuIP2ff2gPAfgX4r/FjVL/AFHV49R8HS+ELPRYPjH8PtJmEhj0S48fa5c+GfMVmtrmyF14\r\nlsRbvaWF19i+ffjRp9/pf/BOX/gmxY6nZXenX0H/AAVd/wCCe/n2d9bTWl1D5v8AwUj8NzR+bb3C\r\nRzR+ZDJHKm9BvjdHXKspP2n/AMPdvhR/0Zt/wVd/8Vb/ALaX/wA6WvmH46/HX4m/8FLPib+yL8Bf\r\ngL+yL+2H8Lvh98Lv2w/2b/2svj/8f/2sv2b/AIjfsx+AvCvgL9mP4jab8XtM8C+BdM+L2m+FvF/x\r\nJ+I/xJ8X+FvD/ha0tPC3h++0nwvpN9f69r1/FbRKqY57xlis9wOIwFbCUaEMRmlDNqlSnO9Spi6N\r\nPM8O6tV+zh7WtWwmNwuHq1pWco5bRkop1JKPdkPCccir4Osswq4xYPL8bl1ONahGEo0sZWyzEuFO\r\ncKjVPD0cRgMTVo0HCbh9fqU41VSoU4P9+6KKK+NPrwooooAKKKKACiiigAooooAKKKKACiiigAoo\r\nooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiii\r\ngAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKA\r\nCiiigAooooA//9k=','FFD8FFE000104A46494600010101006000600000FFE1006645786966000049492A000800000004001A010500010000003E0000001B010500010000004600000028010300010000000200000031010200100000004E00000000000000600000000100000060000000010000005061696E742E4E45542076332E323200FFDB00430001010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101FFDB00430101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101FFC0001108005002A803012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F00FDEFF83FF07FE29FFC153BE29FED8DF17FE2FF00ED8DFB61FC0AF84DF02BF6C3F8F3FB1E7C05F80BFB1E7C79F157ECD1A2695A27ECD1E2AFF8579E2EF89FF13FC5DF0EFEC1E34F893E39F895E34B0D7756B0B0D5B5DFF8463C1DE18FECCB0B0D32E2FEE1DF49FA0FFE1CD3F0FBFE9205FF000586FF00C59E7ED3BFFCD551FF000469FF00927DFF000502FF00B4C37FC14F7FF5A77C555FB08ECA8ACEECA888A59DDC85555504B3331202AA8049248000249C5007E3DFFC39A7E1F7FD240BFE0B0DFF008B3CFDA77FF9AAA3FE1CD3F0FBFE9205FF000586FF00C59E7ED3BFFCD557D3FF0013FF00E0A29FB347C3A9AF34ED06FF00E27FC7EF1058B4F0DD787BF653F81FF187F6A1BEB2BE870ADA76AB77F02FC13E3BD2345BF49DA2B7B8B3D5B52B3BBB292789AF2082370F5F987F1C7FE0E19F845F015A6BDF8B3FB2CFEDA3F017C1F0CA91B7C43F8F9FB1F7ED33E04F09AA316613CB35FF00C2FB099229A3286254F36746120921246C001F4EFF00C39A7E1F7FD240BFE0B0DFF8B3CFDA77FF009AAA3FE1CD3F0FBFE9205FF0586FFC59E7ED3BFF00CD55705FB3EFFC1643E1D7ED4FE1D97C59FB3F78CFE0AFC56D16C9ADFF00B5E2F0CEA1AE2EBDA17DA4C9F6787C49E19D4754B4F13785AE6ED6195AD21F10E89A7CD7088D3430CD1024FD71E1CFDBBA069563F17780A58A02D106BBF0E6A89712A2F499974ED4E3B6491B3F3C4A754887FCB377E3CC201E09FF000E69F87DFF004902FF0082C37FE2CF3F69DFFE6AA8FF008734FC3EFF00A4817FC161BFF1679FB4EFFF003555FA71F0F3E317C3DF8A10B3784B5F82E6F624325CE8B78A6C75AB58C3943249A7CF89258010A4DCDA1B9B65DF1ABCCB236CAF4EA00FC79FF8734FC3EFFA4817FC161BFF001679FB4EFF00F3555E0DF177E067C5BFF8260FC44FD92BE357C13FDB27F6C3F8DFF0C3E2C7ED7FFB3CFEC9FF00B407C06FDB1FE3EF8ABF697F0D6BFE0FFDA7FE21E93F08B41F887F0E3C4FF111751F1B7C36F881F0CFC65E22F0EF88921D075F8BC39E2AD0EDF53D2757D314C92BEA1FBFF5F945FF000576FF009251FB1B7FDA577FE096FF00FADA5F096803F5768A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A00FC79FF8234FFC93EFF82817FDA61BFE0A7BFF00AD3BE2AAFD819E082EA09ADAE618AE2DAE22920B8B79E349A09E0990C72C33452068E58A58D99248DD591D18AB02A48AFC7EFF008234FF00C93EFF0082817FDA61BFE0A7BFFAD3BE2AAFD86A006AAAA2AA22AA22285445015555400AAAA000AAA0000000000003154F548F4C9B4DD462D692C65D1E5B1BB8F568F545B77D364D31EDE45BF4D452EC1B57B17B532ADDADC836ED6E641303196ABD5F8EFF00F0563F8EFADF85BC39E0CF81FE19D46E34D6F1CDB5F789BC713DA5C3DBDD5D785ECE76D2F47D019A29773E97AE6A6355BAD5A378E2F3BFB06C6D5659AD2E752B66F7F86320AFC4D9E60325A1515178BA92F6B5DA52F6187A30956C4565072873CA14A1270A7CD1739F2C2EAF73C5E21CEF0FC3B93E3737C4D39D5A784843968D36A33AD5AB55850A3494A5A454AAD48F3CED274E9A9D4509F2F2BFE6ABFE0ABFFF0004EFF813E10FDAC7E1FF00ED87FF000463F8A1F0F7F67DFDA1F4CF145DC3F1C3C01A3DA788F41FD9ABC5FA749135CCDAC78793C25E1BD63405FED9BAB0B7D23C71E08F0DE9175F0EBC6963AA5B7896C6EFC3FE28D1B559BC4DFB0FF0DBC4BAA78C7E1FF83BC51AE5869DA56BBAD6836573AFE95A3EA33EADA4E99E218435978834FD2B53BBB1D2EFB50D2ACF5BB5BFB7D2F50BFD2749BDD474E8ED6FEE34BB16BAFB347F9BB5F66FECFDE22D02CFC0B7961AAF883C37A45EDBF89F519123D6BC47A2E8D3CB6171A6E8A60315B6A77B66F2C29771EA07ED1189373C8D13B2886351FAEF1CF85795E41C370C7E4BFDA78CCC70F8AC353C47B492AFF58A35A32A75250C3D1A3174DC6B7B3A917072E4839C67CF75387E55C19E25E639D7103C16732CBF0982C4E1ABCB0D1A54A54A34B134631AB18CF115B112B4274215DCA551C94AB28429AA7CEA2BE9ED1B59D57C3DAAD86B7A25FDCE99AB699731DDD85FDA48639EDA78CFCAE8DC86560592589C345344CF0CC8F13BA37ECEFC01F8B69F17BC0D0EAF74B043E22D2A71A5788ED60431442F9214962BEB78999CC76BA842DE6C4A1DD5264B9815BF7240FE747C71FB587ECADF0CB5C3E19F889FB4EFECEBE07F118B4B7BF6D0BC55F1C7E17E85ABA595D9905ADD3E9FA8F8A6DEE920B8F2A43048D1059550B2165C1AFD58FF008273F8AAD3C7563AAF8FBC07AC5978D3E0FF008EBC3AF7FE18F887E12BFB5F11FC3CF145FF00873C4773A05C9F0EF8BF477BDF0F6B53E9F7CBAF6957A34BD52736D7BA6EA165749F69B1952DBF059C274A73A75212A75212709C271709C271769467192528CA2D34E2D269AB3573F6CA75215611A94A70A94E6B9A13A725384A2F671945B8C93EE9B47EA357E517FC15DBFE4947EC6DFF00695DFF00825BFF00EB697C25AFD5DAFCA2FF0082BB7FC928FD8DBFED2BBFF04B7FFD6D2F84B5259FABB4514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145001451450014514500145145007E3CFF00C11A7FE49F7FC140BFED30DFF053DFFD69DF1557EC357E3CFF00C11A7FE49F7FC140BFED30DFF053DFFD69DF1557EC35007E49FF00C1593C07A57ED17F03BC5FFB207C445BC8FE0EFC74F0AE843C5B77E1DB97D27C6305D7847E2068DE31B16D0B5D923BCB2B068F54F0CE82D309B4BBF2F6E6EA22B179F1C8BF891FB2FF00FC135BF678FD927C09ACFC3BF85FA87C4BBFF0F6B9E2DBCF1A5D9F19F8AEC35CD422D66FF47D0F43BA16D796BE1FD28C766F63E1ED376DB491CA239D66951D7CF75AFE9F7F6AFF0085177F117C0906ADA159BDE789FC1D3CD7D656D0286B8D434ABB1147AC58C2814B4B3AAC16D7F6F1EE058D9CB0C4AF2DC2A9FC85AEECBB33CC329C5471B96632BE071508CE11AF87A8E9D450A8B9670BADE325BC5DD3B276BA4D726330182CC68FD5F1F84C3E370FCF1A8E862A8D3AF45CE37E594A9548CA1271BB71E68BE5766ACD26BE40F8BDF097C2DE0AF0741E20D11F52176DE26D2F47912F2E96E226B7BED2F5FBD7750B0C4564497498403F302B23F4C0CFCCD5FA23F17B47BFD73E19F8D6CF46D17FE121F1041A0DEEA9E1BD1A39ED2CEEB54F1068F1FF006A695A459EA1A84D05869B71AEDCDA2E82751BD905AD9DBEA9713CFF00BB4247C99FF0407FDA4BF63EFF0082827C43F8A9A0FC47D275FF00875FB54FC03F184D3597ECC5F11EFF004B8CEADE13D32DEDED2EBC6E2C2E74CB1BFF00146A7E14F1947AA68BE2AF09C4908F04CF63A1DEF88ED750B7F13594765FD03C1FE2B65F97F0B6225C459862F1F9E61B1589F63869AA957138DA328D29E1DC710E9FB0A74D549CE949D4A8EA5354E751539C7962FF000CE2FF000DB31CC78969BC832FC160F2AAF86C2FB6AB4BD8E130983AA9D5A755BC3C5C65524E34A35251C2D1A8DB9C5D45073E797E897C27FF0082147FC13B7F695F861E02F8C1FB647ECA9E1EF8A5F1B3C57E1E8AF2F3C41AC78CFE2D7876FECFC273DEDEDE783B469F4EF0A78FFC3DA4C72DAE87776D773E349B5BB86E6FE7B4BB33CD6A6E25FD97FD9BFF0066DF825FB22FC18F06FECF5FB3A7812CFE19FC1BF87CBAEAF83BC0FA7EABE20D6ACB425F13789758F186BAB6FA8F8A757D735B99751F12F88359D5A5177A9DC08EE2FE648045008E14F71A2BF01CDF33AF9CE698FCD712A2ABE3F155B15523149460EACDCA34E364AF1A71E5A716D733514E4DC9B6FF006FCAB010CAF2CCBF2DA73F690C060B0D83551C541D5FABD1852755C13928CAAB8BA925CD2B4A4F57B857E517FC15DBFE4947EC6DFF00695DFF00825BFF00EB697C25AFD5DAFCA2FF0082BB7FC928FD8DBFED2BBFF04B7FFD6D2F84B5E71DE7EAED14514005145140057E3AFF00C15DFF006B5FDACBF65087F624BDFD917C296BF12BC5BF133F6A6D5BC37E3EF83CDA6E8F75ACFC65F85BE04FD9DBE37FC6BF17FC34F07EA7AADBCF2F87BC73E25B3F8642DBC19AAE9924178DE261A5584A2EEC2F2F34FBBFD8AAFCFEFDB1BE027C4DF8BDFB407FC1367C7BE04D1AD354F0CFECE7FB5C78AFE2C7C58BDB8D634BD365D0BC11AA7ECA7FB44FC2DB2D4ACECF50BAB7BAD6EE24F1A7C41F0B69ADA769115E5FC705F4BA84902D8D95DCF080784EB7FB7BDE7C51FDA7FF00E09067F673F1ED86ABFB347EDCBE14FDAEBC5FE3281B44D0AF350F10D97C2FF81DA178CBC13A65D5FDCDBDF6AFE10F10782FC6375AAE9DE2BD174AD46CAEEDF5CB0D47C3BAFACCDA7C96D1FA87C38FF82A87ECDFF137C6DE07D0F48F0B7C7BD13E18FC59F1EC9F0B3E0AFED39E2FF841ADF86FF66CF8CFF119B52D5F49D2BC29F0F3E215EDC9D46F1FC59A8683ABDB780BC45AEF86740F067C429AD218BC11E25D7E5D57454D4BF3FF00C55FF04A7F8D7E10FF0082ABFC11F8B9F06EEF48B6FF00827DEAD07ED99F12BC7FE07D3358B4F0E78A7F678FDA1BF69DF81F77F0DBE246B7F0EA2FED2B0D5AE7C13F1AB5DB7F0D78E1748F0CC7772F82BE257FC26FAEC29A0691AEDBF99E25FB337FC1307E30780740FD92FF0066FF001D7EC4DA8CB17ECE5F15BE164DE36FDA3FC6BFF050DFDA2FC79FB2FF0089BE1DFECFDE27B4F117823E23FC1FFD99346FDA634BF1068BF17FC4171E14F09EB9E17F0378CBE0DE8BF0ABE1978C9DE694F887C3FA74162403F6525FF82997ECAD67E09FD95BC73AC6BDE2ED06D3F6C4FDA0EEFF0065FF00841A0EB1E0DD52D7C591FC62D33C5DE30F87BAEE81E33D050CD71E16B1F0EF8FFC15A9782B5CD72E9E7D2AC7C43A86816A6E5E0D66D2E5A87C49FF00829A7ECFBE02F14F887E1E787FC39F193E34FC53D1FE32F8ABE03E8DF0B7E09FC3F5F1978D3C75F103E1E7C33F047C58F8A50F83A3BDD6B42D066D13E16785BE2178661F1EEBFAFEBDE1ED3748F10DDBF87609AFB5544B797F2CFE35FF00C12B7F690F885F1A7FE0A1771A6E99A48F84707847E2BFC7AFF82795EC7E2DF0F41E228BF6CCFDA0F5FF00D9FBE3BF8F64BA49E686F7C17A7F863F687FD96F45BDD3F59D727B3D36E348F8D3E2BFB1CCD047A99B6EF3C4DFB007C5E93F64BFD91B47F8C3FB24F82FF6A3F8983E287C72FDA5BF6B2D2BE1C7C6097E047ED43F0C3F687FDA62F7C51F1275EF127ECADFB40E83F147E0FF0086F4C4F02F8C7C61A8FC34F1925E7C41D1EDFC79E07D0FC2579A64B70DA5323807D6371FB7D6A1F167F680FF00826CE9FF00052EFC61E10F86BFB407C6DFDB1FE11FC7AF877F13FE1AB783FE21E97E26FD9E7E02FC4ED76E3C15E22D2BC51A649ADF85B5AF077C4FF05C2F737BE18D49B4BD7ED2056B4D5F5BF0FDFC32CFE8FF000E3FE0AA1FB37FC4DF1B781F43D23C2DF1EF44F863F167C7B27C2CF82BFB4E78BFE106B7E1BFD9B3E33FC466D4B57D274AF0A7C3CF8857B72751BC7F166A1A0EAF6DE02F116BBE19D03C19F10A6B4862F0478975F9755D15352F84BE11FEC45FB71F88FC4BFB126A1FB426A5E39D6BC25F0B7F686FDBDFC412BFC41F8DBA0FC4FF008E9F01BF66CF8EDFB2EF8E7E13FC1CF04FC40F8BF617D69AAFC5DF89563E37F12EA576DE26F0D6A9E32BCF0CE8DAF687A3CDE2CBDB1F09C7A927CFFF00B337FC1307E30780740FD92FF66FF1D7EC4DA8CB17ECE5F15BE164DE36FDA3FC6BFF00050DFDA2FC79FB2FF89BE1DFECFDE27B4F117823E23FC1FF00D99346FDA634BF1068BF17FC4171E14F09EB9E17F0378CBE0DE8BF0ABE1978C9DE694F887C3FA74162403D3E7FF82D37C718BE06FED41F1513F676F15A6AFF0009FF00E0ABDF0C3F623F07E9379F0AFC4115A45F087C71F17FE19FC37D406B70278E85DDEFC70D2F49D4BC50D7F671DDDAE85A6FC40F19FC2FD19F4ABAD2F559205FD13F883FF0555F807F0F75EF166912FC2FFDA8BC67A5FC22D1BC33ACFED31E34F871F023C41E35F087ECA43C4BE0CD23E2249A37C77BED26F1F52B0F14784FC09AE697E2DF1F785FE1EE93F10F5EF04683771DFF0088AC2C616527F39BC69FB0D7ED956BF087FE0A0BF0B340F82565E28BCF117FC1593E13FF00C147FE016BB69F14BE1D699A5FC74F06DA7ED17F02FE33F89FE17DB43AC6B967A8FC38F18785F45F855A869175ABF8FAD74CD0354D535289B449EFED2D84F75E7FE38FF827B7ED0FA1F8DBF6C6D6A2FD89FE277ED01A9FED9FE2FBFF00DA17C01AAD97FC14ABE2BFC0DF855F05BE227C60F86FE10F0CF8FBE077ED45F0D7E1F7ED0DF0CA0F14782FE19F8BF40BFB81E34F81DE17F8AB7FF10BE1E5DDB786ACFF00B08D8693A5E8E01FB27E2AFF00828D7C05D13F68AF037ECBFE11D03E30FC6AF89DE3BF0C7C1BF8896F37C0DF86DA8FC49F057867E11FC70F107893C35E12F8CFE34F1BE93769E1EF0E7C29B1D4BC39BFC41E2EB9BC92DAD34FD6B49D4F4D8756B18F5B9F46ADFF000528F8F7F137F672F801E05F1EFC27D66D342F136B9FB5C7EC4FF09F51BDBCD1F4BD7229BC11F19FF6ADF849F0B7E20E9AB67ABDADE5AC571AB782FC57ADE9B6DA8C512DFE973DCC7A869D3DB5F5B5BCF1F8DFECBBFB1478E7E0A7ED63F1635BBEF0DE91E18F815A8FFC138FF613FD933C11A8FC3CF19788F4F8ED3C47FB3F6A3FB47E9FE33D0BC212EABE30D6FE3078774BF0D787FC7DE0D6F09F8AFC47E25BEF144B1DD433FF00C25BAAF88F4CD4B515E17F6DAFD82BC77A77EC9D7FE10FD970FC7DFDA27E225B7ED41FB15FC723E08F8F1FB59FC40F8997FA9E87FB39FED31F0EFE2C789B4AF06F8B7F694F88FADE85E07B9D43C31E1FD6BCD86CF52D22D75CBF874B5BF5BC9ACAC16200FD4BF8DDF1B3E18FECE5F0A7C69F1B3E3278A2DFC1BF0D7E1FE971EADE27F104F67A8EA4F6D15CDF5A693A6D958693A3DA6A1AC6B5ACEB5ACEA1A7689A0E87A3D85F6AFADEB5A8D8695A659DD5F5E5BC127C9DF0B7FE0A4DF023C77E2BF1CF80BE21F84BE387ECB5E3AF01FC21F11FED0977E13FDA9FE185E7C29D47C41F01BC1D76961E30F8B3E15BE8F52F10E81ABF86FC1D773D8C5E32D29B58B5F1AF83C6ABA3CDE28F0AE9106AB612DC7CC3FB48E93FB74FEDD5FB3F7C43F84977FB1A5EFECA9E38F076B9F057E3F7C1EF13FC4DFDA2BE0F78F7C07F113E28FECF5F1F3E187C63F0FF00C20F16DAFC1BD5FC55E26D07C39F10A0F065FE87ABF89AE34B9F4FD1ADA7376F6F7D3A43653F897ED6DFB3E7ED0BFB7C3F8FBE26FED35F0C6EFF00E09FDF047E077EC11FB71FC26875BF157C4EF007C54F88DACF8E3F6A1F879E0CD1FC57F102283E07EB9E3AF0FE8FF08BE0F7857E1C5EEA6D2DFEAD6DF10FC6DAA6A515BDBF83746D3ED276B900FB97E0FF00FC1537E037C5FF00891F04BE190F861FB50FC2DD4BF6964F11EA5FB3DF88BE347C07F11FC3AF067C5EF087853E18EBDF1735AF1C786FC43A95CCF1695A25B783B425BBFEC8F1A41E15F1C97D6F4199BC209A65ECBA8DAC5F0E3FE0AB5FB34FC4CF1778074CD2BC31F1F7C3FF000B3E3078FA1F857F047F69CF1B7C1AF10F853F671F8CFF00126FB56D4B43D0BC23E00F1DEA52A6B135C78BF55D1F53B1F01EBBE23F0AF877C21E3DB9B6860F06F88B5C9F53D1E3D43F31EF3E2D7ED01FB4CFC7CFF824AFC0DF8DFF000A3E157C23D13C47A27ED27771F8DFE13FED05E17F8D33FC58D1B50FF827C7C6FF008743E357C16B3F0968F05C786BE05349E34DFA66BDF119F42F135E6BDE28F0368D078784967AA5DBFAAE8DFB357EDE1F11FF0066DFD88FFE09E1F147F673F0CF827C19FB2E7C50FD90EEBE2B7ED6DA57C64F02DF7C37F1EFC2EFD87BC79E06F1B782EFFE0DFC38B4D435FF008C69E3FF008C0DF0AFC21A76A7A1FC49F06784BC3BE084D6FC4777FF00091EB6D61A6DADD007E84FFC14B3F6B3F107EC8DF013C27E21F066BFF0E7C15E38F8C3F1C3E18FC03F0B7C4AF8C1298FE157C245F1DDE6A5AB78DBE2F78F50EA7A2457DA2FC31F85FE15F1DF8D2DB48BAD7346B1D6F5BD1B48D1EFF52B7B3BE9CB7C13E07FDBC3F681D17F655FDBD3E2CFC18FDA33E11FFC14925FD9D7E14D9F8C3E1D6A965F0AB57F849F1EBC27F136E3FE12D1E28F0C7C5FFD9E7C25A3E833F883E14E8BE1DD174EF895F0DFC4BE19D3FC29E2AF88DA369FE32F0A680DE34BBB7B0F13D8FDE5FF000526FD973C69FB4DFC25F83FA97C33D27C37E2CF89BFB2EFED49F047F6BCF017C38F18EA56FA0F85FE2D6B1F05B55D51F53F85DAA78A2EB4BD6A0F0A5C78CBC25E22F1269DE1EF10DD69971A5D8F8ABFB0975F7B3F0E4FABDFDAFE747ED2BFB0DFED6BFB78B7ED8BF1B759F835E1DFD9B3C47F127F665F80DFB37FC36FD9BBE2E7C4EF0AF8B23F8DF6FF0006FF00691B1FDA57C6DA87ED01E21F817A8F8D3C19E17F09FC48D2EDEE3F67BF0CD8E93E27F883ABC1E0EF1178DF5BF1559E91A76A36BE18BB00B7FB377FC146BE20786FC4FF00B516A93FED69F0E7FE0A71FB36FC0BFD80756FDB3FC47F1DBE127C3CF067C367F873F127C3D26B7AAD97ECFF007DABFC36BAD67C0974FF00153C07A1F88BC5DE16D0F5BB73F12BE1EFFC215AC5A78CDB5A4D56C5EDBD93E197C6BFDBCFE0978FBF612F1B7ED43F18FC0BF18BE1C7EDEB7F3F803C6BF0ABC3DF06340F84CDFB31FC6CF147C09F16FC7CF86FE1FF0085BE2B9FC5526BFE33F054927C3FF16FC25D6ACFE2B5E6B3E32D5759BCF0AF8A6C2FF458975DD0D381D6FF00631F8EBFB66FC6DF1978CBC5DFB32E9BFF0004EEF86B3FFC13DFF69CFD8A75E8E2F1B7C24F1F7C45F8C9AA7ED3165E0ED3B46F3BC3DF03FC43E23F87F0FC22FD9FD7C33AE788FC2775E27D7747F1D6AFE2AF13C56BA7F86F41D024D704BD97837E1E7EDE7F197C5FFB08F877F682FD996DFE1AF81BF605B8D67E2FFC4BF1745F18BE13FC4A9BF6A9F8EBE04F80BF103E02FC27D3FE04D8C7AF5C788FC3FE19D76EFE21F887E2BEADE20F8E6DF0B35BD1B51B7F0B786A74BE9C6B1E22B200FB6FE14FFC144FF667F8D7AE7C0CF07FC39D73C51AF78FBE3BCBF15174EF8743C2D7969E3AF8630FC11BBBDD1FE2A5DFC73F0F5DCB15CFC24B7F0878AED20F044DFF0009535B4FAD78C356D1F45F0CC1AD497CB2A7E397ECE1FF000530FDA7BC71F1C7F662B2D7BF685F863F127E347C7BFDAB3E247C0CFDA13FE096FE1DF85DE18D07E25FEC55F0C3C1D2FC46975FF893AF78C6D758B9F8A7657BF06B4AF0D780B52F1B789BE28DB9F87FF150F8CEE6D3E1B5AE9579A9F84ADE4F77FD963F623FDB33E02FED45ABFEDB1E26F0F7C37D47E20FFC142BFE123D07F6EBF86BE00B5F875A45EFECAF6F669A9DF7ECD3E24F839E3695BC3937C41B3F85BA4CB2783FF695B5835DBED57E2EF8EB5E3F187C2FA7EBDA86830D95EF927C27FD873F6AEB0F849FB02FEC6F79FB1A7C36F8457FFB1B7ED19F043E2678FBF6F1F0F7C52F86B7FE1FF1D685F02FC5ABE2BF17F8FF00E12F8656FF00C59F1F354F8A5FB53DAC1A8683F11747F8C5E11F0EE83A63FC41F1B5CEA9AF6BF0DBE90F2807AB7C53FDB03F6D8B2F829FB4BFFC148FC13F153C1DA67ECDDFB2DFC71F8DDE14D3FF0064CBFF0082F68D6FF19BE017ECCFF19356F831F183C7FAF7C69D7B51D3FC79E12F889ACCDE10F88FE33F026A5E1DB34F87BA0D9681E19D2FC4BE1AF11A5DF8835187ED8F8BDFF054DF803F097E20FC6EF87107C3BFDA4FE2D6B1FB3345E18D63F68AD53E097C15D63E227873E0DF817C63F0DFC31F153C3BF11FC4DABD95FDA2EB3E19D4FC23E23B9BBB5D2BC090F8BFC7D707C25E33B9B7F054DA5686752B9F84FE237ECA7FB71CDF027F6AEFF8268F84BE09699AAFC09FDA63E3E7C64D6FC27FB62DDFC59F03DEF83FE1A7ECDDFB54FC59D6BE337C69F0D78D7E10789F5FFF0085AFAB7C53F075CF8D3E24780FC1FA0783FC3377F0F3C4D6DACF853C4977E2DF0C1B7D7B4C8D9A57C47FDAAFC2BFB6DFFC1647E1A7ECCFFB2968BF1DBFE12BF177ECCDA4F873C4B27C52F877F0B3C3FF000D3C75ADFEC2BF05F40D365F8B4BE29BBB0F14EA3F0A61B086C754B76F859A678EFC59A6CBA5F88B4FB5F064726B965A83007E9778CFFE0A41FB25FC3B93C612F8DBE21BF87342F0D7ECD3A07ED71A0F8BAFF4C98F84FE2B7C0CF10DC47A7DA788FE106B16EF3A78EF58875BD43C31E1D9BC1D63143E2CB9D73C73E06B2D2B47D47FE129D2E59BEC7F06F8917C65E11F0B78B9742F1278613C53E1ED1BC449E1BF196932681E2ED0135AD3ADF514D1BC53A0CB24D3689E21D356E459EB3A44F23DC699A8C3716539F3A0703F9C5F891FF0498FDA7BC7FE06FD8E3E05687ADF837C1FE17FF8249FECDFF04358FD92FE296BB67E0DF171FDA87F6D5F05687E101A95AFC41F0BEA16FABEADE0EFD97B4BB1F87769E13D5F40BA93C31E2DD67C4DE3883C6361FDA117C2CF09DEDD7F447F0BFC41E33F15FC39F047893E237806E7E1678FF5BF0BE8DA8F8D3E1C5DEBFA078AE5F0578A2E6C617D77C389E27F0B5F6A5A07886D74CD48DCDB58EB5A6DD793A9D925BDE3DB58CF34B636E01DDD145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451400514514005145140051451401F8F3FF0469FF927DFF0502FFB4C37FC14F7FF005A77C555FB0D5F8F3FF0469FF927DFF0502FFB4C37FC14F7FF005A77C555FAE1A66BFA16B52EA1068FAD693AB4FA4DC7D8F54874CD46CEFE5D36EC8622D7508ED6695ECEE08473E45CAC72E15BE5F94E0035ABE4BF8C3FB26F84FE22DE5DF88BC3779FF08878A6EB74B746383ED1A16AD72CFB9AE2F6C94A4D697528CA49776122C6C7F7D358DC4E5E47FAD28A00FC7AF117EC8BF1AF42964167A1587896D91884BBD0B57B260EBFBD21BEC9A9C9A6DF0609102CA2D9C6E9638D1A472C17F1FF00F6AEFF008211693FB487C4ED27F68AF047873E367ECB7FB55787351B6D6F44FDA13F67FBC7F08F8B27D7AC123834ED63C49676ED0C5AAEA9616CAF6F0EBFA45E786BC5D3C060B2BEF12DDE99676DA7C7FD82D7CD371FB63FECBB693CF6B75F1C3C076D756D3496F736D71AA3433DBCF0BB473413C3242B24534522B4724722ABC6EACACA18115DD82CB332CC9D48E5D9763B3074545D5582C262314E929B6A0EA2A14EA3829B8C945CADCCE2D2BD99CF88C661309C9F5AC561F0DED39B93EB15E951E7E5B73727B49479B979A3CD6BDB995ED747E307C00F82FF00F070D7C36B1B0F0C6A3FB6EFECFBF1BFC39611A5A5AF8ABF698FD8EECED7C656F601B6DAA6A33FC28FDA27E19EA1E22BEB7B471E76A7AA35E6A379716C8752BB92E27B895BF53BE19FC15FDB73506B6BFF00DA2BF6CEF0FCF2C52979FC2DFB307ECFBE11F853E1BD461276B59EA9ADFC64D63F688F199B39E166477F0D6AFE11D6ADA60B7165AEC0C1513EA1D27E2E7C35D73C5EBE00D2FC63A35C78D5F40B4F142785DA692DB5A93C3F7D69657D6BAA4763771413496F25AEA16931D8AD246AF2096346B7B858897E2EFC3483C55E29F034BE32D1478BFC13E1C9BC5FE2AF0E09DDF56D13C336F6DA75E4DACDE5A246D20B24B6D5B4D977C4246617B02AA976DA13CB3328CDD3797E35548E1D62E50784AEA6B0929C69C714E2E9F32C3CAA4A34D566BD9B9CA3152E6690FEB386E573FAC50E5556545CBDAD3E555A37E6A4DF359558D9F3536F9959DD2B33BFB6B78ED2DADED6269DE2B6822B78DAE6E6E6F6E5A386358D1AE2F2F259EF2EE7655065B9BA9E6B99E42D2CF2C92BBBB7E54FFC15DBFE4947EC6DFF00695DFF00825BFF00EB697C25AFD1FD07E29FC3BF14781EEFE257877C63A16B5E03B0B3D6350BDF1469D78973A55A5A787D6E1F599AE268C178869D1DACF25C23A09162412046478D9BF2E7FE0A89E34F0AFC42F809FB12F8C3C13AF69DE26F0C6AFF00F055DFF8261FF666B7A54DF68B0BDFECFF00DB87E17E977BE44D85DFF66D42CAEED25E06D9ADE45FE1CD455C1636846B4ABE131546387C42C257955C3D5A71A18B719CD616B39C22A9E21C69D492A3371A9CB4E72E5B424D542B51A9ECF92B529FB6A6EB52E4A9097B5A2B92F569D9BE7A6BDA53BD48DE2BDA435F7A37FD84A28A2B94D428A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A002A29E086E619ADAE618AE2DEE229209E09E349619E1950C72C3345206492291199248DD591D18AB0209152D1401E0DF0A7F657FD987E03EBDAE78ABE077ECE3F01BE0CF89FC4D66DA77893C47F0A7E107C3EF877AF78834F6BC4D45AC75CD63C21E1ED1F50D5ACDB508E3BF6B6BFB8B884DE4697453CF5571EF3451400514514005145140051451400572BA2F813C0FE1BF11F8CBC61E1DF06F85341F16FC45BCD1751F883E29D17C3DA4697E23F1DEA1E1CD12D3C33E1EBEF196B7636706A7E27BCD07C376163E1FD16E75BBABE9F4BD12CAD349B1782C2DA1B74EAA8A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A0028A28A00FE47FC45E1BFDA1FC5BFF000488FF0082CCE85FB3243F11AFBE21DE7FC15D7FE0A00DE20D17E0EC97517C5BF127C1A83F6EAB79FE3CF86BE1B3588FB74DE2BD77E0E47E36D3ECEC34E2354D5ADA7BCD2349126A97F67149CDF82B4AFF00827978BFF6D1FF0082685CFF00C10F7E155CFC3EF899E03F8B924DFB6678A3E117C26F8ABF08BC15E1BFD8887C39F130F895E08FDAE359F15E81E1AD33C4BF123C43E2DFF842EDFE1EDA7C40B8F12FC4483C6BA4DFDEC77D65A89D3AF2EBF417E067C5DF889FF04C1F8B7FB64FC12F8D5FB257ED7FF163E187C6FF00DB0FE3EFED8FF01BF680FD93FF00679F887FB4FF008435FF000D7ED2FE2A5F889E27F871F10F41F845A4F88BC67F0CFE207C36F1B6A3AFE830A788BC3B6FA1F8ABC391699ABE93A9C855E5D43EA2FF0087BB7C28FF00A336FF0082AEFF00E2ADFF006D2FFE74B401F88BFF0004DDF84BFF000570F8C7F01BF66DFDAF3C47FB4F78CBC2FF000EECC7C6DF187C62D57E21FED9BFB41FC5AF1D7C63F87BA18F8C7E0DB6F017FC32EF8C3E05E87F0E7E12F88A3D6EC3C337BA4F8FB42F8EDAB6B1E1FB6F0F43AF69D6B79A96B325869BE15FF04F6FDA9FC7FE30FD933C53E29F8D7FB6AFC6FB9FDAE0FEC37FB4178ABE1C7826CBF6CFFDAF3E20F8BBC4BF19747FD9DFE2BEBFAA4DF133E0DF8EBE067C3AF861F0CBC4BE10D2B4D6F1A784B4FF00067C4AF88CBA7F8CB41B4974FD760BED3B4B875AFE8BFF00E1EEDF0A3FE8CDBFE0ABBFF8AB7FDB4BFF009D2D1FF0F76F851FF466DFF055DFFC55BFEDA5FF00CE96803F1EBF65BFDA27FE0A71E05F8F7FF0465FD97BF6B0D5BE2478F3C15F14359D7BE2EE93FB5FF8575CD774CF0EFED07F06BC49FB177C4BF1A5BFC11FDAAFC3FF00DA6F3D8FC6DF83DF17F54F87D6F6DA96B06E742F8A1A75BE83AEDA0B6F11C5E24B6D5BF563F663F8A7E1AF87BE17B1B1F18FC61BBF0D59DA7C40F1F6A97FF0ED3F67BF127885EE34BBFF001CEBB7D0097C79A77877557BCFEDBB39A2D46DEFECA5FF0042B2BBB5B18E226D0C92F5BFF0F76F851FF466DFF055DFFC55BFEDA5FF00CE968FF87BB7C28FFA336FF82AEFFE2ADFF6D2FF00E74B5EC65B9A470387C661A74275618BAB84AAE54EAD0A7384B09F59E556C460F1B4A719FD61B92749493846D2B3927F3F9BE431CD71D9663655E10FECD86322A854A35AA52AF2C554C0D58BAAE863309270A52C0AE6A3273A55D54E5AD174E32854E5FE27E81AEEAFFB44FC64FDA27E107DBAFF00C5DF07BC0DF017E23782F4CB596FF4BB3F883F0FEE3C3DAD5D7C47F063D918127B91AA784A6D22E96D12C65D56D2F06936305B4377AAC31CB47C15F0A7C69F0F7E3E7C56D77C7A2E2FFC75F11BF611F89BE3FF0089BAD5B25EC9E1F8FC7FE28F89734927877489A60F6D676DE1DF0DE99A1E8567A6C331516DA41BC894C53973DB7FC3DDBE147FD19B7FC1577FF156FF00B697FF003A5A3FE1EEDF0A3FE8CDBFE0ABBFF8AB7FDB4BFF009D2D7D0478DF171CBBFB36382A1ECE59753CB275E53BE26585A3470F0A347DAAA49AA10C452AD8D951D633C4E239E4ED4A9A5E14381E3196226F339BFAC66B8BCDDD18E129C70947178BA95F9EA50A2AB3A91AAB0D56183552788A96A149C69C69AAD5632F2EF83FF03FE2737837E17FC0AD05EE20FD9F7F680F01F817E2BFC58D52FF0051D5E3D47C1D2F842CF4583E31FC3ED2661218F44B8F1F6B973E19F3159ADAE6C85D7896C45BBDA585D7D8BE7DF8D1A7DFE97FF04E5FF826C58EA76577A75F41FF00055DFF00827BF9F677D6D35A5D43E6FF00C148FC37347E6DBDC247347E6432472A6F41BE37475CAB293F69FF00C3DDBE147FD19B7FC1577FF156FF00B697FF003A5AF987E3AFC75F89BFF052CF89BFB22FC05F80BFB22FED87F0BBE1F7C2EFDB0FF66FFDACBE3FFC7FFDACBF66FF0088DFB31F80BC2BE02FD98FE2369BF17B4CF02F8174CF8BDA6F85BC5FF127E23FC49F17F85BC3FE16B4B4F0B787EFB49F0BE937D7FAF6BD7F15B44AA98E7BC658ACF70388C056C251A10C46694336A9529CEF52A62E8D3CCF0EEAD57ECE1ED6B56C26370B87AB5A56728E5B464A29D4928F7643C271C8ABE0EB2CC2AE3160F2FC6E5D4E35A846128D2C656CB312E14E70A8D53C3D1C460313568D0709B87D7EA538D554A85383FDFBA28A2BE34FAF0A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2800A28A2803FFFD9',5,0,'image/jpeg','logo.jpg',0,NULL),
	(9,3,1,'[agnDYN name=\"Text\"/]',NULL,0,0,'text/plain','agnText',0,NULL),
	(10,3,1,'[agnDYN name=\"HTML-Version\"/]',NULL,0,0,'text/html','agnHtml',0,NULL),
	(11,4,1,'[agnDYN name=\"Text\"/]',NULL,0,0,'text/plain','agnText',0,NULL),
	(12,4,1,'[agnDYN name=\"HTML-Version\"/]',NULL,0,0,'text/html','agnHtml',0,NULL);



DROP TABLE IF EXISTS config_tbl;
CREATE TABLE config_tbl (
  class varchar(32)  NOT NULL,
  classid integer NOT NULL default '0',
  name varchar(32)  NOT NULL,
  value text 
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO config_tbl (class, classid, name, value) VALUES ('linkchecker',0,'linktimeout','20000'),
	('linkchecker',0,'threadcount','20');



DROP TABLE IF EXISTS cust_ban_tbl;
CREATE TABLE cust_ban_tbl (
  company_id int(10) unsigned NOT NULL default '0',
  email varchar(200)  NOT NULL default '',
  creation_date timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (company_id,email)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS customer_1_binding_tbl;
CREATE TABLE customer_1_binding_tbl (
  customer_id int(10) unsigned NOT NULL default '0',
  mailinglist_id int(10) unsigned NOT NULL default '0',
  user_type char(1)  default NULL,
  user_status int(10) unsigned default NULL,
  user_remark varchar(150)  default NULL,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  exit_mailing_id int(10) unsigned default NULL,
  creation_date timestamp NULL default NULL,
  mediatype int(10) unsigned NOT NULL default '0',
  UNIQUE KEY cust_1_bind_un (customer_id,mailinglist_id),
  KEY customer_id_327 (customer_id)  
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO customer_1_binding_tbl (customer_id, mailinglist_id, user_type, user_status, user_remark, change_date, exit_mailing_id, creation_date, mediatype) VALUES (1,1,'A',1,'',CURRENT_TIMESTAMP,0,CURRENT_TIMESTAMP,0),
	(2,1,'T',1,'',CURRENT_TIMESTAMP,0,CURRENT_TIMESTAMP,0);



DROP TABLE IF EXISTS customer_1_tbl;
CREATE TABLE customer_1_tbl (
  customer_id integer NOT NULL auto_increment,
  email varchar(100)  default NULL,
  gender integer NOT NULL default '2',
  mailtype integer default '0',
  firstname varchar(100)  default NULL,
  lastname varchar(100)  default NULL,
  creation_date timestamp NOT NULL default '0000-00-00 00:00:00',
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  title varchar(100)  default NULL,
  datasource_id integer NOT NULL default '0',
  PRIMARY KEY  (customer_id),
  KEY email_349 (email)  
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ;


INSERT INTO customer_1_tbl (customer_id, email, gender, mailtype, firstname, lastname, creation_date, change_date, title, datasource_id) VALUES (1,'first.last@domain.org',0,1,'First','Last',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,NULL,0),
	(2,'no.name@yourdomain.com',0,1,'No','Name',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,NULL,0);



DROP TABLE IF EXISTS customer_field_tbl;
CREATE TABLE customer_field_tbl (
  company_id integer NOT NULL default '0',
  col_name varchar(255)  NOT NULL default '',
  admin_id integer NOT NULL default '0',
  shortname varchar(255)  NOT NULL default '',
  description varchar(255)  NOT NULL default '',
  default_value varchar(255)  NOT NULL default '',
  mode_edit integer NOT NULL default '0',
  mode_insert integer NOT NULL default '0',
  PRIMARY KEY  (company_id,col_name)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS datasource_description_tbl;
CREATE TABLE datasource_description_tbl (
  datasource_id integer NOT NULL auto_increment,
  company_id integer NOT NULL default '0',
  sourcegroup_id integer NOT NULL default '0',
  description text ,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  creation_date timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (datasource_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS date_tbl;
CREATE TABLE date_tbl (
  type integer NOT NULL default '0',
  format varchar(100)  default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO date_tbl (type, format) VALUES (0,'d.M.yyyy'),
	(1,'MM/dd/yyyy'),
	(2,'EEEE d MMMM yyyy'),
	(3,'yyyy-MM-dd'),
	(6,'dd/MM/yyyy'),
	(7,'yyyy/MM/dd'),
	(8,'yyyy-MM-dd');



DROP TABLE IF EXISTS doc_mapping_tbl;
CREATE TABLE doc_mapping_tbl (
  filename varchar(200)  default NULL,
  pagekey varchar(50)  default NULL,
  UNIQUE KEY doc_mapping$pagekey$unique (pagekey)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


insert into doc_mapping_tbl (filename, pagekey) values ('what_are_templates_.htm','templateList');
insert into doc_mapping_tbl (filename, pagekey) values ('list_existing_mailings.htm','mailingList');
insert into doc_mapping_tbl (filename, pagekey) values ('sending_normal_file_attachment.htm','attachments');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_mailing.htm', 'createNewMailing');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_mailing_data.htm','newMailingNormal');
insert into doc_mapping_tbl (filename, pagekey) values ('create_new_mailing_using_the_w.htm','newMailingWizard');
insert into doc_mapping_tbl (filename, pagekey) values ('inserting_content.htm','contentList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_text_and_html_modules.htm','contentView');
insert into doc_mapping_tbl (filename, pagekey) values ('using_graphic_elements.htm','pictureComponents');
insert into doc_mapping_tbl (filename, pagekey) values ('create_trackable_and_non_track.htm','trackableLinks');
insert into doc_mapping_tbl (filename, pagekey) values ('preview___for_in_depth_checkin.htm','preview');
insert into doc_mapping_tbl (filename, pagekey) values ('testing_and_sending_a_mailing.htm','mailingTestAndSend');
insert into doc_mapping_tbl (filename, pagekey) values ('send_mailing.htm','sendMailing');
insert into doc_mapping_tbl (filename, pagekey) values ('mailing_statistics_openemm.htm','mailingStatistic');
insert into doc_mapping_tbl (filename, pagekey) values ('heatmap_openemm.htm','heatmap');
insert into doc_mapping_tbl (filename, pagekey) values ('show_available_cm_templates.htm','cmTemplateList');
insert into doc_mapping_tbl (filename, pagekey) values ('editing_cm_templates.htm','cmTemplateView');
insert into doc_mapping_tbl (filename, pagekey) values ('using_cm_templates_in_mailings.htm','cmTemplateForMailing');
insert into doc_mapping_tbl (filename, pagekey) values ('show_available_module_types.htm','cmModuleTypeList');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_data.htm','cmModuleTypeView');
insert into doc_mapping_tbl (filename, pagekey) values ('show_available_content_modules.htm','cmContentModuleList');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_data2.htm','cmContentModuleView');
insert into doc_mapping_tbl (filename, pagekey) values ('saving_a_content_moduke_and_as.htm','cmContentModuleAssign');
insert into doc_mapping_tbl (filename, pagekey) values ('cm_categories.htm','cmCategoryList');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_template_data.htm','newTemplate');
insert into doc_mapping_tbl (filename, pagekey) values ('display_and_amend_details.htm','newTemplateNormal');
insert into doc_mapping_tbl (filename, pagekey) values ('create_a_new_archive.htm','newArchive');
insert into doc_mapping_tbl (filename, pagekey) values ('display_and_amend_details.htm','archiveView');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_recipients.htm','recipientList');
insert into doc_mapping_tbl (filename, pagekey) values ('show_recipient_profile.htm','recipientView');
insert into doc_mapping_tbl (filename, pagekey) values ('create_new_recipients.htm','newRecipient');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_import_profile.htm','newImportProfile');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_fields.htm','manageFields');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_a_profile__deleting_a.htm','manageProfile');
insert into doc_mapping_tbl (filename, pagekey) values ('the_import_assistant.htm','importStep1');
insert into doc_mapping_tbl (filename, pagekey) values ('assigning_the_csv_columns_to_t.htm','importStep2');
insert into doc_mapping_tbl (filename, pagekey) values ('errorhandling.htm','importStep3');
insert into doc_mapping_tbl (filename, pagekey) values ('importing_the_csv_file.htm','importStep4');
insert into doc_mapping_tbl (filename, pagekey) values ('export_function_for_recipient_.htm','export');
insert into doc_mapping_tbl (filename, pagekey) values ('blacklist___do_not_mail.htm','blacklist');
insert into doc_mapping_tbl (filename, pagekey) values ('types_of_address.htm','salutationForms');
insert into doc_mapping_tbl (filename, pagekey) values ('comparing_mailings_openemm.htm','compareMailings');
insert into doc_mapping_tbl (filename, pagekey) values ('domain_statistics.htm','domainStatistic');
insert into doc_mapping_tbl (filename, pagekey) values ('ip_statistics.htm','ipStatistics');
insert into doc_mapping_tbl (filename, pagekey) values ('recipient_statistics_openemm.htm','recipientStatistic');
insert into doc_mapping_tbl (filename, pagekey) values ('what_is_a_traget_group_.htm','targetGroupList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_and_managing_target_g.htm','targetGroupView');
insert into doc_mapping_tbl (filename, pagekey) values ('modifying_a_mailing_list.htm','mailinglists');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_mailing_list.htm','newMailinglist');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_forms.htm','formList');
insert into doc_mapping_tbl (filename, pagekey) values ('this_is_how_forms_work.htm','formView');
insert into doc_mapping_tbl (filename, pagekey) values ('building_trackable_links_into_.htm','trackableLinkView');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_actions.htm','actionList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_action.htm','newAction');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_profile_fields.htm','profileFieldList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_new_fields.htm','newProfileField');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_user_and_changi.htm','newUser');
insert into doc_mapping_tbl (filename, pagekey) values ('assigning_user_rights2.htm','userRights');
insert into doc_mapping_tbl (filename, pagekey) values ('bounce_filter.htm','bounceFilter');
insert into doc_mapping_tbl (filename, pagekey) values ('user_log.htm','userlog');
insert into doc_mapping_tbl (filename, pagekey) values ('automatic_update_of_openemm.htm', 'update');
insert into doc_mapping_tbl (filename, pagekey) values ('feedback_analysis_openemm.htm', 'feedbackAnalysis');



DROP TABLE IF EXISTS dyn_content_tbl;
CREATE TABLE dyn_content_tbl (
  dyn_content_id int(10) unsigned NOT NULL auto_increment,
  dyn_name_id int(10) unsigned NOT NULL default '0',
  company_id int(10) unsigned NOT NULL default '0',
  dyn_content longtext ,
  dyn_order int(10) unsigned default NULL,
  target_id int(10) unsigned default NULL,
  PRIMARY KEY  (dyn_content_id)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 ;


INSERT INTO dyn_content_tbl (dyn_content_id, dyn_name_id, company_id, dyn_content, dyn_order, target_id) VALUES (1,23,1,'[agnIMAGE name=\"logo.jpg\"]',1,0),
	(2,1,1,'Firmenname für Textversion',1,0),
	(3,2,1,'[agnDATE]',1,0),
	(4,4,1,'Ihre<br>\r\nSuper-Firma',1,0),
	(5,20,1,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_profil&agnUID=##AGNUID##',1,0),
	(6,21,1,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_unsubscribe&agnUID=##AGNUID##',1,0),
	(7,22,1,'Ihre Firma, www.meine-firma.de<br>\r\nTelefon: xxx/12343567 ...<br><br>\r\nVorstand: xxx<br>\r\nRegistergericht ...',1,0),
	(8,55,1,'[agnIMAGE name=\"logo.jpg\"]',1,0),
	(9,33,1,'name of company for textversion',1,0),
	(10,34,1,'[agnDATE]',1,0),
	(11,36,1,'your<br>\r\nsuper company',1,0),
	(12,52,1,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_profil&agnUID=##AGNUID##',1,0),
	(13,53,1,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_unsubscribe&agnUID=##AGNUID##',1,0),
	(14,54,1,'Your company, www.my-company.de<br>\r\nFon: xxxx/12343567 ...<br>',1,0),
	(15,65,1,'**********************************************************************\r\n                       Newsletter Anmeldung\r\n**********************************************************************\r\n\r\n[agnTITLE type=2],\r\n\r\nvielen Dank, dass Sie sich für unseren Newsletter interessieren.\r\nUm Ihr Abonnement zu bestätigen, klicken Sie bitte auf folgenden\r\nAktivierungslink:\r\n\r\nhttp://www.meine-firma.de/form.do?agnCI=1&agnFN=de_doi_welcome&agnUID=##AGNUID##\r\n(ACHTUNG: Bitte www.meine-firma.de auf Ihren rdir-Link anpassen\r\nund Link-Messung aktivieren!)\r\n\r\nHaben Sie unseren Newsletter nicht abonniert oder wurden fälschlicher\r\nWeise bei uns angemeldet, müssen Sie nichts weiter unternehmen.\r\n\r\nMit freundlichen Grüßen\r\n\r\nIhr online-Team\r\n\r\n**********************************************************************\r\nImpressum:\r\nFirmenname\r\nStrasse ...',1,0),
	(16,67,1,'**********************************************************************\r\n                       newsletter registration\r\n**********************************************************************\r\n\r\n[agnTITLE type=1],\r\n\r\nthank you for your interest in our newsletter. To activate the\r\nabonnement please click the following link:\r\n\r\nhttp://www.my-company.de/form.do?agnCI=1&agnFN=en_doi_welcome&agnUID=##AGNUID##\r\n(WARNING: Please change www.my-company.de to your rdir-link and be\r\nsure that linktracking is activated!)\r\n\r\nGreetings\r\n\r\nyour online-team\r\n\r\n**********************************************************************\r\nImprint:\r\nname of company ...',1,0);



DROP TABLE IF EXISTS dyn_name_tbl;
CREATE TABLE dyn_name_tbl (
  dyn_name_id int(10) unsigned NOT NULL auto_increment,
  mailing_id int(10) unsigned NOT NULL default '0',
  company_id int(10) unsigned NOT NULL default '0',
  dyn_name varchar(100)  NOT NULL default '',
  deleted int(1) NOT NULL default '0',
  PRIMARY KEY  (dyn_name_id),
  KEY mailing_id_519 (mailing_id)  
) ENGINE=MyISAM AUTO_INCREMENT=69 DEFAULT CHARSET=utf8 ;


INSERT INTO dyn_name_tbl (dyn_name_id, mailing_id, company_id, dyn_name, deleted) VALUES (1,1,1,'0.1.1 Header-Text',0),
	(2,1,1,'0.2 date',0),
	(3,1,1,'0.3 Intro-text',0),
	(4,1,1,'0.4 Greeting',0),
	(5,1,1,'1.0 Headline ****',0),
	(6,1,1,'1.1 Sub-headline',0),
	(7,1,1,'1.2 Content',0),
	(8,1,1,'1.3 Link-URL',0),
	(9,1,1,'1.4 Link-Text',0),
	(10,1,1,'2.0 Headline ****',0),
	(11,1,1,'2.1 Sub-headline',0),
	(12,1,1,'2.2 Content',0),
	(13,1,1,'2.3 Link-URL',0),
	(14,1,1,'2.4 Link-Text',0),
	(15,1,1,'3.0 Headline ****',0),
	(16,1,1,'3.1 Sub-headline',0),
	(17,1,1,'3.2 Content',0),
	(18,1,1,'3.3 Link-URL',0),
	(19,1,1,'3.4 Link-Text',0),
	(20,1,1,'9.0 change-profil-URL',0),
	(21,1,1,'9.1 unsubscribe-URL',0),
	(22,1,1,'9.2 imprint',0),
	(23,1,1,'0.1 Header-image',0),
	(24,1,1,'1.5 Image-URL',0),
	(25,1,1,'1.7 Image-URL-1',0),
	(26,1,1,'1.6 Link-URL',0),
	(27,1,1,'2.5 Image-URL',0),
	(28,1,1,'2.7 Image-URL-1',0),
	(29,1,1,'2.6 Link-URL',0),
	(30,1,1,'3.5 Image-URL',0),
	(31,1,1,'3.7 Image-URL-1',0),
	(32,1,1,'3.6 Link-URL',0),
	(33,2,1,'0.1.1 Header-Text',0),
	(34,2,1,'0.2 date',0),
	(35,2,1,'0.3 Intro-text',0),
	(36,2,1,'0.4 Greeting',0),
	(37,2,1,'1.0 Headline ****',0),
	(38,2,1,'1.1 Sub-headline',0),
	(39,2,1,'1.2 Content',0),
	(40,2,1,'1.3 Link-URL',0),
	(41,2,1,'1.4 Link-Text',0),
	(42,2,1,'2.0 Headline ****',0),
	(43,2,1,'2.1 Sub-headline',0),
	(44,2,1,'2.2 Content',0),
	(45,2,1,'2.3 Link-URL',0),
	(46,2,1,'2.4 Link-Text',0),
	(47,2,1,'3.0 Headline ****',0),
	(48,2,1,'3.1 Sub-headline',0),
	(49,2,1,'3.2 Content',0),
	(50,2,1,'3.3 Link-URL',0),
	(51,2,1,'3.4 Link-Text',0),
	(52,2,1,'9.0 change-profil-URL',0),
	(53,2,1,'9.1 unsubscribe-URL',0),
	(54,2,1,'9.2 imprint',0),
	(55,2,1,'0.1 Header-image',0),
	(56,2,1,'1.5 Image-URL',0),
	(57,2,1,'1.7 Image-URL-1',0),
	(58,2,1,'1.6 Link-URL',0),
	(59,2,1,'2.5 Image-URL',0),
	(60,2,1,'2.7 Image-URL-1',0),
	(61,2,1,'2.6 Link-URL',0),
	(62,2,1,'3.5 Image-URL',0),
	(63,2,1,'3.7 Image-URL-1',0),
	(64,2,1,'3.6 Link-URL',0),
	(65,3,1,'Text',0),
	(66,3,1,'HTML-Version',0),
	(67,4,1,'Text',0),
	(68,4,1,'HTML-Version',0);



DROP TABLE IF EXISTS dyn_target_tbl;
CREATE TABLE dyn_target_tbl (
  target_id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL default '0',
  target_shortname varchar(100)  NOT NULL default '',
  target_description text ,
  target_sql text ,
  target_representation blob,
  deleted int(1) NOT NULL default '0',
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  creation_date timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (target_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS export_predef_tbl;
CREATE TABLE export_predef_tbl (
  id integer NOT NULL auto_increment,
  company_id integer NOT NULL default '0',
  charset varchar(200)  NOT NULL default 'ISO-8859-1',
  column_names text  NOT NULL,
  deleted integer NOT NULL default '0',
  shortname text  NOT NULL,
  description text  NOT NULL,
  mailinglists text  NOT NULL,
  mailinglist_id integer NOT NULL default '0',
  delimiter_char char(1)  NOT NULL default '0',
  separator_char char(1)  NOT NULL default '0',
  target_id integer NOT NULL default '0',
  user_status integer NOT NULL default '0',
  user_type char(1)  NOT NULL default '0',
  timestamp_start timestamp NULL default NULL,
  timestamp_end timestamp NULL default NULL,
  creation_date_start timestamp NULL default NULL,
  creation_date_end timestamp NULL default NULL,
  mailinglist_bind_start timestamp NULL default NULL,
  mailinglist_bind_end timestamp NULL default NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS import_column_mapping_tbl;
CREATE TABLE import_column_mapping_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  profile_id int(10) unsigned NOT NULL,
  file_column varchar(255)  NOT NULL,
  db_column varchar(255)  NOT NULL,
  mandatory tinyint(1) NOT NULL,
  default_value varchar(255)  default '',
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS import_gender_mapping_tbl;
CREATE TABLE import_gender_mapping_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  profile_id int(10) unsigned NOT NULL,
  int_gender int(10) unsigned NOT NULL,
  string_gender varchar(100)  NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS import_log_tbl;
CREATE TABLE import_log_tbl (
  log_id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL,
  admin_id int(10) unsigned NOT NULL,
  creation_date timestamp NOT NULL default CURRENT_TIMESTAMP,
  imported_lines int(10) unsigned NOT NULL,
  datasource_id int(10) unsigned NOT NULL,
  statistics text  NOT NULL,
  profile text  NOT NULL,
  PRIMARY KEY  (log_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS import_profile_tbl;
CREATE TABLE import_profile_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL,
  admin_id int(10) unsigned NOT NULL,
  shortname varchar(255)  NOT NULL,
  column_separator int(10) unsigned NOT NULL,
  text_delimiter int(10) unsigned NOT NULL,
  file_charset int(10) unsigned NOT NULL,
  date_format int(10) unsigned NOT NULL,
  import_mode int(10) unsigned NOT NULL,
  null_values_action int(10) unsigned NOT NULL,
  key_column varchar(255)  NOT NULL,
  ext_email_check tinyint(1) NOT NULL,
  report_email varchar(255)  NOT NULL,
  check_for_duplicates int(10) unsigned NOT NULL,
  mail_type int(10) unsigned NOT NULL,
  update_all_duplicates decimal(1,0) default '0',
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS login_track_tbl;
CREATE TABLE login_track_tbl (
  login_track_id integer NOT NULL auto_increment,
  ip_address varchar(40)  default NULL,
  creation_date timestamp NOT NULL default CURRENT_TIMESTAMP,
  login_status integer default NULL,
  username varchar(50)  default NULL,
  PRIMARY KEY  (login_track_id),
  KEY logtrck$ip_cdate_stat$idx_719 (ip_address,creation_date,login_status)  
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS maildrop_status_tbl;
CREATE TABLE maildrop_status_tbl (
  status_id integer NOT NULL auto_increment,
  company_id integer NOT NULL default '0',
  status_field varchar(10)  NOT NULL default '',
  mailing_id integer NOT NULL default '0',
  senddate timestamp NOT NULL default CURRENT_TIMESTAMP,
  step integer default NULL,
  blocksize integer default NULL,
  gendate timestamp NOT NULL default '0000-00-00 00:00:00',
  genstatus int(1) default NULL,
  genchange timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (status_id)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ;


INSERT INTO maildrop_status_tbl (status_id, company_id, status_field, mailing_id, senddate, step, blocksize, gendate, genstatus, genchange) VALUES (1,1,'E',3,'2008-02-12 10:54:32',0,0,'2008-02-12 10:54:32',1,'2008-02-12 10:54:32'),
	(3,1,'E',4,'2008-02-26 10:53:32',0,0,'2008-02-26 10:53:32',1,'2008-02-26 10:53:33');



DROP TABLE IF EXISTS mailing_account_tbl;
CREATE TABLE mailing_account_tbl (
  mailing_id integer NOT NULL default '0',
  company_id integer NOT NULL default '0',
  mailtype integer NOT NULL default '0',
  no_of_mailings integer NOT NULL default '0',
  no_of_bytes integer NOT NULL default '0',
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  maildrop_id integer NOT NULL default '0',
  mailing_account_id integer NOT NULL default '0',
  status_field varchar(255)  NOT NULL default '',
  blocknr integer default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS mailing_backend_log_tbl;
CREATE TABLE mailing_backend_log_tbl (
  mailing_id int(10) default NULL,
  current_mails int(10) default NULL,
  total_mails int(10) default NULL,
  change_date timestamp NULL default NULL,
  creation_date timestamp NULL default NULL,
  status_id int(10) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS mailing_mt_tbl;
CREATE TABLE mailing_mt_tbl (
  mailing_id int(10) unsigned NOT NULL default '0',
  param text  NOT NULL,
  mediatype int(10) unsigned NOT NULL default '0',
  KEY mailing_id_784 (mailing_id)  
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO mailing_mt_tbl (mailing_id, param, mediatype) VALUES (1,'from=\"Absender anpassen <noreply@openemm.org>\", subject=\"Bitte Betreff einfügen!\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"2\", reply=\"Absender anpassen <noreply@openemm.org>\", onepixlog=\"bottom\", ',0),
	(2,'from=\"change sender name <noreply@openemm.org>\", subject=\"insert subject please!\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"2\", reply=\"change sender name <noreply@openemm.org>\", onepixlog=\"bottom\", ',0),
	(3,'from=\"change sender name <noreply@openemm.org>\", subject=\"Bitte aktivieren: Ihre Anmeldung zum Newsletter\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"0\", reply=\"change sender name <noreply@openemm.org>\", onepixlog=\"none\", ',0),
	(4,'from=\"change sender name <noreply@openemm.org>\", subject=\"please activate: your newsletter subscription\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"0\", reply=\"change sender name <noreply@openemm.org>\", onepixlog=\"none\", ',0);



DROP TABLE IF EXISTS mailing_tbl;
CREATE TABLE mailing_tbl (
  mailing_id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL default '0',
  campaign_id integer unsigned NOT NULL default '0',
  shortname varchar(200)  NOT NULL default '',
  description text  NOT NULL,
  mailing_type int(10) unsigned NOT NULL default '0',
  creation_date timestamp NOT NULL default '0000-00-00 00:00:00',
  mailtemplate_id int(10) unsigned default '0',
  is_template int(10) unsigned NOT NULL default '0',
  deleted int(10) unsigned NOT NULL default '0',
  target_expression text ,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  mailinglist_id int(10) unsigned NOT NULL default '0',
  needs_target int(10) unsigned NOT NULL default '0',
  archived integer unsigned NOT NULL default '0',
  cms_has_classic_content int(1) NOT NULL default '0',
  dynamic_template int(1) NOT NULL default '0',
  openaction_id integer unsigned default '0',
  clickaction_id integer unsigned default '0',
  PRIMARY KEY  (mailing_id)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ;


INSERT INTO mailing_tbl (mailing_id, company_id, campaign_id, shortname, description, mailing_type, creation_date, mailtemplate_id, is_template, deleted, target_expression, change_date, mailinglist_id, needs_target, archived, cms_has_classic_content, dynamic_template, openaction_id, clickaction_id) VALUES (1,1,0,'de_template','created by eMM-Xpress',0,'2008-02-12 10:30:14',0,1,0,'','2011-01-28 08:20:02',1,0,0,1,0,0,0),
	(2,1,0,'en_template','created by eMM-Xpress',0,'2008-02-12 10:47:21',0,1,0,'','2011-01-28 08:20:02',1,0,0,1,0,0,0),
	(3,1,0,'de_doi_mail','double-opt-in mail, subscribe link',1,'2008-02-12 10:54:06',0,0,0,'','2011-01-28 08:20:02',1,0,0,0,0,0,0),
	(4,1,0,'en_doi_mail','double-opt-in mail, subscribe link',1,'2008-02-12 10:57:13',0,0,0,'','2011-01-28 08:20:02',1,0,0,0,0,0,0);



DROP TABLE IF EXISTS mailinglist_tbl;
CREATE TABLE mailinglist_tbl (
  mailinglist_id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned default NULL,
  description text ,
  shortname varchar(100)  NOT NULL default '',
  KEY mailinglist_id_833 (mailinglist_id)  
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ;


INSERT INTO mailinglist_tbl (mailinglist_id, company_id, description, shortname) VALUES (1,1,'','mailinglist');



DROP TABLE IF EXISTS mailloop_tbl;
CREATE TABLE mailloop_tbl (
  rid int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL default '0',
  description text  NOT NULL,
  shortname varchar(200)  NOT NULL default '',
  forward varchar(200)  NOT NULL default '',
  forward_enable int(10) unsigned NOT NULL default '0',
  ar_enable int(10) unsigned NOT NULL default '0',
  ar_sender varchar(200)  NOT NULL default '',
  ar_subject text  NOT NULL,
  ar_text longtext  NOT NULL,
  ar_html longtext  NOT NULL,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  subscribe_enable int(1) unsigned NOT NULL default '0',
  mailinglist_id integer unsigned NOT NULL default '0',
  form_id integer unsigned NOT NULL default '0',
  PRIMARY KEY  (rid)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS mailtrack_tbl;
CREATE TABLE mailtrack_tbl (
  mailtrack_id int(10) NOT NULL auto_increment,
  customer_id int(10) default NULL,
  mailing_id int(10) default NULL,
  company_id int(10) default NULL,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  status_id int(10) default NULL,
  PRIMARY KEY  (mailtrack_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS onepixel_log_tbl;
CREATE TABLE onepixel_log_tbl (
  company_id int(10) unsigned NOT NULL default '0',
  mailing_id int(10) unsigned NOT NULL default '0',
  customer_id int(10) unsigned NOT NULL default '0',
  open_count int(10) unsigned NOT NULL default '1',
  change_date timestamp NULL default NULL,
  ip_adr varchar(15)  NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS plugins_tbl;
CREATE TABLE plugins_tbl (
  plugin_id varchar(100)  NOT NULL,
  activate_on_startup int(1) NOT NULL,
  PRIMARY KEY  (plugin_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS rdir_action_tbl;
CREATE TABLE rdir_action_tbl (
  action_id int(10) unsigned NOT NULL auto_increment,
  shortname varchar(200)  NOT NULL default '',
  description text ,
  action_type int(10) unsigned NOT NULL default '0',
  company_id int(10) unsigned NOT NULL default '0',
  operations blob,
  PRIMARY KEY  (action_id)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ;


INSERT INTO rdir_action_tbl (action_id, shortname, description, action_type, company_id, operations) VALUES (1,'doi_user_confirm','step 1/2: confirm user',1,1,'ACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000177040000000A7372002B6F72672E61676E697461732E616374696F6E732E6F70732E4163746976617465446F75626C654F7074496E6C8B81A57A32855A020000787078'),
	(2,'doi_user_register (de)','step 2/2: save user data, send doi-mail',1,1,'ACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000277040000000A737200296F72672E61676E697461732E616374696F6E732E6F70732E537562736372696265437573746F6D65722AD691805687516A0200035A000B646F75626C65436865636B5A000B646F75626C654F7074496E4C00096B6579436F6C756D6E7400124C6A6176612F6C616E672F537472696E673B78700101740005656D61696C737200236F72672E61676E697461732E616374696F6E732E6F70732E53656E644D61696C696E6709E1AF9AA34EBEAB02000249000C64656C61794D696E757465734900096D61696C696E6749447870000000000000000378'),
	(3,'doi_user_register (en)','step 2/2: save user data, send doi-mail',1,1,'ACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000277040000000A737200296F72672E61676E697461732E616374696F6E732E6F70732E537562736372696265437573746F6D65722AD691805687516A0200035A000B646F75626C65436865636B5A000B646F75626C654F7074496E4C00096B6579436F6C756D6E7400124C6A6176612F6C616E672F537472696E673B78700101740005656D61696C737200236F72672E61676E697461732E616374696F6E732E6F70732E53656E644D61696C696E6709E1AF9AA34EBEAB02000249000C64656C61794D696E757465734900096D61696C696E6749447870000000000000000478'),
	(4,'user_get_data','load data from database',1,1,'ACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000177040000000A737200236F72672E61676E697461732E616374696F6E732E6F70732E476574437573746F6D65729A70BAE4FE18BCD30200015A000A6C6F6164416C7761797378700078'),
	(5,'user_subscribe','subscribe user',1,1,'ACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000177040000000A737200296F72672E61676E697461732E616374696F6E732E6F70732E537562736372696265437573746F6D65722AD691805687516A0200035A000B646F75626C65436865636B5A000B646F75626C654F7074496E4C00096B6579436F6C756D6E7400124C6A6176612F6C616E672F537472696E673B78700100740005656D61696C78'),
	(6,'user_unsubscribe','unsubscribe user',1,1,'ACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000177040000000A7372002B6F72672E61676E697461732E616374696F6E732E6F70732E556E737562736372696265437573746F6D657216BBF6CEE04FB108020000787078');



DROP TABLE IF EXISTS rdir_log_tbl;
CREATE TABLE rdir_log_tbl (
  company_id integer NOT NULL default '0',
  customer_id integer NOT NULL default '0',
  mailing_id integer NOT NULL default '0',
  ip_adr varchar(15)  NOT NULL default '',
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  url_id integer NOT NULL default '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS rdir_url_tbl;
CREATE TABLE rdir_url_tbl (
  url_id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL default '0',
  mailing_id int(10) unsigned NOT NULL default '0',
  action_id int(10) unsigned NOT NULL default '0',
  measure_type int(10) unsigned NOT NULL default '0',
  full_url text  NOT NULL,
  shortname varchar(200)  default NULL,
  relevance int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (url_id)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ;


INSERT INTO rdir_url_tbl (url_id, company_id, mailing_id, action_id, measure_type, full_url, shortname, relevance) VALUES (1,1,1,0,3,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_profil&agnUID=##AGNUID##',NULL,0),
	(2,1,1,0,3,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_unsubscribe&agnUID=##AGNUID##',NULL,0),
	(3,1,2,0,3,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_profil&agnUID=##AGNUID##',NULL,0),
	(4,1,2,0,3,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_unsubscribe&agnUID=##AGNUID##',NULL,0),
	(5,1,3,0,3,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_doi_welcome&agnUID=##AGNUID##',NULL,0),
	(6,1,4,0,3,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_doi_welcome&agnUID=##AGNUID##',NULL,0);



DROP TABLE IF EXISTS rulebased_sent_tbl;
CREATE TABLE rulebased_sent_tbl (
  mailing_id integer default NULL,
  lastsent timestamp NOT NULL default CURRENT_TIMESTAMP 
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS softbounce_email_tbl;
CREATE TABLE softbounce_email_tbl (
  email varchar(200)  NOT NULL default '',
  bnccnt integer NOT NULL default '0',
  mailing_id integer NOT NULL default '0',
  creation_date timestamp NULL default NULL,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  company_id integer NOT NULL default '0',
  KEY email_981 (email)  
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS tag_tbl;
CREATE TABLE tag_tbl (
  tag_id int(10) unsigned NOT NULL auto_increment,
  tagname varchar(64)  NOT NULL default '',
  selectvalue text  NOT NULL,
  type varchar(10)  NOT NULL default '',
  company_id int(10) NOT NULL default '0',
  description text ,
  change_date timestamp NOT NULL default CURRENT_TIMESTAMP ,
  PRIMARY KEY  (tag_id)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 ;


INSERT INTO tag_tbl (tag_id, tagname, selectvalue, type, company_id, description, change_date) VALUES (1,'agnCUSTOMERID','cust.customer_id','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(2,'agnMAILTYPE','cust.mailtype','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(3,'agnIMAGE','''[rdir-domain]/image?ci=[company-id]&mi=[mailing-id]&name={name}''','COMPLEX',0,NULL,'2006-07-10 07:58:25'),
	(4,'agnDB','cust.{column}','COMPLEX',0,'Display one Column','2006-07-10 07:58:25'),
	(5,'agnTITLE','''builtin''','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(6,'agnFIRSTNAME','cust.firstname','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(7,'agnLASTNAME','cust.lastname','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(8,'agnEMAIL','cust.email','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(9,'agnDATE','date_format(current_timestamp, ''%d.%m.%Y'')','SIMPLE',0,NULL,'2006-07-10 07:58:25'),
	(10,'agnIMGLINK','''<a href=\"[rdir-domain]/r.html?uid=[agnUID]\"><img src=\"[rdir-domain]/image?ci=[company-id]&mi=[mailing-id]&name={name}\" border=\"0\"></a>''','COMPLEX',0,NULL,'2008-06-08 22:00:00'),
	(11,'agnFORM','''[rdir-domain]/form.do?agnCI=1&agnFN={name}&agnUID=##agnUID##''','COMPLEX',0,'create a link to a site','2012-07-31 15:35:24'),
	(12,'agnPROFILE','''[rdir-domain]/form.do?agnCI=1\&agnFN=profile\&agnUID=##agnUID##''','COMPLEX',0,'create a link to an openemm-profile-form','2012-07-31 15:35:24'),
	(13,'agnUNSUBSCRIBE','''[rdir-domain]/form.do?agnCI=1\&agnFN=unsubscribe\&agnUID=##agnUID##''','COMPLEX',0,'create a link to an openemm-unsubscribe-form','2012-07-31 15:35:24');



DROP TABLE IF EXISTS timestamp_tbl;
CREATE TABLE timestamp_tbl (
  timestamp_id int(10) default NULL,
  description varchar(250)  default NULL,
  cur timestamp NOT NULL default CURRENT_TIMESTAMP,
  prev timestamp NOT NULL default '0000-00-00 00:00:00',
  temp timestamp NOT NULL default '0000-00-00 00:00:00'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS title_gender_tbl;
CREATE TABLE title_gender_tbl (
  title_id integer NOT NULL default '0',
  gender integer NOT NULL default '0',
  title varchar(50)  NOT NULL default '',
  PRIMARY KEY  (title_id,gender)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO title_gender_tbl (title_id, gender, title) VALUES (1,0,'Mr.'),
	(1,1,'Ms.'),
	(1,2,'Company'),
	(2,0,'Herr'),
	(2,1,'Frau'),
	(2,2,'Firma');



DROP TABLE IF EXISTS title_tbl;
CREATE TABLE title_tbl (
  company_id integer NOT NULL default '0',
  title_id integer NOT NULL auto_increment,
  description varchar(255)  NOT NULL default '',
  PRIMARY KEY  (title_id)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ;


INSERT INTO title_tbl (company_id, title_id, description) VALUES (1,1,'Default'),
	(1,2,'German Default');



DROP TABLE IF EXISTS userform_tbl;
CREATE TABLE userform_tbl (
  form_id int(10) unsigned NOT NULL auto_increment,
  formname varchar(200)  NOT NULL default '',
  description text  NOT NULL,
  company_id int(10) unsigned NOT NULL default '0',
  startaction_id int(10) unsigned NOT NULL default '0',
  endaction_id int(10) unsigned NOT NULL default '0',
  success_template longtext  NOT NULL,
  error_template longtext  NOT NULL,
  success_url varchar(100)  default NULL,
  error_url varchar(100)  default NULL,
  error_use_url int(1) unsigned NOT NULL default '0',
  success_use_url int(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (form_id),
  KEY formname_1077 (formname)  
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 ;


INSERT INTO userform_tbl (form_id, formname, description, company_id, startaction_id, endaction_id, success_template, error_template, success_url, error_url, error_use_url, success_use_url) VALUES (1,'de_doi','double-opt-in german 1/3',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_doi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG 1/3</h1>\r\n                  <p>vielen Dank f&uuml;r Ihr Interesse an unserem Angebot.<br>\r\n                  Hier k&ouml;nnen Sie sich zum Newsletter registrieren:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">Anrede:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unbekannt</option>\r\n                            <option value=\"1\">Frau</option>\r\n                            <option value=\"0\">Herr</option>\r\n                          </select></td></tr>\r\n                  <tr><td>Vorname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>Nachname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>E-Mail-Adresse:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">Newsletterformat:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML (mit Bildern)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text (Plaintext)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Absenden\"></td>\r\n                      <td><input type=\"reset\" value=\"Abbrechen\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(2,'de_doi_confirm','double-opt-in german 2/3',1,2,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG 2/3</h1>\r\n                  <p>Ihre Daten wurden erfolgreich angenommen.<br><br>\r\n                  Bitte best&auml;tigen Sie Ihr Abonnement in der E-Mail,<br>die wir Ihnen in K&uuml;rze zustellen, um den Bestellprozess abzuschlie&szlig;en.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(3,'de_doi_welcome','double-opt-in german 3/3',1,1,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG 3/3</h1>\r\n                  <h1>Willkommen</h1>\r\n                  <p>Ihre Anmeldung ist abgeschlossen und wir freuen uns,<br>Sie in unserem Newsletterverteiler begr&uuml;&szlig;en zu d&uuml;rfen.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN FEHLER</h1>\r\n                  <p>Leider konnten Ihre Daten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(4,'de_profil','profile german 1/2',1,4,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_profil_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">        \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN</h1>\r\n                  <p>Sie erhalten unseren Newsletter und m&ouml;chten Ihre Daten &auml;ndern:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">Anrede:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" #if($!customerData.GENDER == \"2\") selected #end>unbekannt</option>\r\n                            <option value=\"1\" #if($!customerData.GENDER == \"1\") selected #end>Frau</option>\r\n                            <option value=\"0\" #if($!customerData.GENDER == \"0\") selected #end>Herr</option>\r\n                          </select></td></tr>\r\n                  <tr><td>Vorname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\" value=\"$!customerData.FIRSTNAME\"></td></tr>\r\n                  <tr><td>Nachname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\" value=\"$!customerData.LASTNAME\"></td></tr>\r\n                  <tr><td>E-Mail-Adresse:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\" value=\"$!customerData.EMAIL\"></td></tr>\r\n                  <tr><td valign=\"top\">Newsletterformat:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" #if($!customerData.MAILTYPE == \"1\") checked #end>HTML (mit Bildern)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\" #if($!customerData.MAILTYPE == \"0\") checked #end>Text (Plaintext)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Speichern\"></td>\r\n                      <td><input type=\"reset\" value=\"Abbrechen\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN FEHLER</h1>\r\n                  <p>Leider konnten Ihre Daten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(5,'de_profil_confirm','profile german 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN</h1>\r\n                  <p>Ihre &Auml;nderungen wurden erfolgreich &uuml;bernommen.<br>\r\n                  Ab der n&auml;chsten Ausgabe ber&uuml;cksichitgen wir Ihre &Auml;nderungen.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN FEHLER</h1>\r\n                  <p>Leider konnten Ihre Daten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(6,'de_soi','single-opt-in german 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_soi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG 1/2</h1>\r\n                  <p>vielen Dank f&uuml;r Ihr Interesse an unserem Angebot.<br>\r\n                  Hier k&ouml;nnen Sie sich zum Newsletter registrieren:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">Anrede:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unbekannt</option>\r\n                            <option value=\"1\">Frau</option>\r\n                            <option value=\"0\">Herr</option>\r\n                          </select></td></tr>\r\n                  <tr><td>Vorname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>Nachname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>E-Mail-Adresse:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">Newsletterformat:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML (mit Bildern)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text (Plaintext)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Absenden\"></td>\r\n                      <td><input type=\"reset\" value=\"Abbrechen\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(7,'de_soi_confirm','single-opt-in german 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG 2/2</h1>\r\n                  <p>Wir konnten Ihre Anmeldung erfolgreich annehmen.<br>\r\n                  Ab der n&auml;chsten Ausgabe erhalten Sie unseren Newsletter.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(8,'de_unsub_confirm','unsubscribe german 2/2',1,6,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG 2/2</h1>\r\n                  <p>Ihre Abmeldung wurde erfolgreich entgegengenommen.<br>\r\n                  Sie erhalten keine weiteren Newsletterausgaben.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Abmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(9,'de_unsubscribe','unsubscribe german 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_unsub_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">       \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG 1/2</h1>\r\n                  <p>M&ouml;chten Sie den Newsletter wirklich abbestellen?</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\"><input type=\"submit\" value=\" Ja \"></td>\r\n                      <td><input type=\"reset\" value=\" Nein \" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Abmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(10,'en_soi','single-opt-in english 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_soi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>SUBSCRIBE NEWSLETTER 1/2</h1>\r\n                  <p>Thank you for your interest!<br>\r\n                  Register here:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">salutation:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unknown</option>\r\n                            <option value=\"1\">Ms.</option>\r\n                            <option value=\"0\">Mr.</option>\r\n                          </select></td></tr>\r\n                  <tr><td>firstname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>lastname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>eMail:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">mail format:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML (includes images)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text (plaintext only)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Send\"></td>\r\n                      <td><input type=\"reset\" value=\"Cancel\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER SUBSCRIPTION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(11,'en_soi_confirm','single-opt-in english 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER SUBSCRIPTION 2/2</h1>\r\n                  <p>Your newsletter registration was successful.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER SUBSCRIPTION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(12,'en_unsubscribe','unsubscribe english 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscribe</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_unsub_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">       \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE NEWSLETTER 1/2</h1>\r\n                  <p>Do you really want to unsubscribe from our newsletter?</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\"><input type=\"submit\" value=\" Yes \"></td>\r\n                      <td><input type=\"reset\" value=\" No \" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(13,'en_unsub_confirm','unsubscribe english 2/2',1,6,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscribe</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE NEWSLETTER 2/2</h1>\r\n                  <p>Your newsletter unsubscription was successful.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,NULL,0,0),
	(14,'en_profil','profile english 1/2',1,4,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_profil_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">        \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Please change your data here:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">salutation:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" #if($!customerData.GENDER == \"2\") selected #end>unknown</option>\r\n                            <option value=\"1\" #if($!customerData.GENDER == \"1\") selected #end>Ms.</option>\r\n                            <option value=\"0\" #if($!customerData.GENDER == \"0\") selected #end>Mr.</option>\r\n                          </select></td></tr>\r\n                  <tr><td>firstname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\" value=\"$!customerData.FIRSTNAME\"></td></tr>\r\n                  <tr><td>lastname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\" value=\"$!customerData.LASTNAME\"></td></tr>\r\n                  <tr><td>eMail:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\" value=\"$!customerData.EMAIL\"></td></tr>\r\n                  <tr><td valign=\"top\">eMail format:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" #if($!customerData.MAILTYPE == \"1\") checked #end>HTML<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\" #if($!customerData.MAILTYPE == \"0\") checked #end>Text</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Save\"></td>\r\n                      <td><input type=\"reset\" value=\"Cancel\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Sorry, your data could not be saved.<br>\r\n                  Please check your settings and try again.</p>\r\n                  <p>&nbsp;</p>\r\n                  <p>Greeting<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(15,'en_profil_confirm','profile english 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Your setting have been changed successfully.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Sorry, your data could not be saved.<br>\r\n                  Please check your settings and try again.</p>\r\n                  <p>&nbsp;</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(16,'en_doi','double-opt-in english 1/3',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_doi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION 1/3</h1>\r\n                  <p>Subscribe our newsletter here:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">salutation:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unknown</option>\r\n                            <option value=\"1\">Ms.</option>\r\n                            <option value=\"0\">Mr.</option>\r\n                          </select></td></tr>\r\n                  <tr><td>firstname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>lastname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>eMail:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">eMail format:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Send\"></td>\r\n                      <td><input type=\"reset\" value=\"Cancel\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(17,'en_doi_confirm','double-opt-in english 2/3',1,3,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION 2/3</h1>\r\n                  <p>Your data were saved successfully.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(18,'en_doi_welcome','double-opt-in english 3/3',1,1,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION 3/3</h1>\r\n                  <h1>Wellcome</h1>\r\n                  <p>Your registration was finished.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>',NULL,NULL,0,0),
	(19,'redirection_check','check if redirect answer\r\n(see explanation at error-form)',1,0,0,'database is ok','error!\r\n\r\nThis form may be used by a surveillance software like Nagios to check if OpenEMM is alive. To call this form use link\r\n\r\nhttp://your.domain.com/form.do?agnCI=1&agnFN=redirection_check\r\n\r\n(WARNING: Please change www.my-company.de to your rdir-link!)',NULL,NULL,0,0);



DROP TABLE IF EXISTS webservice_user_tbl;
CREATE TABLE webservice_user_tbl (
  username varchar(50)  NOT NULL,
  password varchar(50)  NOT NULL,
  company_id integer NOT NULL DEFAULT 1,
  PRIMARY KEY  (username)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS ws_admin_tbl;
CREATE TABLE ws_admin_tbl (
  ws_admin_id int(22) NOT NULL default '0',
  username varchar(50)  default NULL,
  password varchar(50)  default NULL,
  PRIMARY KEY  (ws_admin_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;






