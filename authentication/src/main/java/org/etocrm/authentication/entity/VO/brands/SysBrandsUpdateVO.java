package org.etocrm.authentication.entity.VO.brands;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsUpdateVO implements Serializable {

    private static final long serialVersionUID = 6635722179511406225L;

    @ApiModelProperty(value = "品牌id",required = true)
    @NotNull(message = "品牌ID不能为空")
    private Long id;

//    品牌机构一旦选择，不能修改
//    @ApiModelProperty(value = "机构id",required = true)
//    @NotNull(message = "机构ID不能为空")
//    private Long orgId;

    @ApiModelProperty(value = "品牌名称",required = true)
    @NotBlank(message = "品牌名称不能为空")
    private String brandsName;

    @ApiModelProperty(value = "品牌英文名称" ,required = true)
    @NotBlank(message = "品牌英文名称不能为空")
    private String brandsNameEn;

    @ApiModelProperty(value = "品牌logoUrl",required = true)
    @NotBlank(message = "品牌logoUrl不能为空")
    private String brandsLogoUrl;

    @ApiModelProperty(value = "woaap对应关系",required = true)
    private List<WoaapBrandsVO> woaapList;

}