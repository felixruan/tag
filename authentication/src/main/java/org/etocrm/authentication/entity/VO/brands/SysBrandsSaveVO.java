package org.etocrm.authentication.entity.VO.brands;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsSaveVO implements Serializable {

    private static final long serialVersionUID = 6635722179511406225L;

    @NotNull(message = "机构id不能为空")
    @ApiModelProperty(value = "机构id",required = true)
    private Long orgId;

    @NotBlank(message = "品牌名称不能为空")
    @ApiModelProperty(value = "品牌名称",required = true)
    private String brandsName;

    @NotBlank(message = "品牌英文名称不能为空")
    @ApiModelProperty(value = "品牌英文名称" ,required = true)
    private String brandsNameEn;

    @NotBlank(message = "品牌logoUrl不能为空")
    @ApiModelProperty(value = "品牌logoUrl",required = true)
    private String brandsLogoUrl;

    @ApiModelProperty(value = "woaap对应关系",required = true)
    private List<WoaapBrandsVO> woaapList;

}