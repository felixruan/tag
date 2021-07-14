package org.etocrm.authentication.entity.VO.brands;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 品牌分页查询列表 出参
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsPageResponseVO  implements Serializable {

    private static final long serialVersionUID = 6635722179511406225L;

    @ApiModelProperty(value = "品牌id" )
    private Long id;

    @ApiModelProperty(value = "品牌名称" )
    private String brandsName;

    @ApiModelProperty(value = "品牌英文名称" )
    private String brandsNameEn;

    @NotBlank(message = "品牌logoUrl不能为空")
    private String brandsLogoUrl;

    @ApiModelProperty(value = "机构名称" )
    private String orgName;

    @ApiModelProperty(value = "机构英文名称" )
    private String orgNameEn;

    //标签数量
    @ApiModelProperty(value = "标签数量" )
    private Integer tagCount;

    @ApiModelProperty(value = "机构id" )
    private Long orgId;

    @ApiModelProperty(value = "woaap品牌名称")
    private String woaapBrandsName;

    @ApiModelProperty(value = "微信小程序的appid 可能存在多个 逗号隔开")
    private String appIds;

    @ApiModelProperty(value = "woaap机构id")
    private String woaapOrgId;

}