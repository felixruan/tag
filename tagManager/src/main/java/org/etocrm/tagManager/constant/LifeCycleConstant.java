package org.etocrm.tagManager.constant;

public class LifeCycleConstant {

    //生命周期模型 标签code
    public static final String TAG_CODE = "life_cycle";

    /**
     * 生命周期更新频率
     */
    // 不更新
    public static final int LIFE_CYCLE_UPDATE_TYPE_ONCE = 0;
    //每天一次
    public static final int LIFE_CYCLE_UPDATE_TYPE_DAILY = 1;
    //每周一次
    public static final int LIFE_CYCLE_UPDATE_TYPE_WEEKLY = 2;
    //每月一次
    public static final int LIFE_CYCLE_UPDATE_TYPE_MONTHLY = 3;


    public static final Integer[] WEEK = {1, 2, 3, 4, 5, 6, 7};

    public static final Integer[] MONTH = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};

    public static final String[] MODEL_RULE_CODE = new String[]{"register", "pureNew", "preNew", "growth", "maturity", "decline", "lose"};

    public static final String[] STEP_RULE_MEMO = {
            "#{registerDay}天内注册，且订单数=0",
            "#{orderDay}天内总订单数=1",
            "#{orderDay}天内订单数=1，总订单数>1",
            "#{orderDay1}天内订单数#{logicName}#{startValue}~#{endValue}，且#{orderDay2}天内订单数>0",
            "#{orderDay1}天内订单数#{logicName}#{startValue}，且#{orderDay2}天内订单数>0",
            "#{orderDay}天内订单数=0，且历史总订单数>0",
            "#{orderDay}天内订单数=0"
    };


    public static final String[] STEP_RULE_WHERE_SQL = {

            /**
             * 1. {180}天内注册，且订单数=0
             * select id from members where org_id=1 and brands_id=2 and registered_time>='2020-05-29'
             * and not exists (select 1 from t_orders where org_id=1 AND brands_id=2 AND order_type=1 and member_id = members.member_id)
            */
            // 记得加 order_type=1
            "org_id = #{orgId} and brands_id=#{brandsId} and  registered_time>= '#{registeredTime}' " +
                    "AND NOT EXISTS ( SELECT 1 FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 and member_id=members.member_id) ",

            /**
             * 2. {180}天内总订单数=1   (历史总订单数=1)
            select id from members where org_id=1 and brands_id=2 and exists (select 1 from (
            select member_id from t_orders where org_id=1 and brands_id=2  and order_type=1 group by member_id having (count(distinct orderno) =1 AND MIN(pay_time)>='2020-05-29' )
            ) t1 where t1.member_id=members.member_id)
            */
            "org_id = #{orgId} and brands_id=#{brandsId} and exists (select 1 from ( " +
                    "select member_id from t_orders where org_id = #{orgId} and brands_id=#{brandsId}  and order_type=1 " +
                    "group by member_id having (count(distinct orderno) =1 AND MIN(pay_time)>='#{payTime}' )  " +
                    "  ) t1 where t1.member_id=members.member_id)",

            /**
             * 3. {180}天内总订单数=1，历史订单数>1
             * SELECT COUNT(1) FROM members WHERE
             *  org_id=1 AND brands_id=2 AND EXISTS (SELECT 1 FROM (
             * 		SELECT member_id FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 AND EXISTS ( SELECT 1 FROM (
             * 			SELECT member_id FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 AND  pay_time>='2020-05-29'  GROUP BY member_id HAVING (COUNT(DISTINCT orderno) =1)
             * 		) t1 WHERE t1.member_id=t_orders.member_id )
             * 	GROUP BY member_id HAVING(COUNT(DISTINCT orderno)>1)) t1 WHERE t1.member_id=members.member_id) ;
            */
            " org_id = #{orgId} and brands_id=#{brandsId} AND EXISTS (SELECT 1 FROM ( " +
                    "SELECT member_id FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 AND EXISTS ( SELECT 1 FROM (  " +
                    "SELECT member_id FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 AND  pay_time>='#{payTime}'  GROUP BY member_id HAVING (COUNT(DISTINCT orderno) =1) " +
                    ") t1 WHERE t1.member_id=t_orders.member_id ) " +
                    "GROUP BY member_id HAVING(COUNT(DISTINCT orderno)>1)) t1 WHERE t1.member_id=members.member_id) ",

            /**
             * 4.{365}天内订单数介于{2}-{3}，且{180}天内订单数>0  (查的时候 180 要>1,不然会和3 有用户重合)
             * SELECT id FROM members WHERE
             *  org_id=1 AND brands_id=2 AND EXISTS (SELECT 1 FROM (
             * 		-- 365天内订单数介于2-3
             * 		SELECT member_id FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 and pay_time>='2019-11-26' AND EXISTS ( SELECT 1 FROM (
             * 		-- 180天订单>1
             * 			SELECT member_id FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 AND  pay_time>='2020-05-29'
             * 			GROUP BY member_id HAVING (COUNT(DISTINCT orderno) >1)
             * 		) t1 WHERE t1.member_id=t_orders.member_id )
             * 		GROUP BY member_id HAVING(COUNT(DISTINCT orderno)>=2 and COUNT(DISTINCT orderno)<=3 )
             * ) t1 WHERE t1.member_id=members.member_id) ;
            */
            " org_id = #{orgId} and brands_id=#{brandsId} AND EXISTS (SELECT 1 FROM (\n" +
                    "  SELECT member_id FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 and pay_time>='#{payTime1}' AND EXISTS ( SELECT 1 FROM ( \n" +
                    "    SELECT member_id FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1  AND  pay_time>='#{payTime2}'  \n" +
                    "    GROUP BY member_id HAVING (COUNT(DISTINCT orderno) >1)\n" +
                    "  ) t1 WHERE t1.member_id=t_orders.member_id )\n" +
                    "  GROUP BY member_id HAVING(COUNT(DISTINCT orderno)>=#{startValue} and COUNT(DISTINCT orderno)<=#{endValue} )\n" +
                    ") t1 WHERE t1.member_id=members.member_id) ",

            /**
             * 5.{365}天内订单数大于{4}，且{180}天内订单数>0
             * SELECT id FROM members WHERE
             * org_id=1 AND brands_id=2 AND EXISTS (SELECT 1 FROM (
             * 		-- 365天内订单数大于4
             * 		SELECT member_id FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 and pay_time>='2019-11-26' AND EXISTS ( SELECT 1 FROM (
             * 		-- 180天订单>1
             * 			SELECT member_id FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 AND  pay_time>='2020-05-29'
             * 			GROUP BY member_id HAVING (COUNT(DISTINCT orderno) >1)
             * 		) t1 WHERE t1.member_id=t_orders.member_id )
             * 		GROUP BY member_id HAVING(COUNT(DISTINCT orderno)>4 )
             * ) t1 WHERE t1.member_id=members.member_id) ;
            */
            "org_id = #{orgId} and brands_id=#{brandsId} AND EXISTS (SELECT 1 FROM (\n" +
                    "  SELECT member_id FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1  and pay_time>='#{payTime1}' AND EXISTS ( SELECT 1 FROM ( \n" +
                    "    SELECT member_id FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 AND  pay_time>='#{payTime2}'  \n" +
                    "    GROUP BY member_id HAVING (COUNT(DISTINCT orderno) >1)\n" +
                    "  ) t1 WHERE t1.member_id=t_orders.member_id )\n" +
                    "  GROUP BY member_id HAVING(COUNT(DISTINCT orderno)>#{startValue} )\n" +
                    ") t1 WHERE t1.member_id=members.member_id)",

            /**
             * 6. {180}天内订单数=0，且历史总订单数>0
             * select id from members where
             * org_id=1 AND brands_id=2  and exists (select 1 from (
             * 	    select member_id from t_orders
             * 	    where org_id=1 AND brands_id=2 and order_type=1 and pay_time>='2019-11-27'
             * 	    group by member_id having (count(distinct orderno)>0 and max(pay_time)<'2020-05-29')
             * ) t1 where t1.member_id=members.member_id)
            */
            "org_id = #{orgId} and brands_id=#{brandsId} and exists (select 1 from (\n" +
                    "select member_id from t_orders \n" +
                    "where org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 and pay_time>='#{lose.payTime}' \n" +
                    "group by member_id having (count(distinct orderno)>0 and max(pay_time)<'#{payTime}')\n" +
                    ") t1 where t1.member_id=members.member_id)",

            /**
             * 7. {365}天内订单数=0  （排除 180内注册的）
             * SELECT id FROM members WHERE
             * org_id=1 AND brands_id=2 AND registered_time<'2020-05-29'
             * AND NOT  EXISTS (SELECT 1 FROM t_orders WHERE org_id=1 AND brands_id=2 and order_type=1 AND pay_time>='2019-11-26' AND member_id=members.member_id);
            */
            "org_id = #{orgId} and brands_id=#{brandsId} AND registered_time<'#{registeredTime}'" +
                    "AND NOT  EXISTS (SELECT 1 FROM t_orders WHERE org_id = #{orgId} and brands_id=#{brandsId} and order_type=1 AND pay_time>='#{payTime}' AND member_id=members.member_id)"
    };

}
