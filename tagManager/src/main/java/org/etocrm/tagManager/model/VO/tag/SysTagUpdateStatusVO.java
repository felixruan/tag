package org.etocrm.tagManager.model.VO.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:55
 */
@ApiModel(value = "标签修改入参VO")
@Data
public class SysTagUpdateStatusVO {
    /**
     * 主键
     */
    @NotNull(message = "标签ID不得为空")
    @ApiModelProperty(value = "标签ID",required = true)
    private Long id;

    /**
     * 启用状态
     */
    @NotNull(message = "标签状态不能为空")
    @ApiModelProperty(value = "启用状态,0-启用 1-停用",required = true)
    private Integer tagStatus;
}
