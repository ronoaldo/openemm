-- 
-- Tabellenstruktur für Tabelle `log_tbl`
-- 
create table `log_tbl` (
    `log_id` int(11) NOT NULL auto_increment,
    `company_id` int(11),
    `admin_id` int(11),
    `creation_date` timestamp not null default CURRENT_TIMESTAMP,
    `category` int(11),
    `ip_adr` varchar(20),
    `message` varchar(2000),
    PRIMARY KEY  (`log_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- geänderte Tabellenstruktur für Tabelle `mailing_tbl`
-- 
alter table `mailing_tbl` add `archived` int(11) unsigned NOT NULL default '0';

-- 
-- geänderte Tabellenstruktur für Tabelle `company_tbl`
-- 
alter table `company_tbl` add `mailtracking` int(11) unsigned NOT NULL default '0';

-- 
-- Daten für Tabelle `admin_group_permission_tbl`
-- 
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'action.op.GetArchiveList');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'action.op.GetArchiveMailing');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'targets.createml');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'mailing.archived');

-- 
-- geänderte Daten für Tabelle `company_tbl`
-- 
UPDATE `company_tbl` set mailtracking=1 where company_id=1;
 
