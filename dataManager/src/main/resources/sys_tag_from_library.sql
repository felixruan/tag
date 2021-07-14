CREATE TABLE `sys_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `master_tag_id` int DEFAULT '0' COMMENT '主库tagId,0-自建',
  `tag_classes_id` int DEFAULT NULL COMMENT '标签分类',
  `tag_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_memo` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_update_type` int DEFAULT NULL COMMENT '标签更新类型，0-定时任务,1-手动',
  `tag_update_cron` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_status` int DEFAULT NULL COMMENT '启用状态 0未启用  1启用',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` int DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签表 ';




CREATE TABLE `sys_tag_group` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_group_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_group_type` int DEFAULT NULL COMMENT '群组类型，0-静态，1-动态',
  `tag_group_start_type` int DEFAULT NULL COMMENT '群组启动类型:0-立即，1-指定日期',
  `tag_group_start_time` datetime DEFAULT NULL COMMENT '群组启动日期',
  `tag_group_memo` varchar(526) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_group_status` int DEFAULT NULL COMMENT '标签群组状态',
  `tag_group_rule_relationship_id` int DEFAULT NULL COMMENT '标签群组规则关系id',
  `tag_group_count_limit_percent` int DEFAULT NULL COMMENT '人群-限制总人数-百分比，和固定人数二选一',
  `tag_group_count_limit_num` int DEFAULT NULL COMMENT '人群-限制总人数-固定人数，和百分比二选一',
  `exclude_user_group_id` varchar(526) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '剔除的群组ids,多个之间逗号分隔',
  `tag_group_split_count` int DEFAULT NULL COMMENT '子群组数量',
  `tag_group_split_rule` json DEFAULT NULL COMMENT '子群组拆分规则',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签群组表 ';




CREATE TABLE `sys_tag_group_rule` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_id` int DEFAULT NULL COMMENT '标签id',
  `tag_group_id` int DEFAULT NULL COMMENT '标签群组ID',
  `tag_group_rule_parent_id` int DEFAULT NULL COMMENT '父id',
  `tag_group_rule_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `tag_group_rule_relationship_id` int DEFAULT NULL COMMENT '群组规则运算关系id',
  `tag_group_rule` json DEFAULT NULL COMMENT '标签群组规则',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签群组规则表 ';



CREATE TABLE `sys_tag_group_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_group_id` int DEFAULT NULL COMMENT '标签群组ID',
  `user_id` int DEFAULT NULL COMMENT '用户id',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `subcontract_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_group_id` (`tag_group_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签群组人群表 ';



CREATE TABLE `sys_tag_property` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_id` int DEFAULT NULL COMMENT '标签ID',
  `property_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `property_memo` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `rule_relationship_id` int DEFAULT NULL COMMENT '规则运算id',
  `process_status` int DEFAULT NULL COMMENT '0未执行1执行中2执行已结束',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签属性表 ';



CREATE TABLE `sys_tag_property_rule` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `property_id` int DEFAULT NULL COMMENT '属性ID',
  `model_table_id` int DEFAULT NULL COMMENT '业务模型表ID',
  `model_table_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `model_table` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `column_relationship_id` int DEFAULT NULL COMMENT '字段运算关系ID',
  `columns` json DEFAULT NULL COMMENT '字段信息',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签属性规则表 ';


CREATE TABLE `sys_tag_property_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `property_id` int DEFAULT NULL COMMENT '标签属性ID',
  `tag_id` int DEFAULT NULL COMMENT '标签ID',
  `user_id` int DEFAULT NULL COMMENT '用户id',
  `revision` int DEFAULT NULL COMMENT '乐观锁',
  `created_by` int DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_by` int DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_delete` tinyint DEFAULT '0' COMMENT '删除标志',
  `delete_by` int DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `tag_id_property_id_idx` (`tag_id`,`property_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统标签属性人群表 ';