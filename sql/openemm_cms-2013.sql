-- MySQL dump 10.11
--
-- Host: localhost    Database: openemm_cms
-- ------------------------------------------------------
-- Server version	5.0.77

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cm_category_tbl`
--

DROP TABLE IF EXISTS `cm_category_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_category_tbl` (
  `id` int(10) unsigned NOT NULL,
  `company_id` int(10) unsigned NOT NULL,
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_category_tbl`
--

LOCK TABLES `cm_category_tbl` WRITE;
/*!40000 ALTER TABLE `cm_category_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_category_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_category_tbl_seq`
--

DROP TABLE IF EXISTS `cm_category_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_category_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_category_tbl_seq`
--

LOCK TABLES `cm_category_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_category_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_category_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_category_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_content_module_tbl`
--

DROP TABLE IF EXISTS `cm_content_module_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_content_module_tbl` (
  `id` int(10) unsigned NOT NULL,
  `company_id` int(10) unsigned NOT NULL,
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  `category_id` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_content_module_tbl`
--

LOCK TABLES `cm_content_module_tbl` WRITE;
/*!40000 ALTER TABLE `cm_content_module_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_content_module_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_content_tbl`
--

DROP TABLE IF EXISTS `cm_content_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_content_tbl` (
  `id` int(10) unsigned NOT NULL,
  `content_module_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) collate utf8_unicode_ci NOT NULL,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  `tag_type` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_content_tbl`
--

LOCK TABLES `cm_content_tbl` WRITE;
/*!40000 ALTER TABLE `cm_content_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_content_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_content_tbl_seq`
--

DROP TABLE IF EXISTS `cm_content_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_content_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_content_tbl_seq`
--

LOCK TABLES `cm_content_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_content_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_content_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_content_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_location_tbl`
--

DROP TABLE IF EXISTS `cm_location_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_location_tbl` (
  `id` int(10) unsigned NOT NULL,
  `mailing_id` int(10) unsigned NOT NULL,
  `cm_template_id` int(10) unsigned NOT NULL,
  `content_module_id` int(10) unsigned NOT NULL,
  `dyn_name` varchar(100) collate utf8_unicode_ci NOT NULL,
  `dyn_order` int(10) unsigned NOT NULL,
  `target_group_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_location_tbl`
--

LOCK TABLES `cm_location_tbl` WRITE;
/*!40000 ALTER TABLE `cm_location_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_location_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_location_tbl_seq`
--

DROP TABLE IF EXISTS `cm_location_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_location_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_location_tbl_seq`
--

LOCK TABLES `cm_location_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_location_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_location_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_location_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_mailing_bind_tbl`
--

DROP TABLE IF EXISTS `cm_mailing_bind_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_mailing_bind_tbl` (
  `id` int(10) unsigned NOT NULL,
  `mailing_id` int(10) unsigned NOT NULL,
  `content_module_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_mailing_bind_tbl`
--

LOCK TABLES `cm_mailing_bind_tbl` WRITE;
/*!40000 ALTER TABLE `cm_mailing_bind_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_mailing_bind_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_mailing_bind_tbl_seq`
--

DROP TABLE IF EXISTS `cm_mailing_bind_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_mailing_bind_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_mailing_bind_tbl_seq`
--

LOCK TABLES `cm_mailing_bind_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_mailing_bind_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_mailing_bind_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_mailing_bind_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_media_file_tbl`
--

DROP TABLE IF EXISTS `cm_media_file_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_media_file_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `cm_template_id` int(10) unsigned NOT NULL default '0',
  `content_module_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `media_name` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `content` longblob,
  `media_type` int(10) unsigned NOT NULL default '0',
  `mime_type` varchar(255) collate utf8_unicode_ci default NULL,
  `cmtId` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_media_file_tbl`
--

LOCK TABLES `cm_media_file_tbl` WRITE;
/*!40000 ALTER TABLE `cm_media_file_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_media_file_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_media_file_tbl_seq`
--

DROP TABLE IF EXISTS `cm_media_file_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_media_file_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_media_file_tbl_seq`
--

LOCK TABLES `cm_media_file_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_media_file_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_media_file_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_media_file_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_tbl_seq`
--

DROP TABLE IF EXISTS `cm_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_tbl_seq`
--

LOCK TABLES `cm_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_template_mail_bind_tbl_seq`
--

DROP TABLE IF EXISTS `cm_template_mail_bind_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_template_mail_bind_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_template_mail_bind_tbl_seq`
--

LOCK TABLES `cm_template_mail_bind_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_template_mail_bind_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_template_mail_bind_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_template_mail_bind_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_template_mailing_bind_tbl`
--

DROP TABLE IF EXISTS `cm_template_mailing_bind_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_template_mailing_bind_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `mailing_id` int(10) unsigned NOT NULL,
  `cm_template_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_template_mailing_bind_tbl`
--

LOCK TABLES `cm_template_mailing_bind_tbl` WRITE;
/*!40000 ALTER TABLE `cm_template_mailing_bind_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_template_mailing_bind_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_template_tbl`
--

DROP TABLE IF EXISTS `cm_template_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_template_tbl` (
  `id` int(10) unsigned NOT NULL,
  `company_id` int(10) unsigned NOT NULL default '0',
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL default ' ',
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default ' ',
  `content` longblob NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_template_tbl`
--

LOCK TABLES `cm_template_tbl` WRITE;
/*!40000 ALTER TABLE `cm_template_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_template_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_template_tbl_seq`
--

DROP TABLE IF EXISTS `cm_template_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_template_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_template_tbl_seq`
--

LOCK TABLES `cm_template_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_template_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_template_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_template_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_text_version_tbl`
--

DROP TABLE IF EXISTS `cm_text_version_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_text_version_tbl` (
  `id` int(10) unsigned NOT NULL,
  `admin_id` int(10) unsigned NOT NULL,
  `text` text collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_text_version_tbl`
--

LOCK TABLES `cm_text_version_tbl` WRITE;
/*!40000 ALTER TABLE `cm_text_version_tbl` DISABLE KEYS */;
INSERT INTO `cm_text_version_tbl` (`id`, `admin_id`, `text`) VALUES (1,0,'Dear Reader,\n\nthis e-mail was created in HTLM format only. Almost every e-mail client supports HTML mails, but it could be that your client is not permitted to display HTML mails.\n\nHowever, you can view this e-mail in HTML format in your browser:\n\nhttp://localhost:8080/form.do?agnCI=1&agnFN=fullview&agnUID=##AGNUID##\n\nTo unsubscribe from the list of this mailing please click this link:\n\nhttp://localhost:8080/form.do?agnCI=1&agnFN=unsubscribe&agnUID=##AGNUID##');
/*!40000 ALTER TABLE `cm_text_version_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_text_version_tbl_seq`
--

DROP TABLE IF EXISTS `cm_text_version_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_text_version_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_text_version_tbl_seq`
--

LOCK TABLES `cm_text_version_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_text_version_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_text_version_tbl_seq` (`value`) VALUES (1);
/*!40000 ALTER TABLE `cm_text_version_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_type_tbl`
--

DROP TABLE IF EXISTS `cm_type_tbl`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_type_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL,
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  `read_only` tinyint(1) NOT NULL,
  `is_public` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_type_tbl`
--

LOCK TABLES `cm_type_tbl` WRITE;
/*!40000 ALTER TABLE `cm_type_tbl` DISABLE KEYS */;
/*!40000 ALTER TABLE `cm_type_tbl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cm_type_tbl_seq`
--

DROP TABLE IF EXISTS `cm_type_tbl_seq`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cm_type_tbl_seq` (
  `value` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `cm_type_tbl_seq`
--

LOCK TABLES `cm_type_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_type_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_type_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_type_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

GRANT DELETE, INSERT, UPDATE, LOCK TABLES, SELECT, ALTER, INDEX, CREATE
TEMPORARY TABLES, DROP, CREATE ON openemm_cms.* TO 'agnitas'@'localhost'
IDENTIFIED BY 'openemm';

FLUSH PRIVILEGES;
