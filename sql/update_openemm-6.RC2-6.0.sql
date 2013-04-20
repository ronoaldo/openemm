CREATE TABLE `login_track_tbl` (
`login_track_id` int(11) NOT NULL primary key auto_increment,
`ip_address` varchar(20),
`creation_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
`login_status` int(11),
`username` varchar(50)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE INDEX `logtrck$ip_cdate_stat$idx` ON `login_track_tbl` (ip_address, creation_date, login_status);

ALTER TABLE `company_tbl` ADD COLUMN `max_login_fails` int(3) NOT NULL default 3;
ALTER TABLE `company_tbl` ADD COLUMN `login_block_time` int(5) NOT NULL default 300;



