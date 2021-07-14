/**
新增表  23个
`gateway_define`
`sys_column`
`sys_etl_processing_rule`
`sys_life_cycle_model`
`sys_life_cycle_model_rule`
`sys_life_cycle_model_user`
`sys_tag_group`
`sys_tag_group_rule`
`sys_tag_group_user`
`sys_tag_property_user`
`sys_tag_property_wechat_user`
`sys_woaap_brands`
`members`
`t_coupons`
`t_leaguer_levels`
`t_members_coupons`
`t_orders`
`t_regions`
`t_shops`
`wechat_user`
`woaap_manage_brands`
`woaap_sys_organizations`
`woaap_brands`
mat_automation_visit_auth_records
*/

/*Table structure for table `gateway_define` */

use eto_crm_sys;

DROP TABLE IF EXISTS `gateway_define`;

CREATE TABLE `gateway_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `predicates` varchar(255) DEFAULT NULL,
  `filters` varchar(255) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT NULL COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ;

/*Table structure for table `sys_column` */

DROP TABLE IF EXISTS `sys_column`;

CREATE TABLE `sys_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `classes_id` int(11) DEFAULT NULL COMMENT '字段分类ID',
  `column_code` varchar(64) DEFAULT NULL COMMENT '字段编码',
  `column_value` varchar(1024) DEFAULT NULL COMMENT '字段value',
  `column_name` varchar(64) DEFAULT NULL COMMENT '字段名称',
  `column_memo` varchar(64) DEFAULT NULL COMMENT '字段描述',
  `column_status` int(11) DEFAULT 0 COMMENT '字段状态',
  `revision` int(11) DEFAULT 0 COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` varchar(1) DEFAULT '0' COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='系统字段表 ';

/*Table structure for table `sys_etl_processing_rule` */

DROP TABLE IF EXISTS `sys_etl_processing_rule`;

CREATE TABLE `sys_etl_processing_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '规则名称',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态，1-启用，0-停用',
  `param_table` varchar(128) DEFAULT NULL COMMENT '入参模型表名称',
  `param_column` varchar(128) DEFAULT NULL COMMENT '入参模型字段',
  `processing_sql` varchar(2048) DEFAULT NULL COMMENT '加工sql',
  `target_table` varchar(128) DEFAULT NULL COMMENT '目标模型表',
  `target_columns` varchar(526) DEFAULT NULL COMMENT '目标模型字段，多个逗号分隔',
  `target_where_column` varchar(128) DEFAULT NULL COMMENT '更新目标表的where 字段',
  `target_where_column_value_source` varchar(128) DEFAULT NULL COMMENT '目标字段值，来源于param_column',
  `update_size` int(10) NOT NULL DEFAULT 1 COMMENT '每次更新大小，根据更新影响行数设置',
  `query_size` int(10) NOT NULL DEFAULT 1 COMMENT '每次查询大小',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='系统etl 加工规则';

/*Table structure for table `sys_life_cycle_model` */

DROP TABLE IF EXISTS `sys_life_cycle_model`;

CREATE TABLE `sys_life_cycle_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(256) DEFAULT NULL COMMENT '模型名称',
  `org_id` bigint(20) DEFAULT NULL COMMENT '机构id',
  `brands_id` bigint(20) DEFAULT NULL COMMENT '品牌id',
  `update_type` int(11) DEFAULT NULL COMMENT '更新频率，0-不更新，1-每天一次，2-每周一次，3-每月一次',
  `update_value` int(11) DEFAULT NULL COMMENT '每周几/每月哪天',
  `data_update_time` datetime DEFAULT NULL COMMENT '数据更新时间',
  `data_next_update_date` date DEFAULT NULL COMMENT '数据下次更新时间',
  `data_update_status` int(11) DEFAULT NULL COMMENT '更新状态,0-未执行，1-执行中，2-执行结束',
  `rule_update_time` datetime DEFAULT NULL COMMENT '规则修改时间',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='生命周期模型表';

/*Table structure for table `sys_life_cycle_model_rule` */

DROP TABLE IF EXISTS `sys_life_cycle_model_rule`;

CREATE TABLE `sys_life_cycle_model_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `model_id` bigint(20) DEFAULT NULL COMMENT '生命周期模型id',
  `step_name` varchar(128) DEFAULT NULL COMMENT '阶段名称',
  `step_code` varchar(64) DEFAULT NULL COMMENT '阶段code',
  `step_order` int(11) DEFAULT NULL COMMENT '阶段顺序,越小越靠前',
  `step_memo` varchar(526) DEFAULT NULL COMMENT '阶段规则描述',
  `step_rule_value` json DEFAULT NULL COMMENT '阶段规则',
  `covered_count` int(11) DEFAULT 0 COMMENT '覆盖人群',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='生命周期模型规则表';

/*Table structure for table `sys_life_cycle_model_user` */

DROP TABLE IF EXISTS `sys_life_cycle_model_user`;

CREATE TABLE `sys_life_cycle_model_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `model_id` int(11) DEFAULT NULL COMMENT '生命周期模型id',
  `model_rule_id` int(11) DEFAULT NULL COMMENT '生命周期模型规则id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `model_rule_id_idx` (`model_rule_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci  COMMENT='生命周期模型人群';

/*Table structure for table `sys_tag_group` */

DROP TABLE IF EXISTS `sys_tag_group`;

CREATE TABLE `sys_tag_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_id` int(11) DEFAULT NULL COMMENT '机构id',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `tag_group_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_group_type` int(11) DEFAULT NULL COMMENT '群组类型，0-静态，1-动态',
  `tag_group_start_type` int(11) DEFAULT NULL COMMENT '群组启动类型:0-立即，1-指定日期',
  `tag_group_start_time` datetime DEFAULT NULL COMMENT '群组启动日期',
  `tag_group_rest_date` datetime DEFAULT NULL COMMENT '静止日期',
  `tag_group_memo` varchar(526) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_group_status` int(11) DEFAULT NULL COMMENT '标签群组状态',
  `tag_group_rule_change_execute_status` tinyint(4) DEFAULT 0 COMMENT '群组规则更新执行状态，0-未执行，1-已执行',
  `tag_group_rule_relationship_id` int(11) DEFAULT NULL COMMENT '标签群组规则关系id',
  `tag_group_count_limit_percent` int(11) DEFAULT NULL COMMENT '人群-限制总人数-百分比，和固定人数二选一',
  `tag_group_count_limit_num` int(11) DEFAULT NULL COMMENT '人群-限制总人数-固定人数，和百分比二选一',
  `exclude_user_group_id` varchar(526) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '剔除的群组ids,多个之间逗号分隔',
  `tag_group_split_count` int(11) DEFAULT NULL COMMENT '子群组数量',
  `tag_group_split_rule` json DEFAULT NULL COMMENT '子群组拆分规则',
  `count_user_info` json DEFAULT NULL COMMENT '群组覆盖人数信息{"countUser":"计算中","countMobileId":"1","countMemberId":"1","countUnionID":"1"}',
  `son_count_info` json DEFAULT NULL COMMENT '子群组覆盖人数信息[{"name":"A","count":"10"}]',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci  COMMENT='系统标签群组表 ';

/*Table structure for table `sys_tag_group_rule` */

DROP TABLE IF EXISTS `sys_tag_group_rule`;

CREATE TABLE `sys_tag_group_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签id',
  `property_ids` varchar(526) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '属性ids,逗号分隔',
  `tag_group_id` int(11) DEFAULT NULL COMMENT '标签群组ID',
  `tag_group_rule_parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `tag_group_rule_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_group_rule_relationship_id` int(11) DEFAULT NULL COMMENT '群组规则运算关系id',
  `tag_group_rule` json DEFAULT NULL COMMENT '标签群组规则',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci  COMMENT='系统标签群组规则表 ';

/*Table structure for table `sys_tag_group_user` */

DROP TABLE IF EXISTS `sys_tag_group_user`;

CREATE TABLE `sys_tag_group_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_group_id` int(11) DEFAULT NULL COMMENT '标签群组ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `subcontract_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_group_id` (`tag_group_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签群组人群表 ';

/*Table structure for table `sys_tag_property_user` */

DROP TABLE IF EXISTS `sys_tag_property_user`;

CREATE TABLE `sys_tag_property_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `property_id` int(11) DEFAULT NULL COMMENT '标签属性ID',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `tag_id` (`tag_id`),
  KEY `property_id` (`property_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci  COMMENT='系统标签属性人群表 ';

/*Table structure for table `sys_tag_property_wechat_user` */

DROP TABLE IF EXISTS `sys_tag_property_wechat_user`;

CREATE TABLE `sys_tag_property_wechat_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `property_id` int(11) DEFAULT NULL COMMENT '标签属性ID',
  `tag_id` int(11) DEFAULT NULL COMMENT '标签ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `open_id` varchar(200) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'openid',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint(4) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `property_id` (`property_id`),
  KEY `open_id` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci  COMMENT='系统标签属性粉丝人群表 ';

/*Table structure for table `sys_woaap_brands` */

DROP TABLE IF EXISTS `sys_woaap_brands`;
CREATE TABLE `sys_woaap_brands` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `app_id` varchar(50) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表woaap_manager_brands中 appid',
  `woaap_id` int(11) DEFAULT NULL COMMENT '表woaap_manager_brands中woaap_id',
  `is_delete` int(11) DEFAULT 0,
  `revision` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci  COMMENT='品牌关系表';

/*Table structure for table `members` */

DROP TABLE IF EXISTS `members`;

CREATE TABLE `members` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `member_id` varchar(200) DEFAULT NULL,
  `number` varchar(200) DEFAULT NULL COMMENT '会员编号',
  `name` varchar(255) DEFAULT NULL COMMENT '会员姓名',
  `gender` varchar(200) DEFAULT NULL COMMENT '会员性别 0:未知 1:男 2:女',
  `vip_level_id` varchar(200) DEFAULT NULL COMMENT '会员等级id',
  `integral` varchar(200) DEFAULT NULL COMMENT '积分',
  `registered_time` varchar(200) DEFAULT NULL COMMENT '注册时间',
  `mobile` varchar(200) DEFAULT NULL COMMENT '会员手机号',
  `registered_channel` varchar(200) DEFAULT NULL COMMENT '注册渠道',
  `vip_level` varchar(255) DEFAULT NULL COMMENT '会员等级名称',
  `country` varchar(255) DEFAULT NULL COMMENT '会员所在的国家',
  `province` varchar(255) DEFAULT NULL COMMENT '会员所在省份',
  `city` varchar(255) DEFAULT NULL COMMENT '会员所在城市',
  `area` varchar(255) DEFAULT NULL COMMENT '会员所在区域',
  `storeno` varchar(255) DEFAULT NULL COMMENT '注册门店编号',
  `unionid` varchar(255) DEFAULT NULL,
  `openid` varchar(255) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `email_available` varchar(200) DEFAULT NULL COMMENT '邮箱可用性',
  `birthday` date DEFAULT NULL COMMENT '会员生日',
  `age` varchar(200) DEFAULT NULL COMMENT '年龄',
  `birth_month` varchar(200) DEFAULT NULL COMMENT '生日月',
  `birth_day` varchar(200) DEFAULT NULL COMMENT '生日天',
  `mobile_available` varchar(200) DEFAULT NULL COMMENT '手机号有效否',
  `store_type` varchar(200) DEFAULT NULL COMMENT '注册门店类型',
  `store_province` varchar(200) DEFAULT NULL COMMENT '注册门店省',
  `store_city` varchar(200) DEFAULT NULL COMMENT '注册门店市',
  `store_district` varchar(200) DEFAULT NULL COMMENT '注册门店-区',
  `first_consume_time` datetime DEFAULT NULL COMMENT '首次消费时间',
  `last_consume_time` varchar(200) DEFAULT NULL COMMENT '最后一次消费时间',
  `consume_amount` varchar(200) DEFAULT NULL COMMENT '消费总金额',
  `order_goods_num` varchar(200) DEFAULT NULL COMMENT '购买件数',
  `order_count` varchar(200) DEFAULT NULL COMMENT '订单数',
  `recent_year_consume_amount` decimal(20,2) DEFAULT NULL COMMENT '近一年消费总金额',
  `recent_year_order_count` varchar(200) DEFAULT NULL COMMENT '一年内订单数',
  `unit_price` varchar(200) DEFAULT NULL COMMENT '件单价=消费金额/购买件数',
  `customer_order_goods_num` varchar(200) DEFAULT NULL COMMENT '客单件:购买件数/订单数',
  `customer_order_price` varchar(200) DEFAULT NULL COMMENT '客单价:消费金额/订单数',
  `pay_amount` varchar(200) DEFAULT NULL COMMENT '支付金额',
  `recent_year_pay_amount` varchar(200) DEFAULT NULL COMMENT '近一年支付金额',
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  KEY `unionid` (`unionid`),
  KEY `mobile` (`mobile`),
  KEY `number` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='会员模型 ' ;

/*Table structure for table `t_coupons` */

DROP TABLE IF EXISTS `t_coupons`;

CREATE TABLE `t_coupons` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `title` varchar(200) DEFAULT NULL COMMENT '优惠券名称',
  `coupon_id` varchar(200) DEFAULT NULL,
  `least_cost` varchar(200) DEFAULT NULL COMMENT '起用金额 满减券折扣券满折券礼品券专属',
  `reduce_cost` varchar(200) DEFAULT NULL COMMENT '代金券减免金额',
  `discount` varchar(200) DEFAULT NULL COMMENT '折扣额度1.7.8专属',
  `used_type` varchar(200) DEFAULT NULL COMMENT '使用渠道 1.官网 2.APP 3.H5 4.微信 5.etoshop 6.线下渠道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='优惠券模型';

/*Table structure for table `t_leaguer_levels` */

DROP TABLE IF EXISTS `t_leaguer_levels`;

CREATE TABLE `t_leaguer_levels` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `level_id` varchar(200) DEFAULT NULL,
  `level_name` varchar(200) DEFAULT NULL COMMENT '等级名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='会员等级模型' ;

/*Table structure for table `t_members_coupons` */

DROP TABLE IF EXISTS `t_members_coupons`;

CREATE TABLE `t_members_coupons` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `member_id` varchar(200) DEFAULT NULL COMMENT 'members表主键ID',
  `coupon_id` varchar(200) DEFAULT NULL COMMENT 'coupons表主键ID',
  `coupon_type` varchar(200) DEFAULT NULL COMMENT '优惠券类型 1.折扣券 2.代金券',
  `type` varchar(200) DEFAULT NULL COMMENT '来源类型 1.兑换 2.注册赠送 3.会员升级 4.生日赠送 5.定向投放 6.活动送券',
  `start_time` varchar(200) DEFAULT NULL COMMENT '开始有效时间',
  `end_time` varchar(200) DEFAULT NULL COMMENT '失效时间',
  `coupon_use_channel` varchar(200) DEFAULT NULL COMMENT '使用渠道 1.官网 2.APP 3.H5 4.微信 5.etoshop 6.线下渠道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='会员优惠券模型';

/*Table structure for table `t_orders` */

DROP TABLE IF EXISTS `t_orders`;

CREATE TABLE `t_orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `order_id` varchar(200) DEFAULT NULL,
  `member_id` varchar(200) DEFAULT NULL COMMENT 'members表主键ID',
  `source` varchar(200) DEFAULT NULL COMMENT '订单来源 1.微信 2.支付宝 3.天猫 4.淘宝 5.ERP 6.官网 7 pos 8 商城',
  `pay_amount` varchar(200) DEFAULT NULL COMMENT '支付金额',
  `pay_info` varchar(200) DEFAULT NULL COMMENT '付款详细信息  pay_way1现金 2微信 3支付宝 4银联 pay_price 对应支付金额',
  `orderno` varchar(200) DEFAULT NULL COMMENT '订单编号',
  `storeno` varchar(200) DEFAULT NULL COMMENT '消费门店编号',
  `pay_time` varchar(200) DEFAULT NULL COMMENT '支付时间',
  `order_type` int(11) DEFAULT NULL COMMENT '订单类型 1 订单 2 退单 ',
  `store_type` varchar(200) DEFAULT NULL COMMENT '门店类型',
  `store_province` varchar(200) DEFAULT NULL COMMENT '门店-省份',
  `store_city` varchar(200) DEFAULT NULL COMMENT '门店-市',
  `store_district` varchar(200) DEFAULT NULL COMMENT '门店-区',
  `goods_number` varchar(200) DEFAULT NULL COMMENT '商品数量',
  `order_time` varchar(200) DEFAULT NULL COMMENT '下单时间',
  `order_amount` varchar(200) DEFAULT NULL COMMENT '订单金额',
  `aaaa` int(11) DEFAULT NULL COMMENT 'aaaa',
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  KEY `orderno` (`orderno`),
  KEY `storeno` (`storeno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单模型';

/*Table structure for table `t_regions` */

DROP TABLE IF EXISTS `t_regions`;

CREATE TABLE `t_regions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `gid` varchar(200) DEFAULT NULL COMMENT '区域ID',
  `pid` varchar(200) DEFAULT NULL COMMENT '上级ID',
  `prov_id` varchar(200) DEFAULT NULL COMMENT '对应的省份ID',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `fullname` varchar(200) DEFAULT NULL COMMENT '全称',
  `lat` varchar(200) DEFAULT NULL COMMENT '纬度',
  `lng` varchar(200) DEFAULT NULL COMMENT '经度',
  `region_id` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='省市区模型';

/*Table structure for table `t_shops` */

DROP TABLE IF EXISTS `t_shops`;

CREATE TABLE `t_shops` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `shop_id` varchar(200) DEFAULT NULL,
  `brand_id` varchar(200) DEFAULT NULL COMMENT '品牌id',
  `status` varchar(200) DEFAULT NULL COMMENT '门店状态 1-开店中 2-已关店',
  `name` varchar(200) DEFAULT NULL COMMENT '门店名称',
  `province` varchar(200) DEFAULT NULL COMMENT '省ID',
  `city` varchar(200) DEFAULT NULL COMMENT '市ID',
  `district` varchar(200) DEFAULT NULL COMMENT '区ID',
  `brand_name` varchar(200) DEFAULT NULL COMMENT '品牌名称',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `shop_code` varchar(200) DEFAULT NULL COMMENT '商家编码',
  `type` varchar(200) DEFAULT NULL COMMENT '门店类型 1-实体门店 2-虚拟门店',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='门店模型';

/*Table structure for table `wechat_user` */

DROP TABLE IF EXISTS `wechat_user`;

CREATE TABLE `wechat_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `wechat_id` int(11) DEFAULT NULL COMMENT '粉丝id',
  `wechat_unionid` varchar(64) DEFAULT NULL COMMENT '粉丝unionid',
  `wechat_openid` varchar(64) DEFAULT NULL COMMENT '粉丝openid',
  `wechat_appid` varchar(64) DEFAULT NULL COMMENT '微信appid',
  `wechat_sex` int(11) DEFAULT NULL COMMENT '{ 1:男 ,2:女 ,0:未知 }',
  `wechat_nickname` varchar(255) DEFAULT NULL COMMENT '粉丝用户昵称',
  `wechat_subscribe` int(11) DEFAULT NULL COMMENT '粉丝取关',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `wechat_country` varchar(255) DEFAULT NULL COMMENT '国家',
  `wechat_province` varchar(255) DEFAULT NULL COMMENT '省份',
  `wechat_city` varchar(255) DEFAULT NULL COMMENT '城市',
  PRIMARY KEY (`id`),
  KEY `wechat_appid` (`wechat_appid`),
  KEY `wechat_id` (`wechat_id`),
  KEY `wechat_unionid` (`wechat_unionid`),
  KEY `wechat_openid` (`wechat_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='粉丝表';

/*Table structure for table `woaap_manage_brands` */

DROP TABLE IF EXISTS `woaap_manage_brands`;

CREATE TABLE `woaap_manage_brands` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `woaap_id` varchar(200) DEFAULT NULL COMMENT '主键',
  `organization_id` varchar(200) DEFAULT NULL COMMENT '机构',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `wechat_appid` varchar(200) DEFAULT NULL COMMENT '微信appid',
  `alipay_appid` varchar(200) DEFAULT NULL COMMENT '支付宝appid',
  `status` varchar(200) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='woaap 小程序公众号管理表';

/*Table structure for table `woaap_sys_organizations` */

DROP TABLE IF EXISTS `woaap_sys_organizations`;

CREATE TABLE `woaap_sys_organizations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `woaap_id` varchar(200) DEFAULT NULL COMMENT '主键',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `industry` varchar(200) DEFAULT NULL COMMENT '所属行业',
  `is_auth` varchar(200) DEFAULT NULL COMMENT '是否认证',
  `status` varchar(200) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='woaap机构表';

/* Table structure for table `woaap_brands`  */

-- DROP TABLE IF EXISTS `woaap_brands`;

CREATE TABLE `woaap_brands` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brands_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `org_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `woaap_id` varchar(200) DEFAULT NULL COMMENT '主键',
  `status` varchar(200) DEFAULT NULL COMMENT '状态',
  `organization_id` varchar(200) DEFAULT NULL COMMENT '机构',
  `appid` varchar(200) DEFAULT NULL COMMENT 'appid',
  `appkey` varchar(200) DEFAULT NULL COMMENT 'appkey',
  `type` varchar(200) DEFAULT NULL COMMENT '类型''service'',''subscribe'',''miniapp''',
  `wxid` varchar(200) DEFAULT NULL COMMENT '微信号',
  `wechat_name` varchar(200) DEFAULT NULL COMMENT '微信名称',
  `description` varchar(200) DEFAULT NULL COMMENT '公众号描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='优惠券模型';

CREATE TABLE `youngor_member_preference_tag_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` varchar(100) DEFAULT NULL COMMENT '会员id',
  `phone` varchar(100) DEFAULT NULL COMMENT '会员电话',
  `register_time` varchar(100) DEFAULT NULL COMMENT '注册时间',
  `cardno` varchar(100) DEFAULT NULL COMMENT '会员卡号',
  `realname` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `sex` varchar(100) DEFAULT NULL COMMENT '性别',
  `age` varchar(100) DEFAULT NULL COMMENT '年龄段',
  `birth_date` varchar(100) DEFAULT NULL COMMENT '出生日期',
  `birth_month` varchar(100) DEFAULT NULL COMMENT '出生月份',
  `chinese_zodiac` varchar(100) DEFAULT NULL COMMENT '生肖',
  `constellation` varchar(100) DEFAULT NULL COMMENT '星座',
  `province_city` varchar(100) DEFAULT NULL COMMENT '所属省份城市',
  `store_code` varchar(100) DEFAULT NULL COMMENT '所属门店编码',
  `store_name` varchar(100) DEFAULT NULL COMMENT '所属门店名称',
  `pianqu_code` varchar(100) DEFAULT NULL COMMENT '所属雅戈尔片区编码',
  `pianqu_name` varchar(100) DEFAULT NULL COMMENT '所属雅戈尔片区名称',
  `area_code` varchar(100) DEFAULT NULL COMMENT '所属雅戈尔区域编码',
  `area_name` varchar(100) DEFAULT NULL COMMENT '所属雅戈尔区域名称',
  `yxgs_code` varchar(100) DEFAULT NULL COMMENT '所属雅戈尔营销公司编码',
  `yxgs_name` varchar(100) DEFAULT NULL COMMENT '所属雅戈尔营销公司名称',
  `initiation_time` varchar(100) DEFAULT NULL COMMENT '入会时长年',
  `member_level` varchar(100) DEFAULT NULL COMMENT '会员等级',
  `member_life_cycle` varchar(100) DEFAULT NULL COMMENT '会员生命周期',
  `current_point` varchar(100) DEFAULT NULL COMMENT '当前可用积分',
  `consum_sum` varchar(100) DEFAULT NULL COMMENT '累计消费区间',
  `consumption_num_ytd` varchar(100) DEFAULT NULL COMMENT '当年购买次数',
  `consumption_num` varchar(100) DEFAULT NULL COMMENT '累计购买次数',
  `consumption_last_month` varchar(100) DEFAULT NULL COMMENT '上月有无消费',
  `consumption_last_quarter` varchar(100) DEFAULT NULL COMMENT '上季度有无消费',
  `consumption_first_half_year` varchar(100) DEFAULT NULL COMMENT '上一个半年有无消费',
  `consumption_last_year` varchar(100) DEFAULT NULL COMMENT '去年有无消费',
  `consum_brand` varchar(100) DEFAULT NULL COMMENT '购买过的品牌',
  `brand_prefer_name` varchar(100) DEFAULT NULL COMMENT '品牌偏好',
  `dalei_prefer_name` varchar(100) DEFAULT NULL COMMENT '大类偏好',
  `prod_style_prefer_name` varchar(100) DEFAULT NULL COMMENT '风格偏好',
  `season_prefer_name` varchar(100) DEFAULT NULL COMMENT '季节偏好',
  `color_prefer_name` varchar(100) DEFAULT NULL COMMENT '颜色偏好',
  `series_prefer_name` varchar(100) DEFAULT NULL COMMENT '系列偏好',
  `banxing_prefer_name` varchar(100) DEFAULT NULL COMMENT '版型偏好',
  `consum_range_prefer` varchar(100) DEFAULT NULL COMMENT '消费区间偏好',
  `discount_range_prefer` varchar(100) DEFAULT NULL COMMENT '折扣区间偏好',
  `consum_day_class_prefer_lm` varchar(100) DEFAULT NULL COMMENT '近30天消费日期偏好',
  `consum_day_class_prefer_lq` varchar(100) DEFAULT NULL COMMENT '近90天消费日期偏好',
  `consumtime_range_prefer` varchar(100) DEFAULT NULL COMMENT '消费时间段区间偏好',
  `consum_channel_prefer_name` varchar(100) DEFAULT NULL COMMENT '消费渠道偏好',
  `pay_mode_prefer_name_offline` varchar(100) DEFAULT NULL COMMENT '线下支付方式偏好',
  `revision` int(11) DEFAULT NULL COMMENT '乐观锁',
  `created_by` int(11) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` int(11) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_by` int(11) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_cardno` (`cardno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=57489 COMMENT='雅戈尔会员标签表';

CREATE TABLE `mat_automation_visit_auth_records` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sys_code` VARCHAR(128) DEFAULT NULL COMMENT '对方系统编号',
  `app_name` VARCHAR(128) DEFAULT NULL COMMENT '访问己方系统名称',
  `api_url` VARCHAR(128) DEFAULT NULL COMMENT '访问己方接口路径',
  `request_ip` VARCHAR(128) DEFAULT NULL COMMENT '对方请求IP',
  `access_key` VARCHAR(1024) DEFAULT NULL COMMENT '公钥',
  `secret_key` VARCHAR(128) DEFAULT NULL COMMENT '私钥',
  `status` INT(11) DEFAULT 0 COMMENT '启停标志 0启用、1停用',
  `REVISION` INT(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` VARCHAR(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` VARCHAR(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` DATETIME DEFAULT NULL COMMENT '更新时间',
  `delete_by` VARCHAR(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` INT(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='自动化营销&外部 访问授权记录表 ';