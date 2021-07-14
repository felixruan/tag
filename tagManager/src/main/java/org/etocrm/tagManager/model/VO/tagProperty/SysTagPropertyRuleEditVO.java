package org.etocrm.tagManager.model.VO.tagProperty;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyRuleEditVO implements Serializable {

    private static final long serialVersionUID = -5393738031546881657L;

    @ApiModelProperty(value = "业务模型ID",required = true)
    @NotNull(message = "业务模型不能为空")
    private Long modelTableId;

    @ApiModelProperty(value = "业务模型字段ID",required = true)
    @NotNull(message = "业务模型字段ID不能为空")
    private Long columnId;

    @ApiModelProperty(value = "字段数据类型ID",required = true)
    @NotNull(message = "字段数据类型不能为空")
    private Long columnDataTypeId;

    @ApiModelProperty(value = "字段数据类型dictCode",required = true)
    @NotBlank( message = "字段数据类型dictCode不能为空")
    private String columnDataTypeDictCode;


    /**
     * {"id":1,"startValue":"","endValue":"","controlTypeId":1}
     * {"id":1,"childId":{"id":"","startValue":"","endValue":"","controlTypeId":1}}
     */
    @ApiModelProperty(value = "条件运算信息",required = true)
    @Valid
    @NotNull(message = "条件运算信息不能为空")
    private SysTagPropertyRuleLogicalOperationVO logicalOperation;//


}