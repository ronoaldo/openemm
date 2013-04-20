ALTER TABLE customer_1_binding_tbl ADD CONSTRAINT cust_1_bind_un UNIQUE KEY (customer_id, mailinglist_id);

INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) VALUES (4, 'mailing.send.admin.options');