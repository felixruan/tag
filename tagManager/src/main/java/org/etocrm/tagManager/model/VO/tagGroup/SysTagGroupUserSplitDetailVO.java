package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 拆分人群明细列表VO
 * Create By peter.li
 */
@ApiModel(value = "拆分人群明细列表VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SysTagGroupUserSplitDetailVO {

    @ApiModelProperty(value = "群组id")
    @NotNull(message = "群组id不能为空")
    private Long tagGroupId;

    @ApiModelProperty(value = "拆分方式" )
    @NotBlank(message = "拆分方式不能为空")
    private String splitWay;

    @ApiModelProperty(value = "拆分内容" )
    private List<SplitWayVO> data;
}
