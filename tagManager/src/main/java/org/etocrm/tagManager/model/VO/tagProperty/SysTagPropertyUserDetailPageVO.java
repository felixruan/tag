package org.etocrm.tagManager.model.VO.tagProperty;

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
public class SysTagPropertyUserDetailPageVO extends TagPageInfo {

    @ApiModelProperty(value = "标签id")
    @NotNull(message = "标签id不能为空")
    private Long id;

}
