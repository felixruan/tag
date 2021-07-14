package org.etocrm.authentication.entity.VO.tagBrands;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:55
 */
@ApiModel(value = "系统标签表")
@Data
public class SysTagTreeVO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 标签名称
     */
    @ApiModelProperty(value = "标签名称")
    private String tagName;

    /**
     * 标签选中状态
     */
    @ApiModelProperty(value = "标签选中状态 0-选中，1-未选中 ,2-已使用")
    private Integer tagFlag;

}
