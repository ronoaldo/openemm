DELETE FROM `tag_tbl` WHERE `tagname` = 'agnFORM';

INSERT INTO `tag_tbl` (`tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES ('agnFORM', '\'[rdir-domain]/form.do?agnCI=1\&agnFN={name}\&agnUID=##agnUID##\'', 'COMPLEX', 0, 'create a link to a site', current_timestamp);
INSERT INTO `tag_tbl` (`tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES ('agnPROFILE', '\'[rdir-domain]/form.do?agnCI=1\&agnFN=profile\&agnUID=##agnUID##\'', 'COMPLEX', 0, 'create a link to an openemm-profile-form', current_timestamp);
INSERT INTO `tag_tbl` (`tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES ('agnUNSUBSCRIBE', '\'[rdir-domain]/form.do?agnCI=1\&agnFN=unsubscribe\&agnUID=##agnUID##\'', 'COMPLEX', 0, 'create a link to an openemm-unsubscribe-form', current_timestamp);
