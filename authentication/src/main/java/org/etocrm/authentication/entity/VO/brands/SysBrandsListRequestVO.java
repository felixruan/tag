package org.etocrm.authentication.entity.VO.brands;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsListRequestVO implements Serializable {

    private static final long serialVersionUID = 6635722179511406225L;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "2期===== 1-查询系统品牌，其他不查, 只有用户管理模块需要系统品牌")
    private Integer withSysBrands;
}