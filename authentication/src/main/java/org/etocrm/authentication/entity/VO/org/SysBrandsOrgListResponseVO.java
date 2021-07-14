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
import org.etocrm.authentication.entity.VO.brands.SysBrandsListResponseVO;

import java.io.Serializable;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@ApiModel(value = "机构列表出参")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysBrandsOrgListResponseVO implements Serializable {

    private static final long serialVersionUID = 6580793330524771525L;

    @ApiModelProperty(value = "机构id")
    private Long id;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "对应的品牌集合")
    private List<SysBrandsListResponseVO> brands;

    @ApiModelProperty(value = "对应woaapOrgId")
    private Long woaapOrgId;

    @ApiModelProperty(value = "woaap机构名称")
    private String woappOrgName;

}