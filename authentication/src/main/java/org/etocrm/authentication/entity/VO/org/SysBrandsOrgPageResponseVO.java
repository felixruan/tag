package org.etocrm.authentication.entity.VO.org;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@ApiModel(value = "分页查询机构列表出参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysBrandsOrgPageResponseVO implements Serializable {

    private static final long serialVersionUID = 6580793330524771525L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "机构英文名")
    private String orgNameEn;

    @ApiModelProperty(value = "品牌数量")
    private Integer orgBrandsCount;

    @ApiModelProperty(value = "woaap机构名称")
    private String woappOrgName;

    @ApiModelProperty(value = "woaapOrgId")
    private Long woaapOrgId;
}