package org.etocrm.authentication.entity.VO.industry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-05
 */
@ApiModel(value = "修改行业入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysIndustryUpdateVO implements Serializable {

    private static final long serialVersionUID = -6278598933407349472L;

    @NotNull(message = "行业id不能为空")
    @ApiModelProperty(value = "行业id",required = true)
    private Long id;

    @NotBlank(message = "行业名称不能为空")
    @ApiModelProperty(value = "行业名称",required = true)
    private String industryName;

}