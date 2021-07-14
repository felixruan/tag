package org.etocrm.tagManager.model.VO.tagProperty;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.tagManager.util.DataRange;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyRuleLogicalOperationVO implements Serializable {

    private static final long serialVersionUID = -5393738031546881657L;

    //id":1,"startValue":"","endValue":"","controlTypeId
    @ApiModelProperty(value = "条件运算关系id",required = true)
    @NotNull(message = "逻辑预算id不能为空")
    private Long id;

    @ApiModelProperty(value = "逻辑运算dictCode",required = true)
    @NotNull(message = "逻辑运算dictCode不能为空")
    private String dictCode;

    @ApiModelProperty(value = "逻辑运算名称",required = true)
//    @NotBlank(message = "逻辑运算名称不能为空")
    private String dictCodeName;

    @ApiModelProperty(value = "运算值")
    private String startValue;

    @ApiModelProperty(value = "运算值")
    private String endValue;

    @ApiModelProperty(value = "字段值控件id")
    private Long controlTypeId;

    @ApiModelProperty(value = "字段值控件dictCode")
    private String controlTypeDictCode;

    @ApiModelProperty(value = "条件逻辑运算二")
    private SysTagPropertyRuleLogicalOperationVO child;//

    @ApiModelProperty(value = "值域类型数据")
    private List<DataRange> dataRangeList;

    @ApiModelProperty(value = "动态数据数据")
    private List<Object> dynamicDataList;

    @ApiModelProperty(value = "关联数据数据")
    private List<RelationDataVO> relationDataList;

}