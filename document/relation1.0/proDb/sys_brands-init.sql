/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.21-commercial 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

INSERT INTO `sys_brands_org` (`id`, `org_code`, `org_name`, `org_name_en`, `org_full_name`, `org_status`, `revision`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_delete`, `delete_by`, `delete_time`) VALUES('1',NULL,'系统组织',NULL,'组织全称','1',NULL,NULL,NOW(),NULL,NOW(),'0',NULL,NULL);

insert into `sys_brands` (`id`, `org_id`, `destination_database_id`, `brands_code`, `brands_name`, `brands_name_en`, `brands_full_name`, `brands_memo`, `brands_logo_url`, `brands_status`, `revision`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_delete`, `delete_by`, `delete_time`) values('1','1',NULL,NULL,'系统品牌','system brands','品牌全称','品牌描述',NULL,'1',NULL,NULL,NOW(),'15',NOW(),'0',NULL,NULL);
