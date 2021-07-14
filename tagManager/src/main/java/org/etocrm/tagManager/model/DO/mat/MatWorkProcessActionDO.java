package org.etocrm.tagManager.model.DO.mat;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * Create By peter.li
 */
@ApiModel(value = "自动化营销流程每种处理支线要发送的内容DO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_map_works_actions")
public class MatWorkProcessActionDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -1709534671122583089L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /** mat的系统id */
    @ApiModelProperty(value = "mat的系统id")
    private Long matId ;

    /** works流程表主键id */
    @ApiModelProperty(value = "works流程表主键id")
    private Long worksId ;

    /** mat系统works流程表id */
    @ApiModelProperty(value = "mat系统works流程表id")
    private Long matWorkId ;

    /** handle表主键id */
    @ApiModelProperty(value = "handle表主键id")
    private Long handleId ;

    /** mat系统handleid */
    @ApiModelProperty(value = "mat系统handleid")
    private Long matHandleId ;

    /** 品牌id */
    @ApiModelProperty(value = "品牌id")
    private Long brandId ;

    /** 机构id */
    @ApiModelProperty(value = "机构id")
    private Long originalId ;

    /** 微信素材的 media_id */
    @ApiModelProperty(value = "微信素材的media_id")
    private Long mediaId ;

    /** 发送内容详情 */
    @ApiModelProperty(value = "发送内容详情")
    private String details ;

    /** 是否对照组;0:否 1:是 */
    /*@ApiModelProperty(value = "是否对照组;0:否 1:是")
    private Integer isCompare ;*/

    /** 支线所占百分比 */
   /* @ApiModelProperty(value = "支线所占百分比")
    private Double percent ;*/

    /** 消息类型;0:不处理 1:分组群发 2:客服消息 3:模板消息 4:小程序订阅消息 */
    @ApiModelProperty(value = "消息类型;0:不处理 1:分组群发 2:客服消息 3:模板消息 4:小程序订阅消息")
    private Integer sendType ;

    /** 标签类型;1:公众号标签 2:CRM标签 */
    @ApiModelProperty(value = "标签类型;1:公众号标签 2:CRM标签")
    private Integer tagType ;

    /** 标签分组id */
    /*@ApiModelProperty(value = "标签分组id")
    private Integer tagGroupId ;*/

    /** 公众号或小程序的APPID */
    @ApiModelProperty(value = "公众号或小程序的APPID")
    private String appid ;

    /** 发送内容;保存素材ID或模板ID或短信内容ID或标签ID等信息 */
    /*@ApiModelProperty(value = "发送内容;保存素材ID或模板ID或短信内容ID或标签ID等信息")
    private String sendValue ;*/

    /** 拓展信息 */
   /* @ApiModelProperty(value = "拓展信息")
    private String expandInfo ;*/

    /** 延迟小时 */
    @ApiModelProperty(value = "延迟小时")
    private Integer delayHour ;

    /** 延迟分钟 */
    @ApiModelProperty(value = "延迟分钟")
    private Integer delayMinute ;

    /** 延迟秒 */
    @ApiModelProperty(value = "延迟秒")
    private Integer delaySecond ;



}
