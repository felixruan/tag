package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class SysTagGroupRuleVO implements Serializable {
    private static final long serialVersionUID = 177206764393056425L;

    @ApiModelProperty(value = "群组id")
    @NotNull(message = "群组id不能为空")
    private Long id;

    @NotNull(message = "群组间关系不能为空")
    @ApiModelProperty(value = "标签群组规则关系id", required = true)
    private Long tagGroupRuleRelationshipId;

    @Valid
    @Size(min = 1, message = "群组规则不能为空")
    @NotNull(message = "群组规则不能为空")
    @ApiModelProperty(value = "标签群组规则", required = true)
    private List<SysTagGroupRuleRequestVO> rule;

    @Range(min = 1,max = 100, message = "请输入正确的百分比")
    @ApiModelProperty(value = "人群-限制总人数-百分比，和固定人数二选一")
    private Integer tagGroupCountLimitPercent;

    @Range(min = 1,message = "请输入正确的人数")
    @ApiModelProperty(value = "人群-限制总人数-固定人数，和百分比二选一")
    private Integer tagGroupCountLimitNum;

    @ApiModelProperty(value = "剔除已有人群ids,逗号分隔")
    private String excludeUserGroupId;
}
