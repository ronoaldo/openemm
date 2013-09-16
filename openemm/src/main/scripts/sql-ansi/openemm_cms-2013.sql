


DROP TABLE IF EXISTS cm_category_tbl;
CREATE TABLE cm_category_tbl (
  id int(10) unsigned NOT NULL,
  company_id int(10) unsigned NOT NULL,
  shortname varchar(255)  NOT NULL,
  description varchar(255)  NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_category_tbl_seq;
CREATE TABLE cm_category_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_category_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_content_module_tbl;
CREATE TABLE cm_content_module_tbl (
  id int(10) unsigned NOT NULL,
  company_id int(10) unsigned NOT NULL,
  shortname varchar(255)  NOT NULL,
  description varchar(255)  NOT NULL,
  content longtext  NOT NULL,
  category_id int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_content_tbl;
CREATE TABLE cm_content_tbl (
  id int(10) unsigned NOT NULL,
  content_module_id int(10) unsigned NOT NULL,
  tag_name varchar(255)  NOT NULL,
  content longtext  NOT NULL,
  tag_type integer unsigned NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_content_tbl_seq;
CREATE TABLE cm_content_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_content_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_location_tbl;
CREATE TABLE cm_location_tbl (
  id int(10) unsigned NOT NULL,
  mailing_id int(10) unsigned NOT NULL,
  cm_template_id int(10) unsigned NOT NULL,
  content_module_id int(10) unsigned NOT NULL,
  dyn_name varchar(100)  NOT NULL,
  dyn_order int(10) unsigned NOT NULL,
  target_group_id int(10) unsigned NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_location_tbl_seq;
CREATE TABLE cm_location_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_location_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_mailing_bind_tbl;
CREATE TABLE cm_mailing_bind_tbl (
  id int(10) unsigned NOT NULL,
  mailing_id int(10) unsigned NOT NULL,
  content_module_id int(10) unsigned NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_mailing_bind_tbl_seq;
CREATE TABLE cm_mailing_bind_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_mailing_bind_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_media_file_tbl;
CREATE TABLE cm_media_file_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  cm_template_id int(10) unsigned NOT NULL default '0',
  content_module_id int(10) unsigned NOT NULL default '0',
  company_id int(10) unsigned NOT NULL default '0',
  media_name varchar(255)  NOT NULL default '',
  content longblob,
  media_type int(10) unsigned NOT NULL default '0',
  mime_type varchar(255)  default NULL,
  cmtId int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_media_file_tbl_seq;
CREATE TABLE cm_media_file_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_media_file_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_tbl_seq;
CREATE TABLE cm_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_template_mail_bind_tbl_seq;
CREATE TABLE cm_template_mail_bind_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_template_mail_bind_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_template_mailing_bind_tbl;
CREATE TABLE cm_template_mailing_bind_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  mailing_id int(10) unsigned NOT NULL,
  cm_template_id int(10) unsigned NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_template_tbl;
CREATE TABLE cm_template_tbl (
  id int(10) unsigned NOT NULL,
  company_id int(10) unsigned NOT NULL default '0',
  shortname varchar(255)  NOT NULL default ' ',
  description varchar(255)  NOT NULL default ' ',
  content longblob NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_template_tbl_seq;
CREATE TABLE cm_template_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_template_tbl_seq (value) VALUES (0);



DROP TABLE IF EXISTS cm_text_version_tbl;
CREATE TABLE cm_text_version_tbl (
  id int(10) unsigned NOT NULL,
  admin_id int(10) unsigned NOT NULL,
  text text  NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;


INSERT INTO cm_text_version_tbl (id, admin_id, text) VALUES (1,0,'Dear Reader,\n\nthis e-mail was created in HTLM format only. Almost every e-mail client supports HTML mails, but it could be that your client is not permitted to display HTML mails.\n\nHowever, you can view this e-mail in HTML format in your browser:\n\nhttp://localhost:8080/form.do?agnCI=1&agnFN=fullview&agnUID=##AGNUID##\n\nTo unsubscribe from the list of this mailing please click this link:\n\nhttp://localhost:8080/form.do?agnCI=1&agnFN=unsubscribe&agnUID=##AGNUID##');



DROP TABLE IF EXISTS cm_text_version_tbl_seq;
CREATE TABLE cm_text_version_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_text_version_tbl_seq (value) VALUES (1);



DROP TABLE IF EXISTS cm_type_tbl;
CREATE TABLE cm_type_tbl (
  id int(10) unsigned NOT NULL auto_increment,
  company_id int(10) unsigned NOT NULL,
  shortname varchar(255)  NOT NULL,
  description varchar(255)  NOT NULL,
  content longtext  NOT NULL,
  read_only tinyint(1) NOT NULL,
  is_public tinyint(1) NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;





DROP TABLE IF EXISTS cm_type_tbl_seq;
CREATE TABLE cm_type_tbl_seq (
  value integer NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO cm_type_tbl_seq (value) VALUES (0);




