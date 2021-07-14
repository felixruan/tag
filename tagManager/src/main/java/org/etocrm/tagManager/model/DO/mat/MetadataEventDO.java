package org.etocrm.tagManager.model.DO.mat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_event")
public class MetadataEventDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -3257914896549340424L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

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
     * 应埋点平台;表明此事件需要埋点上报的端，可多选。 iOS、Android、JS、后台、其他
     */
    @ApiModelProperty(value = "应埋点平台")
    private String buryingPointPlatform;

    /**
     * 过去30天入库
     */
    @ApiModelProperty(value = "过去30天入库数")
    private Integer historyThirtyNum;

    /**
     * 上报数据;有无上报数据
     */
    @ApiModelProperty(value = "上报数据 0无，1有")
    private Integer isReport;

    /**
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态")
    private Integer status;

    /**
     * 是否接收;启动或关闭接收上报数据
     */
    @ApiModelProperty(value = "是否接收")
    private Integer isReceive;

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
}
