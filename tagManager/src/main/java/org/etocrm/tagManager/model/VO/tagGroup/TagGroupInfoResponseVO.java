package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TagGroupInfoResponseVO {

    @ApiModelProperty(value = "群组id")
    private Long tagGroupId;

    @ApiModelProperty(value = "群组名称")
    private String tagGroupName;

    @ApiModelProperty(value = "群组间的且/或")
    private Long relationshipId;

    @ApiModelProperty(value = "规则")
    private List<SysTagGroupRuleResponseVO> groupRule;
}
