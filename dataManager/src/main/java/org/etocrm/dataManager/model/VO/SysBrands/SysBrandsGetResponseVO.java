package org.etocrm.dataManager.model.VO.SysBrands;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsGetResponseVO implements Serializable {


    private static final long serialVersionUID = 3285232804493789173L;
    @ApiModelProperty(value = "品牌id")
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "品牌名称")
    private String brandsName;

    @ApiModelProperty(value = "品牌英文名称")
    private String brandsNameEn;

    @ApiModelProperty(value = "品牌logoUrl")
    private String brandsLogoUrl;

    @ApiModelProperty(value = "微信小程序的appid 可能存在多个 逗号隔开")
    private String appIds;

}