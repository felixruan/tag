package org.etocrm.tagManager.model.VO.tagClasses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.etocrm.tagManager.model.VO.tag.SysTagTreeVO;

import java.util.List;

/**
 * @author  lingshuang.pang
 * @Date 2020/8/31 16:38
 */
@ApiModel(value = "系统标签分类表")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagClassesTreeVO {
    /** 主键 */
    @ApiModelProperty(value = "主键")
    private Long id ;

    /** 分类名称 */
    @ApiModelProperty(value = "分类名称")
    private String tagClassesName ;

    /** 标签列表 */
    private List<SysTagTreeVO> tagVOList;
}
