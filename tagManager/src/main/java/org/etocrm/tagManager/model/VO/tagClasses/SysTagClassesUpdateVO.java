package org.etocrm.tagManager.model.VO.tagClasses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.etocrm.dynamicDataSource.util.BasePojo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author  lingshuang.pang
 * @Date 2020/8/31 16:38
 */
@ApiModel(value = "系统标签分类表 Update VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagClassesUpdateVO {
    /** 主键 */
    @ApiModelProperty(value = "主键")
    @NotNull(message = "主键不得为空")
    private Long id ;

    /** 分类名称 */
    @ApiModelProperty(value = "分类名称")
    @NotBlank(message = "分类名称不得为空")
    private String tagClassesName ;
}
