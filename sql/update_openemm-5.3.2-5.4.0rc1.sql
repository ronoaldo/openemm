--
-- Extend MAILLOOP_TBL for subscriber interface
--
alter table `mailloop_tbl` add `subscribe_enable` int(1) unsigned NOT NULL default '0';
alter table `mailloop_tbl` add `mailinglist_id` int(11) unsigned NOT NULL default '0';
alter table `mailloop_tbl` add `form_id` int(11) unsigned NOT NULL default '0';

--
-- Add new permissions for update process
--
insert into admin_group_permission_tbl (admin_group_id, security_token) values (4, 'update.show');
