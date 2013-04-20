DROP TABLE IF EXISTS `mailing_status_tbl`;

ALTER TABLE `customer_1_tbl` MODIFY `gender` int(11) NOT NULL default '2';
ALTER TABLE `date_tbl` CONVERT TO CHARSET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE `admin_tbl` ADD COLUMN `default_import_profile_id` INTEGER NOT NULL DEFAULT 0;
ALTER TABLE `company_tbl` ADD COLUMN `max_login_fails` int(3) NOT NULL default 3;
ALTER TABLE `company_tbl` ADD COLUMN `login_block_time` int(5) NOT NULL default 300;

INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) VALUES (4, 'action.op.ServiceMail'),(4, 'blacklist'),(4,'cms.central_content_management'),(4,'cms.mailing_content_management');

CREATE TABLE `login_track_tbl` (
`login_track_id` int(11) NOT NULL primary key auto_increment,
`ip_address` varchar(20),
`creation_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
`login_status` int(11),
`username` varchar(50)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE INDEX `logtrck$ip_cdate_stat$idx` ON `login_track_tbl` (ip_address, creation_date, login_status);

--
-- Table for storing import profiles
--
DROP TABLE IF EXISTS `import_profile_tbl`;
CREATE TABLE `import_profile_tbl` (
`id` int(10) unsigned NOT NULL auto_increment,
`company_id` int(10) unsigned NOT NULL,
`admin_id` int(10) unsigned NOT NULL,
`shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
`column_separator` int(10) unsigned NOT NULL,
`text_delimiter` int(10) unsigned NOT NULL,
`file_charset` int(10) unsigned NOT NULL,
`date_format` int(10) unsigned NOT NULL,
`import_mode` int(10) unsigned NOT NULL,
`null_values_action` int(10) unsigned NOT NULL,
`key_column` varchar(255) collate utf8_unicode_ci NOT NULL,
`ext_email_check` tinyint(1) NOT NULL,
`report_email` varchar(255) collate utf8_unicode_ci NOT NULL,
`check_for_duplicates` int(10) unsigned NOT NULL,
`mail_type` int(10) unsigned NOT NULL,
PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for storing column mappings of import profile
--
DROP TABLE IF EXISTS `import_column_mapping_tbl`;
CREATE TABLE  `import_column_mapping_tbl` (
`id` int(10) unsigned NOT NULL auto_increment,
`profile_id` int(10) unsigned NOT NULL,
`file_column` varchar(255) collate utf8_unicode_ci NOT NULL,
`db_column` varchar(255) collate utf8_unicode_ci NOT NULL,
`mandatory` tinyint(1) NOT NULL,
`default_value` varchar(255) collate utf8_unicode_ci DEFAULT '',
PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for storing gender mappings of import profile
--
DROP TABLE IF EXISTS `import_gender_mapping_tbl`;
CREATE TABLE  `import_gender_mapping_tbl` (
`id` int(10) unsigned NOT NULL auto_increment,
`profile_id` int(10) unsigned NOT NULL,
`int_gender` int(10) unsigned NOT NULL,
`string_gender` varchar(100) collate utf8_unicode_ci NOT NULL,
PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for logging import results
--
DROP TABLE IF EXISTS `import_log_tbl`;
CREATE TABLE  `import_log_tbl` (
`log_id` int(10) unsigned NOT NULL auto_increment,
`company_id` int(10) unsigned NOT NULL,
`admin_id` int(10) unsigned NOT NULL,
`creation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`imported_lines` int(10) unsigned NOT NULL,
`datasource_id` int(10) unsigned NOT NULL,
`statistics` text collate utf8_unicode_ci NOT NULL,
`profile` text collate utf8_unicode_ci NOT NULL,
PRIMARY KEY  (`log_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for table `click_stat_colors_tbl`
--
DROP TABLE IF EXISTS `click_stat_colors_tbl`;
CREATE TABLE `click_stat_colors_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL,
  `range_start` int(10) NOT NULL,
  `range_end` int(10) NOT NULL,
  `color` varchar(6) collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `click_stat_colors_tbl`
--
LOCK TABLES `click_stat_colors_tbl` WRITE;
/*!40000 ALTER TABLE `click_stat_colors_tbl` DISABLE KEYS */;
insert into `click_stat_colors_tbl` (`company_id`, `range_start`, `range_end`, `color`) values (1, 0, 5, 'F4F9FF');
insert into `click_stat_colors_tbl` (`company_id`, `range_start`, `range_end`, `color`) values (1, 5, 10, 'D5E6FF');
insert into `click_stat_colors_tbl` (`company_id`, `range_start`, `range_end`, `color`) values (1, 10, 15, 'E1F7E1');
insert into `click_stat_colors_tbl` (`company_id`, `range_start`, `range_end`, `color`) values (1, 15, 20, 'FEFECC');
insert into `click_stat_colors_tbl` (`company_id`, `range_start`, `range_end`, `color`) values (1, 20, 25, 'FFE4BA');
insert into `click_stat_colors_tbl` (`company_id`, `range_start`, `range_end`, `color`) values (1, 25, 100, 'FFCBC3');
/*!40000 ALTER TABLE `click_stat_colors_tbl` ENABLE KEYS */;
UNLOCK TABLES;
