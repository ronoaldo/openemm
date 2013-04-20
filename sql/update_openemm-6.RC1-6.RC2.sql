ALTER TABLE `customer_1_tbl` MODIFY `gender` int(11) NOT NULL default '2';

use openemm_cms

LOCK TABLES `cm_mailing_bind_tbl_seq` WRITE;
/*!40000 ALTER TABLE `cm_mailing_bind_tbl_seq` DISABLE KEYS */;
INSERT INTO `cm_mailing_bind_tbl_seq` (`value`) VALUES (0);
/*!40000 ALTER TABLE `cm_mailing_bind_tbl_seq` ENABLE KEYS */;
UNLOCK TABLES;

