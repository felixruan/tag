package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.core.util.DateUtil;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "系统标签群组表 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_tag_group")
public class SysTagGroupDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -597577860379245865L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签群组名称")
    private String tagGroupName;

    @ApiModelProperty(value = "群组类型")
    private Integer tagGroupType;

    @ApiModelProperty(value = "群组启动类型")
    private Integer tagGroupStartType;

    @ApiModelProperty(value = "群组启动日期")
    private Date tagGroupStartTime;

    @ApiModelProperty(value = "动态群组指定静止日期")
    private Date tagGroupRestDate;

    @ApiModelProperty(value = "标签群组描述")
    private String tagGroupMemo;

    @ApiModelProperty(value = "标签群组状态")
    private Integer tagGroupStatus;

    @ApiModelProperty(value = "群组规则更新执行状态，0-未执行，1-已执行")
    private Integer tagGroupRuleChangeExecuteStatus;

    @ApiModelProperty(value = "标签群组规则关系id")
    private Long tagGroupRuleRelationshipId;

    @ApiModelProperty(value = "人群-限制总人数-百分比，和固定人数二选一")
    private Integer tagGroupCountLimitPercent;

    @ApiModelProperty(value = "人群-限制总人数-固定人数，和百分比二选一")
    private Integer tagGroupCountLimitNum;

    @ApiModelProperty(value = "剔除已有人群ids,逗号分隔")
    private String excludeUserGroupId;

    @ApiModelProperty(value = "子群组数量")
    private Long tagGroupSplitCount;

    @ApiModelProperty(value = "分组规则")
    private String tagGroupSplitRule;

    @ApiModelProperty(value = "品牌id")
    private Long brandsId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "群组覆盖数据信息")
    private String countUserInfo;

    @ApiModelProperty(value = "子群组信息")
    private String sonCountInfo;
}