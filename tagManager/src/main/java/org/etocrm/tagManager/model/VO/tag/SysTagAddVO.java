package org.etocrm.tagManager.model.VO.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:55
 */
@ApiModel(value = "系统标签表 ADD VO")
@Data
public class SysTagAddVO {

    /**
     * 标签分类
     */
    @NotNull(message = "标签分类不得为空")
    @ApiModelProperty(value = "标签分类")
    private Long tagClassesId;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不得为空")
    @ApiModelProperty(value = "标签名称")
    private String tagName;

    /**
     * 标签定义
     */
    @NotBlank(message = "标签定义不得为空")
    @ApiModelProperty(value = "标签定义")
    private String tagMemo;

    /**
     * 标签更新频率 dictId
     */
    @NotNull(message = "标签更新频率不得为空")
    @ApiModelProperty(value = "2期=====  标签更新频率, 系统字典： dictParentCode 为 tag_update_frequency")
    private Long tagUpdateFrequency;

    @ApiModelProperty(value = "2期=====  标签类型,查询字典表，传dictValue,显示dictName")
    @NotNull(message = "标签类型不得为空")
    private String tagType;

    @ApiModelProperty(value = "公众号appId")
    private String appId;
}
