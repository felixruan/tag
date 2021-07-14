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
  `birthday` varchar(200) DEFAULT NULL COMMENT '会员生日',
  `vip_level_id` varchar(200) DEFAULT NULL COMMENT '会员等级id',
  `integral` varchar(200) DEFAULT NULL COMMENT '积分',
  `registered_time` varchar(200) NULL DEFAULT NULL COMMENT '注册时间',
  `mobile` varchar(200) DEFAULT NULL COMMENT '会员手机号',
  `registered_channel` varchar(200) DEFAULT NULL COMMENT '注册渠道',
  `vip_level` varchar(255) DEFAULT NULL COMMENT '会员等级名称',
  `country` varchar(255) DEFAULT NULL COMMENT '会员所在的国家',
  `province` varchar(255) DEFAULT NULL COMMENT '会员所在省份',
  `city` varchar(255) DEFAULT NULL COMMENT '会员所在城市',
  `area` varchar(255) DEFAULT NULL COMMENT '会员所在区域',
  `storeno` varchar(255) DEFAULT NULL COMMENT '注册门店编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



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
  `start_time` varchar(200) NULL DEFAULT NULL COMMENT '开始有效时间',
  `end_time` varchar(200) NULL DEFAULT NULL COMMENT '失效时间',
  `coupon_use_channel` varchar(200) DEFAULT NULL COMMENT '使用渠道 1.官网 2.APP 3.H5 4.微信 5.etoshop 6.线下渠道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;