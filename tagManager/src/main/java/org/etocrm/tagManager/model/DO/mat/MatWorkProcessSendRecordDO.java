package org.etocrm.tagManager.model.DO.mat;

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
@ApiModel(value = "自动化营销流程发送记录DO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_map_send_records")
public class MatWorkProcessSendRecordDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 8008383392779293255L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /** mat系统works_id */
    @ApiModelProperty(value = "mat系统works_id")
    private Long matWorksId ;

    /** mat系统handle_id */
    @ApiModelProperty(value = "mat系统handle_id")
    private Long matHandleId ;

    /** mat系统action_id */
    @ApiModelProperty(value = "mat系统action_id")
    private Long matActionId ;

    /** 会员ID */
    @ApiModelProperty(value = "会员ID")
    private Integer memberId ;

    /** unionId */
    @ApiModelProperty(value = "unionId")
    private String unionid ;

    /** 状态;0:取消 1:等待发送确认 2:等待发送 3:发送中 4:发送暂停 5:发送完成 */
    @ApiModelProperty(value = "状态;0:取消 1:等待发送确认 2:等待发送 3:发送中 4:发送暂停 5:发送完成")
    private Integer status ;

    /** 发送开始时间 */
    @ApiModelProperty(value = "发送开始时间")
    private String  beginTime ;

    /** 发送结束时间 */
    @ApiModelProperty(value = "发送结束时间")
    private String endTime ;

    /** 发送是否成功 */
    @ApiModelProperty(value = "发送是否成功 : 1 成功，0 失败")
    private Integer sendStatus ;

    /** 备注说明 */
    @ApiModelProperty(value = "备注说明")
    private String remark ;



}
