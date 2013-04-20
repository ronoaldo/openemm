CREATE TABLE IF NOT EXISTS `config_tbl` (
	`class`	VARCHAR(32) NOT NULL,
	`classid`	INT(11) NOT NULL default 0,
	`name` VARCHAR(32) NOT NULL,
	`value`	TEXT
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO config_tbl (`class`, `classid`, `name`, `value`) VALUES ('linkchecker', 0, 'linktimeout', 20000);
INSERT INTO config_tbl (`class`, `classid`, `name`, `value`) VALUES ('linkchecker', 0, 'threadcount', 20);

ALTER TABLE dyn_name_tbl ADD deleted int(1) NOT NULL DEFAULT 0;

ALTER TABLE dyn_target_tbl ADD `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;
ALTER TABLE dyn_target_tbl ADD `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00';

UPDATE dyn_target_tbl SET change_date=CURRENT_TIMESTAMP, creation_date=CURRENT_TIMESTAMP;

ALTER TABLE component_tbl ADD description VARCHAR(200);

INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) VALUES (4, 'import.mode.blacklist'),(4,'adminlog.show'),(4,'userlog.show');

ALTER TABLE mailing_tbl ADD `cms_has_classic_content` int(1) NOT NULL default 0;
ALTER TABLE mailing_tbl ADD `dynamic_template` int(1);

UPDATE mailing_tbl SET cms_has_classic_content=1;
UPDATE mailing_tbl mail SET cms_has_classic_content=0 WHERE mail.mailing_id IN (SELECT mailing_id FROM component_tbl comp WHERE compname='agnHtml' AND comptype=0 AND company_id=1 AND emmblock='[agnDYN name="HTML-Version"/]');

ALTER TABLE company_tbl ADD COLUMN uid_version int(2);

use openemm_cms

DROP TABLE IF EXISTS `cm_category_tbl`;
CREATE TABLE `cm_category_tbl` (
  `id` int(10) unsigned NOT NULL,
  `company_id` int(10) unsigned NOT NULL,
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `cm_category_tbl_seq`;
CREATE TABLE cm_category_tbl_seq (value int NOT NULL) type=MYISAM;
INSERT INTO `cm_category_tbl_seq` (`value`) VALUES (0);

ALTER TABLE `cm_content_module_tbl` ADD COLUMN `category_id` INTEGER UNSIGNED NOT NULL DEFAULT 0 AFTER `content`;
