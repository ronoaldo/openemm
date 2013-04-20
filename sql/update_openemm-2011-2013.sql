DROP TABLE IF EXISTS `doc_mapping_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `doc_mapping_tbl` (
	`filename` VARCHAR(200),
	`pagekey` VARCHAR(50)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `plugins_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `plugins_tbl` (
	`plugin_id` VARCHAR(100) PRIMARY KEY NOT NULL,
	`activate_on_startup` INTEGER(1) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `webservice_user_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `webservice_user_tbl` (
	`username` VARCHAR(50) NOT NULL,
	`password` VARCHAR(50) NOT NULL,
	`company_id` int(11) NOT NULL DEFAULT 1,
	PRIMARY KEY  (username)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

ALTER TABLE `company_tbl` ADD COLUMN `max_recipients` INTEGER(11) DEFAULT 10000;

ALTER TABLE `doc_mapping_tbl` ADD CONSTRAINT `doc_mapping$pagekey$unique` UNIQUE (pagekey);

ALTER TABLE `export_predef_tbl` ADD `timestamp_start` TIMESTAMP NULL;
ALTER TABLE `export_predef_tbl` ADD `timestamp_end` TIMESTAMP NULL;
ALTER TABLE `export_predef_tbl` ADD `creation_date_start` TIMESTAMP NULL;
ALTER TABLE `export_predef_tbl` ADD `creation_date_end` TIMESTAMP NULL;
ALTER TABLE `export_predef_tbl` ADD `mailinglist_bind_start` TIMESTAMP NULL;
ALTER TABLE `export_predef_tbl` ADD `mailinglist_bind_end` TIMESTAMP NULL;

ALTER TABLE `login_track_tbl` MODIFY `ip_address` varchar(40) collate utf8_unicode_ci default NULL;

ALTER TABLE `mailing_tbl` ADD COLUMN `openaction_id` int(11) unsigned default 0;
ALTER TABLE `mailing_tbl` ADD COLUMN `clickaction_id` int(11) unsigned default 0;
ALTER TABLE `mailing_tbl` MODIFY `dynamic_template` int(1) NOT NULL DEFAULT 0;

ALTER TABLE `userform_tbl` ADD COLUMN `success_url` varchar(100) collate utf8_unicode_ci default NULL;
ALTER TABLE `userform_tbl` ADD COLUMN `error_url` varchar(100) collate utf8_unicode_ci default NULL;
ALTER TABLE `userform_tbl` ADD COLUMN `error_use_url` int(1) unsigned NOT NULL default '0';
ALTER TABLE `userform_tbl` ADD COLUMN `success_use_url` int(1) unsigned NOT NULL default '0';

INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) VALUES (4, 'targets.change'),(4,'targets.delete'),(4, 'mailloop.change'),(4,'mailloop.delete'),(4,'pluginmanager.show'),(4,'mailinglist.recipients.delete'),(4, 'recipient.column.select');

UPDATE admin_tbl SET pwd_change = CURRENT_TIMESTAMP;

INSERT INTO `date_tbl` (`type`, `format`) VALUES (6, 'dd/MM/yyyy');
INSERT INTO `date_tbl` (`type`, `format`) VALUES (7, 'yyyy/MM/dd');
INSERT INTO `date_tbl` (`type`, `format`) VALUES (8, 'yyyy-MM-dd');

insert into doc_mapping_tbl (filename, pagekey) values ('what_are_templates_.htm','templateList');
insert into doc_mapping_tbl (filename, pagekey) values ('list_existing_mailings.htm','mailingList');
insert into doc_mapping_tbl (filename, pagekey) values ('sending_normal_file_attachment.htm','attachments');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_mailing.htm', 'createNewMailing');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_mailing_data.htm','newMailingNormal');
insert into doc_mapping_tbl (filename, pagekey) values ('create_new_mailing_using_the_w.htm','newMailingWizard');
insert into doc_mapping_tbl (filename, pagekey) values ('inserting_content.htm','contentList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_text_and_html_modules.htm','contentView');
insert into doc_mapping_tbl (filename, pagekey) values ('using_graphic_elements.htm','pictureComponents');
insert into doc_mapping_tbl (filename, pagekey) values ('create_trackable_and_non_track.htm','trackableLinks');
insert into doc_mapping_tbl (filename, pagekey) values ('preview___for_in_depth_checkin.htm','preview');
insert into doc_mapping_tbl (filename, pagekey) values ('testing_and_sending_a_mailing.htm','mailingTestAndSend');
insert into doc_mapping_tbl (filename, pagekey) values ('send_mailing.htm','sendMailing');
insert into doc_mapping_tbl (filename, pagekey) values ('mailing_statistics_openemm.htm','mailingStatistic');
insert into doc_mapping_tbl (filename, pagekey) values ('heatmap_openemm.htm','heatmap');
insert into doc_mapping_tbl (filename, pagekey) values ('show_available_cm_templates.htm','cmTemplateList');
insert into doc_mapping_tbl (filename, pagekey) values ('editing_cm_templates.htm','cmTemplateView');
insert into doc_mapping_tbl (filename, pagekey) values ('using_cm_templates_in_mailings.htm','cmTemplateForMailing');
insert into doc_mapping_tbl (filename, pagekey) values ('show_available_module_types.htm','cmModuleTypeList');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_data.htm','cmModuleTypeView');
insert into doc_mapping_tbl (filename, pagekey) values ('show_available_content_modules.htm','cmContentModuleList');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_data2.htm','cmContentModuleView');
insert into doc_mapping_tbl (filename, pagekey) values ('saving_a_content_moduke_and_as.htm','cmContentModuleAssign');
insert into doc_mapping_tbl (filename, pagekey) values ('cm_categories.htm','cmCategoryList');
insert into doc_mapping_tbl (filename, pagekey) values ('entering_basic_template_data.htm','newTemplate');
insert into doc_mapping_tbl (filename, pagekey) values ('display_and_amend_details.htm','newTemplateNormal');
insert into doc_mapping_tbl (filename, pagekey) values ('create_a_new_archive.htm','newArchive');
insert into doc_mapping_tbl (filename, pagekey) values ('display_and_amend_details.htm','archiveView');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_recipients.htm','recipientList');
insert into doc_mapping_tbl (filename, pagekey) values ('show_recipient_profile.htm','recipientView');
insert into doc_mapping_tbl (filename, pagekey) values ('create_new_recipients.htm','newRecipient');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_import_profile.htm','newImportProfile');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_fields.htm','manageFields');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_a_profile__deleting_a.htm','manageProfile');
insert into doc_mapping_tbl (filename, pagekey) values ('the_import_assistant.htm','importStep1');
insert into doc_mapping_tbl (filename, pagekey) values ('assigning_the_csv_columns_to_t.htm','importStep2');
insert into doc_mapping_tbl (filename, pagekey) values ('errorhandling.htm','importStep3');
insert into doc_mapping_tbl (filename, pagekey) values ('importing_the_csv_file.htm','importStep4');
insert into doc_mapping_tbl (filename, pagekey) values ('export_function_for_recipient_.htm','export');
insert into doc_mapping_tbl (filename, pagekey) values ('blacklist___do_not_mail.htm','blacklist');
insert into doc_mapping_tbl (filename, pagekey) values ('types_of_address.htm','salutationForms');
insert into doc_mapping_tbl (filename, pagekey) values ('comparing_mailings_openemm.htm','compareMailings');
insert into doc_mapping_tbl (filename, pagekey) values ('domain_statistics.htm','domainStatistic');
insert into doc_mapping_tbl (filename, pagekey) values ('ip_statistics.htm','ipStatistics');
insert into doc_mapping_tbl (filename, pagekey) values ('recipient_statistics_openemm.htm','recipientStatistic');
insert into doc_mapping_tbl (filename, pagekey) values ('what_is_a_traget_group_.htm','targetGroupList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_and_managing_target_g.htm','targetGroupView');
insert into doc_mapping_tbl (filename, pagekey) values ('modifying_a_mailing_list.htm','mailinglists');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_mailing_list.htm','newMailinglist');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_forms.htm','formList');
insert into doc_mapping_tbl (filename, pagekey) values ('this_is_how_forms_work.htm','formView');
insert into doc_mapping_tbl (filename, pagekey) values ('building_trackable_links_into_.htm','trackableLinkView');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_actions.htm','actionList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_action.htm','newAction');
insert into doc_mapping_tbl (filename, pagekey) values ('managing_profile_fields.htm','profileFieldList');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_new_fields.htm','newProfileField');
insert into doc_mapping_tbl (filename, pagekey) values ('creating_a_new_user_and_changi.htm','newUser');
insert into doc_mapping_tbl (filename, pagekey) values ('assigning_user_rights2.htm','userRights');
insert into doc_mapping_tbl (filename, pagekey) values ('bounce_filter.htm','bounceFilter');
insert into doc_mapping_tbl (filename, pagekey) values ('user_log.htm','userlog');
insert into doc_mapping_tbl (filename, pagekey) values ('automatic_update_of_openemm.htm', 'update');
insert into doc_mapping_tbl (filename, pagekey) values ('feedback_analysis_openemm.htm', 'feedbackAnalysis');

INSERT INTO `tag_tbl` (`tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES ('agnFORM', '\'[rdir-domain]/form.do?agnCI=1\&agnFN={name}\&agnUID=##agnUID##\'', 'COMPLEX', 0, 'create a link to a site', current_timestamp);
INSERT INTO `tag_tbl` (`tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES ('agnPROFILE', '\'[rdir-domain]/form.do?agnCI=1\&agnFN=profile\&agnUID=##agnUID##\'', 'COMPLEX', 0, 'create a link to an openemm-profile-form', current_timestamp);
INSERT INTO `tag_tbl` (`tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES ('agnUNSUBSCRIBE', '\'[rdir-domain]/form.do?agnCI=1\&agnFN=unsubscribe\&agnUID=##agnUID##\'', 'COMPLEX', 0, 'create a link to an openemm-unsubscribe-form', current_timestamp);

UPDATE click_stat_colors_tbl SET range_start = 0, range_end = 1 WHERE id = 1;
UPDATE click_stat_colors_tbl SET range_start = 1, range_end = 2 WHERE id = 2;
UPDATE click_stat_colors_tbl SET range_start = 2, range_end = 3 WHERE id = 3;
UPDATE click_stat_colors_tbl SET range_start = 3, range_end = 5 WHERE id = 4;
UPDATE click_stat_colors_tbl SET range_start = 5, range_end = 10 WHERE id = 5;
UPDATE click_stat_colors_tbl SET range_start = 10, range_end = 100 WHERE id = 6;

DROP TABLE IF EXISTS `customer_1_tbl_seq`;
DROP TABLE IF EXISTS `customer_import_errors_tbl`;
DROP TABLE IF EXISTS `customer_import_status_tbl`;
DROP TABLE IF EXISTS `emm_layout_tbl`;
DROP TABLE IF EXISTS `log_tbl`;
