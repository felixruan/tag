package org.etocrm.tagManager.model.VO.tagClasses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author  lingshuang.pang
 * @Date 2020/8/31 16:38
 */
@ApiModel(value = "标签分类 base VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagClassesBaseVO {
    /** 主键 */
    @ApiModelProperty(value = "主键")
    private Long id ;

    /** 分类名称 */
    @ApiModelProperty(value = "分类名称")
    private String tagClassesName ;

}
