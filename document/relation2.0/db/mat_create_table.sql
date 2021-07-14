
-- ----------------------------
-- Table structure for mat_automation_visit_auth_records
-- ----------------------------
DROP TABLE IF EXISTS `mat_automation_visit_auth_records`;
CREATE TABLE `mat_automation_visit_auth_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sys_code` varchar(128) DEFAULT NULL COMMENT '对方系统编号',
  `app_name` varchar(128) DEFAULT NULL COMMENT '访问己方系统名称',
  `api_url` varchar(128) DEFAULT NULL COMMENT '访问己方接口路径',
  `request_ip` varchar(128) DEFAULT NULL COMMENT '对方请求IP',
  `access_key` varchar(1024) DEFAULT NULL COMMENT '公钥',
  `secret_key` varchar(128) DEFAULT NULL COMMENT '私钥',
  `status` int(11) DEFAULT 0 COMMENT '启停标志 0启用、1停用',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=120002 COMMENT='自动化营销访问授权记录表 ';

-- ----------------------------
-- Table structure for mat_event
-- ----------------------------
DROP TABLE IF EXISTS `mat_event`;
CREATE TABLE `mat_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_code` varchar(128) DEFAULT NULL COMMENT '事件名',
  `event_name` varchar(128) DEFAULT NULL COMMENT '事件显示名',
  `burying_point_platform` int(11) DEFAULT NULL COMMENT '应埋点平台 1 iOS、2 Android、3 JavaScript、4 小程序、5 服务端、6 其他',
  `history_thirty_num` int(11) DEFAULT NULL COMMENT '过去30天入库',
  `status` int(11) DEFAULT 0 COMMENT '显示状态 可见或隐藏',
  `is_receive` int(11) DEFAULT 0 COMMENT '是否接收 0 是、1否',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注 帮助业务人员更好理解的信息',
  `touch_opportunity` varchar(32) DEFAULT NULL COMMENT '触发时机 例如：是在点击按钮的时候上报，还是已进入页面后即上报。',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `is_report` int(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002 COMMENT='元数据事件表 ';

-- ----------------------------
-- Table structure for mat_event_modular_relationship
-- ----------------------------
DROP TABLE IF EXISTS `mat_event_modular_relationship`;
CREATE TABLE `mat_event_modular_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `event_code` varchar(128) DEFAULT NULL COMMENT '事件code',
  `mod_code` varchar(128) DEFAULT NULL COMMENT '模块code',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30001 COMMENT='元数据事件模块关系表 ';

-- ----------------------------
-- Table structure for mat_event_property
-- ----------------------------
DROP TABLE IF EXISTS `mat_event_property`;
CREATE TABLE `mat_event_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `property_code` varchar(128) DEFAULT NULL COMMENT '属性名',
  `property_name` varchar(128) DEFAULT NULL COMMENT '属性显示名',
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型 必填单选 1、String   2、NUMBER  3、BOOL   4、DATETIME  5、LIST',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位/格式 统计值的单位，设置后会在分析详情和概览中显示。',
  `value_explain` varchar(255) DEFAULT NULL COMMENT '属性值说明或示例',
  `is_public` int(11) DEFAULT 0 COMMENT '是否为公共属性 0否、1是',
  `is_report` int(11) DEFAULT 0 COMMENT '上报数据 0 无、1 有',
  `status` int(11) DEFAULT 0 COMMENT '显示状态 0可见、1隐藏',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002 COMMENT='元数据事件属性表 ';

-- ----------------------------
-- Table structure for mat_event_property_relationship
-- ----------------------------
DROP TABLE IF EXISTS `mat_event_property_relationship`;
CREATE TABLE `mat_event_property_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_code` varchar(128) DEFAULT NULL COMMENT '事件code',
  `property_code` varchar(128) DEFAULT NULL COMMENT '属性code',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002 COMMENT='元数据事件属性关系表 ';

-- ----------------------------
-- Table structure for mat_event_reporting_data
-- ----------------------------
DROP TABLE IF EXISTS `mat_event_reporting_data`;
CREATE TABLE `mat_event_reporting_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `event_code` varchar(128) DEFAULT NULL COMMENT '事件code',
  `event_property` text DEFAULT NULL COMMENT '事件属性json',
  `user_property` text DEFAULT NULL COMMENT '用户属性json ',
  `mod_code` varchar(128) DEFAULT NULL COMMENT '模块code',
  `mod_id` int(11) DEFAULT NULL COMMENT '模块id',
  `reporting_time` datetime DEFAULT NULL COMMENT '上报时间',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=244476 COMMENT='MAT事件上报表 ';

-- ----------------------------
-- Table structure for mat_map_calculation_data
-- ----------------------------
DROP TABLE IF EXISTS `mat_map_calculation_data`;
CREATE TABLE `mat_map_calculation_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `tag_group_id` int(11) DEFAULT NULL COMMENT '群组id',
  `member_id` int(11) DEFAULT NULL COMMENT '会员表id',
  `union_id` varchar(32) DEFAULT NULL COMMENT 'union_id',
  `open_id` varchar(32) DEFAULT NULL COMMENT 'open_id',
  `mod_code` varchar(32) DEFAULT NULL COMMENT '模块code',
  `work_id` int(11) DEFAULT NULL COMMENT '流程id',
  `mat_work_id` varchar(32) DEFAULT NULL COMMENT 'mat系统流程id',
  `handle_id` int(11) DEFAULT NULL COMMENT 'handle表id',
  `mat_handle_id` int(11) DEFAULT NULL COMMENT 'mat系统handle表id',
  `brand_id` varchar(32) DEFAULT NULL COMMENT '品牌id',
  `original_id` varchar(32) DEFAULT NULL COMMENT '机构id',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `is_hit` tinyint(4) DEFAULT NULL COMMENT '是否命中：0否，1是',
  `member_info` text DEFAULT NULL COMMENT '会员相关信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=1525443 COMMENT='自动化营销计算人群结果数据表 ';


-- ----------------------------
-- Table structure for mat_map_send_records
-- ----------------------------
DROP TABLE IF EXISTS `mat_map_send_records`;
CREATE TABLE `mat_map_send_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mat_works_id` int(11) DEFAULT NULL COMMENT 'mat系统works_id',
  `mat_handle_id` int(11) DEFAULT NULL COMMENT 'mat系统handle_id',
  `mat_action_id` int(11) DEFAULT NULL COMMENT 'mat系统action_id',
  `status` int(11) DEFAULT NULL COMMENT '状态 0:取消 1:等待发送确认 2:等待发送 3:发送中 4:发送暂停 5:发送完成',
  `begin_time` datetime DEFAULT NULL COMMENT '发送开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '发送结束时间',
  `send_status` int(11) DEFAULT NULL COMMENT '发送是否成功 : 1 成功，0 失败',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注说明',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `member_id` int(11) DEFAULT NULL COMMENT '会员id',
  `unionid` varchar(99) DEFAULT NULL COMMENT 'unionId',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30001 COMMENT='自动化营销流程发送记录表 ';





-- ----------------------------
-- Table structure for mat_map_work_handles
-- ----------------------------
DROP TABLE IF EXISTS `mat_map_work_handles`;
CREATE TABLE `mat_map_work_handles` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mat_id` int(11) DEFAULT NULL COMMENT '旧的id',
  `work_id` int(11) DEFAULT NULL COMMENT '流程id',
  `mat_work_id` int(11) DEFAULT NULL COMMENT '旧的流程id',
  `process_type` int(11) DEFAULT NULL COMMENT '流程选择 0:否 1:是',
  `is_open_abtest` int(11) DEFAULT NULL COMMENT '是否开启ABTest 0:关闭 1:开启',
  `is_compare` int(11) DEFAULT NULL COMMENT '是否对照组 0:否 1:是',
  `exec_time` datetime DEFAULT NULL COMMENT 'ABTest剩余处理执行时间',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `percent` decimal(5,2) DEFAULT NULL COMMENT '支线所占百分比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=120001 COMMENT='自动化营销流程人群处理类型表 ';


-- ----------------------------
-- Table structure for mat_map_works
-- ----------------------------
DROP TABLE IF EXISTS `mat_map_works`;
CREATE TABLE `mat_map_works` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mat_id` int(11) DEFAULT NULL COMMENT '旧的id',
  `name` varchar(128) DEFAULT NULL COMMENT '流程名称',
  `type` int(11) DEFAULT NULL COMMENT '营销类型 1:事件营销 2:单次营销 3:周期性营销 4:被动触发营销',
  `status` int(11) DEFAULT NULL COMMENT '状态 0:取消 1:未执行 2:等待执行 3:执行中 4: 已执行',
  `user_group_id` int(11) DEFAULT NULL COMMENT '客户分组id',
  `trigger_condition` text DEFAULT NULL COMMENT '事件营销触发条件',
  `begin_time` datetime DEFAULT NULL COMMENT '任务开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `exec_type` int(11) DEFAULT NULL COMMENT '单次营销执行类型 0:立即执行 1:计划执行',
  `exec_time` datetime DEFAULT NULL COMMENT '单次营销执行时间',
  `exec_config` text DEFAULT NULL COMMENT '周期性执行时间配置',
  `send_limit` int(11) DEFAULT NULL COMMENT '触发次数 0:不限 1:限制',
  `send_limit_config` text DEFAULT NULL COMMENT '触发次数具体配置',
  `handle_type` int(11) DEFAULT NULL COMMENT '人群处理类型 1:不筛选 2:属性筛选',
  `conditions` text DEFAULT NULL COMMENT '属性筛选条件',
  `is_open_abtest` int(11) DEFAULT NULL COMMENT '是否开启ABTest 0:关闭 1:开启',
  `last_exec_time` datetime DEFAULT NULL COMMENT '最近一次执行时间',
  `trigger_count` int(11) DEFAULT NULL COMMENT '触发人数',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注说明',
  `is_compare` int(11) DEFAULT NULL COMMENT '是否对照组 0:否 1:是',
  `percent` decimal(4,2) DEFAULT NULL COMMENT '支线所占百分比',
  `exec_surplus_time` datetime DEFAULT NULL COMMENT 'ABTest剩余处理执行时间',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `handle_id` int(11) DEFAULT NULL COMMENT '分支id，用于标记子流程',
  `org_id` int(11) DEFAULT NULL COMMENT 'mat系统机构id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=189120 COMMENT='自动化营销流程表 ';


-- ----------------------------
-- Table structure for mat_map_works_actions
-- ----------------------------
DROP TABLE IF EXISTS `mat_map_works_actions`;
CREATE TABLE `mat_map_works_actions` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mat_id` int(11) DEFAULT NULL COMMENT '旧的id',
  `works_id` int(11) DEFAULT NULL COMMENT 'works流程表主键id',
  `mat_work_id` int(11) DEFAULT NULL COMMENT '旧的流程id',
  `handle_id` int(11) DEFAULT NULL COMMENT 'handle表主键id',
  `mat_handle_id` int(11) DEFAULT NULL COMMENT '旧的handleid',
  `brand_id` int(11) DEFAULT NULL COMMENT '品牌id',
  `original_id` int(11) DEFAULT NULL COMMENT '机构id',
  `media_id` int(11) DEFAULT NULL COMMENT '微信素材media_id',
  `details` text DEFAULT NULL COMMENT '发送内容详情',
  `send_type` int(11) DEFAULT NULL COMMENT '消息类型 0:不处理 1:分组群发 2:客服消息 3:模板消息 4:小程序订阅消息',
  `tag_type` int(11) DEFAULT NULL COMMENT '标签类型 1:公众号标签 2:CRM标签',
  `appid` varchar(128) DEFAULT NULL COMMENT '公众号或小程序的APPID',
  `delay_hour` int(11) DEFAULT NULL COMMENT '延迟小时',
  `delay_minute` int(11) DEFAULT NULL COMMENT '延迟分钟',
  `delay_second` int(11) DEFAULT NULL COMMENT '延迟秒',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `untitled` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=120001 COMMENT='自动化营销流程每种处理支线要发送的内容表 ';


-- ----------------------------
-- Table structure for mat_modular_info
-- ----------------------------
DROP TABLE IF EXISTS `mat_modular_info`;
CREATE TABLE `mat_modular_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mod_code` varchar(128) DEFAULT NULL COMMENT '模块名 公众号(official)、小程序(app)、电商(online_shop)、CRM(crm)、导购通(shop_guide)',
  `name` varchar(128) DEFAULT NULL COMMENT '模块显示名 公众号、小程序、电商、CRM、导购通',
  `remarks` varchar(128) DEFAULT NULL COMMENT '备注',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002 COMMENT='mat模块信息表 ';


-- ----------------------------
-- Table structure for mat_modular_model_relationship
-- ----------------------------
DROP TABLE IF EXISTS `mat_modular_model_relationship`;
CREATE TABLE `mat_modular_model_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `modular_id` int(11) DEFAULT NULL COMMENT '模块id',
  `model_id` int(11) DEFAULT NULL COMMENT '模型id',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002 COMMENT='模块模型关系表 ';


-- ----------------------------
-- Table structure for mat_origin_terminal
-- ----------------------------
DROP TABLE IF EXISTS `mat_origin_terminal`;
CREATE TABLE `mat_origin_terminal` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `terminal_code` varchar(99) DEFAULT NULL COMMENT '终端名',
  `terminal_name` varchar(99) DEFAULT NULL COMMENT '终端显示名',
  `terminal_identification` varchar(99) DEFAULT NULL COMMENT '设备标识',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注 帮助业务人员更好理解的信息',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='埋点数据来源终端表 ';


-- ----------------------------
-- Table structure for mat_user_property
-- ----------------------------
DROP TABLE IF EXISTS `mat_user_property`;
CREATE TABLE `mat_user_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `property_code` varchar(128) DEFAULT NULL COMMENT '属性名',
  `property_name` varchar(128) DEFAULT NULL COMMENT '属性显示名',
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型 必填单选 1、String   2、NUMBER  3、BOOL   4、DATETIME  5、LIST',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位/格式 统计值的单位，设置后会在分析详情和概览中显示。',
  `value_explain` varchar(255) DEFAULT NULL COMMENT '属性值说明或示例',
  `status` int(11) DEFAULT 0 COMMENT '显示状态 0可见、1隐藏',
  `is_report` int(11) DEFAULT 0 COMMENT '上报数据 0 无、1 有',
  `REVISION` int(11) DEFAULT NULL COMMENT '乐观锁',
  `CREATED_BY` varchar(32) DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_BY` varchar(32) DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_delete` int(11) DEFAULT 0 COMMENT '删除标志',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30002 COMMENT='元数据用户属性表 ';


