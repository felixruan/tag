package org.etocrm.tagManager.model.DO.mat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * Create By peter.li
 */
@ApiModel(value = "自动化营销流程人群处理类型DO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_map_work_handles")
public class MatWorkProcessHandleDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -6436010293300566200L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /** mat系统handle_id */
    @ApiModelProperty(value = "mat系统handle_id")
    private Long matId ;

    /** 流程id */
    @ApiModelProperty(value = "流程id")
    private Long workId ;

    /** mat系统流程id */
    @ApiModelProperty(value = "mat系统流程id")
    private Long matWorkId ;

    /** 流程选择;0:否 1:是 */
    @ApiModelProperty(value = "流程选择;0:否 1:是")
    private Integer processType ;

    /** 是否开启ABTest;0:关闭 1:开启 */
    @ApiModelProperty(value = "是否开启ABTest;0:关闭 1:开启")
    private Integer isOpenAbtest ;

    /** 是否对照组;0:否 1:是 */
    @ApiModelProperty(value = "是否对照组;0:否 1:是")
    private Integer isCompare ;

    /** 支线所占百分比 */
    @ApiModelProperty(value = "支线所占百分比")
    private Double percent ;

    /** ABTest剩余处理执行时间 */
    @ApiModelProperty(value = "ABTest剩余处理执行时间")
    private String execTime ;



}
