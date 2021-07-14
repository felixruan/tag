package org.etocrm.tagManager.model.VO.tagClasses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.etocrm.dynamicDataSource.util.BasePojo;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author  lingshuang.pang
 * @Date 2020/8/31 16:38
 */
@ApiModel(value = "系统标签分类表 ADD VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagClassesAddVO {

    /** 分类名称 */
    @NotBlank(message = "分类名称不得为空")
    @ApiModelProperty(value = "分类名称")
    private String tagClassesName ;
}
