package org.etocrm.tagManager.model.VO.mat;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Create By peter.li
 */
@ApiModel(value = "自动化营销流程VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MatWorkProcessVO implements Serializable {

    private static final long serialVersionUID = 531133261809608604L;

    /** id */
    @ApiModelProperty(value = "id")
    private Long id ;

    /** 分支id handleId ，用于标记子流程*/
    @ApiModelProperty(value = "handleId")
    private Long handleId ;

    /** mat系统机构id*/
    @ApiModelProperty(value = "mat系统机构id")
    private Long orgId ;

    /** 流程名称 */
    @ApiModelProperty(value = "流程名称")
    private String name ;

    /** 营销类型;1:事件营销 2:单次营销 3:周期性营销 4:被动触发营销 */
    @ApiModelProperty(value = "营销类型 1:事件营销 2:单次营销 3:周期性营销 4:被动触发营销")
    private Integer type ;

    /** 状态;0:取消 1:未执行 2:等待执行 3:执行中 4: 已执行 */
    @ApiModelProperty(value = "状态;0:取消 1:未执行 2:等待执行 3:执行中 4: 已执行")
    private Integer status ;

    /** 客户分组id */
    @ApiModelProperty(value = "客户分组id")
    private Long userGroupId ;

    /** 事件营销触发条件 */
    @ApiModelProperty(value = "事件营销触发条件")
    private JSONObject triggerCondition ;

    /** 任务开始时间 */
    @ApiModelProperty(value = "任务开始时间")
    private String beginTime ;

    /** 任务结束时间 */
    @ApiModelProperty(value = "任务结束时间")
    private String endTime ;

    /** 单次营销执行类型;0:立即执行 1:计划执行' */
    @ApiModelProperty(value = "单次营销执行类型;0:立即执行 1:计划执行")
    private Integer execType ;

    /** 单次营销执行时间 */
    @ApiModelProperty(value = "单次营销执行时间")
    private String execTime ;

    /** 周期性执行时间配置 */
    @ApiModelProperty(value = "周期性执行时间配置")
    private JSONObject execConfig ;

    /** 触发次数;0:不限 1:限制 */
    @ApiModelProperty(value = "触发次数;0:不限 1:限制")
    private Integer sendLimit ;

    /** 触发次数具体配置 */
    @ApiModelProperty(value = "触发次数具体配置")
    private JSONObject sendLimitConfig ;

    /** 人群处理类型;1:不筛选 2:属性筛选 */
    @ApiModelProperty(value = "人群处理类型;1:不筛选 2:属性筛选")
    private Integer handleType ;

    /** 属性筛选条件 */
    @ApiModelProperty(value = "属性筛选条件")
    private JSONObject searchCondition ;

    /** 是否开启ABTest;0:关闭 1:开启 */
    @ApiModelProperty(value = "是否开启ABTest;0:关闭 1:开启")
    private Integer isOpenAbtest ;

    /** 最近一次执行时间 */
    @ApiModelProperty(value = "最近一次执行时间")
    private String lastExecTime ;

    /** 触发人数 */
    @ApiModelProperty(value = "触发人数")
    private Integer triggerCount ;

    /** 备注说明 */
    @ApiModelProperty(value = "备注说明")
    private String remark ;

    /** 是否对照组;0:否 1:是 */
    /*@ApiModelProperty(value = "是否对照组;0:否 1:是")
    private Integer isCompare ;*/

    /** 支线所占百分比 */
    /*@ApiModelProperty(value = "支线所占百分比")
    private Double percent ;*/

    /** ABTest剩余处理执行时间 */
    /*@ApiModelProperty(value = "ABTest剩余处理执行时间")
    private String execSurplusTime ;*/

    @ApiModelProperty(value = "流程人群处理类型组")
    private List<MatWorkProcessHandleVO> handles;

    @ApiModelProperty(value = "处理支线要发送内容组")
    private List<MatWorkProcessActionVO> actions;

    @ApiModelProperty(value = "子流程详细信息",hidden = true)
    private MatWorkProcessVO subProcessVO;

    @ApiModelProperty(value = "works表主键id",hidden = true)
    private Long worksId ;

    @ApiModelProperty(value = "works表主键id",hidden = true)
    private Map<Long,Long> handleIdMap;

    @ApiModelProperty(value = "会员id",hidden = true)
    private String memberId;

    @ApiModelProperty(value = "会员id集",hidden = true)
    private String memberIds;



}
