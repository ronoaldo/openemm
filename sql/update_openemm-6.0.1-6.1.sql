ALTER TABLE `dyn_target_tbl` ADD COLUMN `deleted` INTEGER(1) NOT NULL DEFAULT 0;

ALTER TABLE `import_profile_tbl` ADD `update_all_duplicates` NUMERIC(1,0) DEFAULT 0;
