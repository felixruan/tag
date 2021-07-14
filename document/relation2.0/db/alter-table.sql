use eto_crm_sys;

/**  修改表 */
alter table `sys_brands` drop column destination_database_id;
alter table `sys_brands` add column  `avg_repurchase_cycle` int(11) DEFAULT NULL COMMENT '平均复购周期，单位：天' after `brands_status`;

alter table `sys_brands_org` add column `woaap_org_id` int(11) DEFAULT NULL COMMENT '表woaap_manager_brands woaapOrgId' after `org_name`;

alter table `sys_data_source` add column `org_id` int(11) DEFAULT NULL COMMENT '机构id' after `data_type`;
ALTER TABLE `sys_data_source` CHANGE `data_flag` `data_flag` INT(11) NULL COMMENT '0会员库1粉丝库1';

alter table `sys_model_table` add column `data_flag` int(11) DEFAULT NULL COMMENT '0会员库1粉丝库' after `model_table_name`;

alter table `sys_synchronization_config` add column `origin_table_primary_key` varchar(100) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '源表主键' after `destination_database_id`;
alter table `sys_synchronization_config` add column `app_id` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'appid' after `destination_database_id`;
alter table `sys_synchronization_config` add column `brand_id` int(11) DEFAULT NULL COMMENT '品牌ID' after `destination_database_id`;
alter table `sys_synchronization_config` add column `org_id` int(11) DEFAULT NULL COMMENT '机构ID' after `destination_database_id`;

ALTER TABLE `sys_tag`   CHANGE `master_tag_id` `master_tag_id` INT(11) DEFAULT 0 NULL COMMENT '系统标签id,0-自建';
ALTER TABLE `sys_tag` 	ADD COLUMN `org_id` INT(11) NULL COMMENT '机构id' AFTER `master_tag_id`;
ALTER TABLE `sys_tag` 	ADD COLUMN `brands_id` INT(11) NULL COMMENT '品牌id' AFTER `org_id`;
ALTER TABLE `sys_tag` 	ADD COLUMN `app_id` varchar(255) DEFAULT NULL COMMENT 'appid'  AFTER `tag_name`;
ALTER TABLE `sys_tag` 	ADD COLUMN `tag_type` varchar(1) DEFAULT '0' COMMENT '标签类型，0-会员标签，1-粉丝标签,来自sys_dict：tag_type'  AFTER `tag_memo`;
ALTER TABLE `sys_tag`  	ADD COLUMN `tag_update_frequency_dict_id` INT(11) NULL COMMENT '标签更新频率字典id' AFTER `tag_update_cron`;
ALTER TABLE `sys_tag`  	ADD COLUMN `tag_last_update_date` DATE NULL COMMENT '标签最后一次更新日期' AFTER `tag_update_frequency_dict_id`;
ALTER TABLE `sys_tag`  	ADD COLUMN `tag_next_update_date` DATE NULL COMMENT '标签下次更新日期' AFTER `tag_last_update_date`;
ALTER TABLE `sys_tag` 	ADD COLUMN `covered_people_num` int(11) DEFAULT NULL COMMENT '覆盖人数' after `tag_status`;
ALTER TABLE `sys_tag`   ADD COLUMN `tag_property_change_execute_status` TINYINT(4) DEFAULT 0 NULL COMMENT '属性规则更新执行状态，0-未执行，1-已执行' AFTER `tag_status`;