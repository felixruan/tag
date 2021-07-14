package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import javax.validation.constraints.NotNull;

/**
 * 获取人群明细列表分页VO
 * Create By peter.li
 */
@ApiModel(value = "获取人群明细列表分页VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SysTagGroupUserDetailPageVO {

    @ApiModelProperty(value = "群组id")
    @NotNull(message = "群组id不能为空")
    private Long tagGroupId;

    @ApiModelProperty(value = "当前页" )
    @NotNull(message = "当前页码不能为空")
    private Long current;

    @ApiModelProperty(value = "每页的数量" )
    @NotNull(message = "每页记录数不能为空")
    private Long size;
}
