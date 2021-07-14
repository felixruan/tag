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

/**
 * Create By peter.li
 */
@ApiModel(value = "自动化营销流程人群处理类型VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MatWorkProcessHandleVO implements Serializable {

    private static final long serialVersionUID = 2893916130208825608L;

    /** id */
    @ApiModelProperty(value = "id")
    private Long id ;

    /** 流程id */
    @ApiModelProperty(value = "流程id")
    private Long workId ;

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
