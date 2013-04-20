alter table `component_tbl` add `url_id` int(1) unsigned NOT NULL default '0';

insert into tag_tbl (tagname, type, company_id, change_date, selectvalue) values ('agnIMGLINK', 'COMPLEX', 0, current_date, '''<a href="[rdir-domain]/r.html?uid=[agnUID]"><img src="[rdir-domain]/image?ci=[company-id]&mi=[mailing-id]&name={name}" border="0"></a>''');

insert into admin_group_permission_tbl (admin_group_id, security_token) values (4, 'action.op.ExecuteScript');

alter table `cust_ban_tbl` add `creation_date` timestamp not null default CURRENT_TIMESTAMP;

alter table `admin_tbl` add `preferred_list_size` smallint unsigned NOT NULL default '0'; 
