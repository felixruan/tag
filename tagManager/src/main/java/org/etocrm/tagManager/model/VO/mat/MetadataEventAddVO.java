package org.etocrm.tagManager.model.VO.mat;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Create By peter.li
 */
@ApiModel(value = "元数据事件新增VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MetadataEventAddVO implements Serializable {

    private static final long serialVersionUID = 5980380258021057089L;


    /**
     * 事件名
     */
    @ApiModelProperty(value = "事件名")
    private String eventCode;

    /**
     * 事件显示名
     */
    @ApiModelProperty(value = "事件显示名")
    private String eventName;

    /**
     * 应埋点平台;表明此事件需要埋点上报的端，可多选。 1 iOS、2 Android、3 JavaScript、4 小程序、5 服务端、6 其他
     */
    @ApiModelProperty(value = "应埋点平台: 1 iOS、2 Android、3 JavaScript、4 小程序、5 服务端、6 其他")
    private Integer buryingPointPlatform;

    /**
     * 上报数据;有无上报数据
     */
    /*@ApiModelProperty(value = "上报数据: 0 无、1 有")
    private Integer isReport;*/

    /**
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态: 0 可见，1 隐藏")
    private Integer status;

    /**
     * 是否接收;启动或关闭接收上报数据
     */
    @ApiModelProperty(value = "是否接收上报数据: 0 接收、1 不接收")
    private Integer isReceive;

    /**
     * 过去30天入库
     */
    /*@ApiModelProperty(value = "过去30天入库数")
    private Integer historyThirtyNum;*/

    /**
     * 备注;帮助业务人员更好理解的信息
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 触发时机;例如：是在点击按钮的时候上报，还是已进入页面后即上报。
     */
   /* @ApiModelProperty(value = "触发时机")
    private String touchOpportunity;*/

    /**
     * 事物关联事物属性的propertyCode集合
     */
    @ApiModelProperty(value = "事物关联事物属性的propertyCode集合")
    private List<String> eventPropertyCodes;

}
