package org.etocrm.dynamicDataSource.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: dkx
 * @Date: 19:48 2020/9/9
 * @Desc:
 */
@Data
public class TagPageInfo {

    @ApiModelProperty(value = "当前页" )
    @NotNull(message = "当前页码不能为空")
    private Long current;

    @ApiModelProperty(value = "每页的数量" )
    @NotNull(message = "每页记录数不能为空")
    private Long size;
}
