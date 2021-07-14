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
public class SysTagUpdateVO {
    /**
     * 主键
     */
    @NotNull(message = "标签ID不得为空")
    @ApiModelProperty(value = "标签ID",required = true)
    private Long id;

    /**
     * 标签分类ID
     */
    @NotNull(message = "标签分类ID不能为空")
    @ApiModelProperty(value = "标签分类ID",required = true)
    private Long tagClassesId;


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
     * 标签更新频率 dictId
     */
    @NotNull(message = "标签更新频率不得为空")
    @ApiModelProperty(value = "标签更新频率, 系统字典： dictParentCode 为 tag_update_frequency")
    private Long tagUpdateFrequency;

//    @NotNull(message = "标签类型不得为空")
//    @ApiModelProperty(value = "2期=====  标签类型,查询字典表，传dictValue,显示dictName")
//    private String tagType;

    @ApiModelProperty(value = "公众号appId")
    private String appId;

}