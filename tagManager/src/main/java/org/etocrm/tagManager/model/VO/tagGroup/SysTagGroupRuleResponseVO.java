package org.etocrm.tagManager.model.VO.tagGroup;

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

@ApiModel(value = "查询标签群组规则入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysTagGroupRuleResponseVO implements Serializable {

    private static final long serialVersionUID = -4183319355590249513L;

    @ApiModelProperty(value = "群组规则运算关系id")
    private Long tagGroupRuleRelationshipId;

//    //todo 前端其实不需要？ 只需要返回id 吧？
//    @ApiModelProperty(value = "群组规则运算关系名称")
//    private Long tagGroupRuleRelationshipName;

    @ApiModelProperty(value = "标签群组规则")
    private List<SysTagGroupRuleInfoResponseVO> tagGroupRule;

}





