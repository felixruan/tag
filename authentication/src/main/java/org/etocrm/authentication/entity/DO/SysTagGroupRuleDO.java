package org.etocrm.authentication.entity.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "系统标签群组规则表 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_tag_group_rule")
public class SysTagGroupRuleDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 1504000762048307718L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签群组ID")
    private Long tagGroupId;

    @ApiModelProperty(value = "父id")
    private Long tagGroupRuleParentId;

    @ApiModelProperty(value = "群组名称")
    private String tagGroupRuleName;

    @ApiModelProperty(value = "群组规则运算关系id")
    private Long tagGroupRuleRelationshipId;

    @ApiModelProperty(value = "标签群组规则")
    private String tagGroupRule;

    private Long tagId;


}