package org.etocrm.tagManager.model.VO.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:55
 */
@ApiModel(value = "标签出参")
@Data
public class SysTagVO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "标签id")
    private Long id;

    /**
     * 品牌id
     */
    private Long brandsId;

    /**
     * 标签分类
     */
    @ApiModelProperty(value = "标签分类")
    private Long tagClassesId;

    @ApiModelProperty(value = "标签分类名字")
    private String tagClassesIdName;

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
     * 启用状态
     */
    @ApiModelProperty(value = "启用状态，1-启用，0-停用")
    private Integer tagStatus;

    /**
     * 覆盖人数
     */
    @ApiModelProperty(value = "覆盖人数")
    private Integer coveredPeopleNum;

    @ApiModelProperty(value = "更新时间")
    private String updatedTime;

    @ApiModelProperty(value = "创建时间 （自定义标签）")
    private String createdTime;

    @ApiModelProperty(value = "2期修改===  更新频率")
    private Long tagUpdateFrequency;

    @ApiModelProperty(value = "标签类型")
    private String tagType;

    @ApiModelProperty(value = "公众号appId")
    private String appId;

    @ApiModelProperty(value = "公众号")
    private String appIdName;

    @ApiModelProperty(value = "数据最后执行时间")
    private String tagLastUpdateDate;
}
