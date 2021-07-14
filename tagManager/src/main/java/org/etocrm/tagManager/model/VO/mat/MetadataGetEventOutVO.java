package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "元数据根据属性code获取事物VO ")
@Data
public class MetadataGetEventOutVO implements Serializable {

    private static final long serialVersionUID = -5951834006485625413L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 属性名
     */
    private String eventCode;
    /**
     * 属性显示名
     */
    @ApiModelProperty(value = "属性名")
    private String eventName;

    /**
     * 应埋点平台;表明此事件需要埋点上报的端，可多选。 iOS、Android、JS、后台、其他
     */
    @ApiModelProperty(value = "应埋点平台")
    private String buryingPointPlatform;

    /**
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态")
    private Integer status;

    /**
     * 上报数据;有无上报数据
     */
    @ApiModelProperty(value = "上报数据")
    private Integer isReport;

    /**
     * 是否接收;启动或关闭接收上报数据
     */
    @ApiModelProperty(value = "是否接收")
    private Integer isReceive;

    /**
     * 过去30天入库
     */
    @ApiModelProperty(value = "过去30天入库数")
    private Integer historyThirtyNum;

    /**
     * 备注;帮助业务人员更好理解的信息
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 触发时机;例如：是在点击按钮的时候上报，还是已进入页面后即上报。
     */
    /*@ApiModelProperty(value = "触发时机")
    private String touchOpportunity;*/


}
