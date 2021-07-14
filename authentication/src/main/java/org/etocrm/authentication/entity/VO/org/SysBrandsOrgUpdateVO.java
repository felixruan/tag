package org.etocrm.authentication.entity.VO.org;

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
 * @Date 2020-09-01
 */
@ApiModel(value = "修改机构入参 " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysBrandsOrgUpdateVO  implements Serializable {

    private static final long serialVersionUID = 6580793330524771525L;

    @NotNull(message = "机构ID不能为空")
    @ApiModelProperty(value = "机构ID" ,required = true)
    private Long id;

    @NotBlank(message = "机构名称不能为空")
    @ApiModelProperty(value = "机构名称" ,required = true)
    private String orgName;

    @NotBlank(message = "机构英文名不能为空")
    @ApiModelProperty(value = "机构英文名" ,required = true)
    private String orgNameEn;


    @NotNull(message = "机构id不能为空")
    @ApiModelProperty(value = "机构id",required = true)
    private Long woaapOrgId;

}