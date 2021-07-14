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

import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-05
 */
@ApiModel(value = "包含标签数量的行业列表出参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysIndustryListWithTagResponseVO implements Serializable {

    private static final long serialVersionUID = -6278598933407349472L;

    @ApiModelProperty(value = "行业id")
    private Long id;

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "标签数量")
    private Integer tagCount;

}