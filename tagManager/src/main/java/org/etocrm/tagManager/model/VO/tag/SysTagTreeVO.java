package org.etocrm.tagManager.model.VO.tag;

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
     * 标签定义
     */
    @ApiModelProperty(value = "标签定义")
    private String tagMemo;

    /**
     * 标签分类
     */
    @ApiModelProperty(value = "标签分类")
    private Long tagClassesId;
}
