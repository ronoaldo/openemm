create table date_tbl (type int(11) NOT NULL, format varchar(100));

insert into date_tbl (type, format) values (0, 'd.M.yyyy');

insert into date_tbl (type, format) values (1, 'MM/dd/yyyy');

insert into date_tbl (type, format) values (2, 'EEEE d MMMM yyyy');

insert into date_tbl (type, format) values (3, 'yyyy-MM-dd');

ALTER TABLE `customer_1_tbl_seq` MODIFY `customer_id` int(11) AUTO_INCREMENT; 