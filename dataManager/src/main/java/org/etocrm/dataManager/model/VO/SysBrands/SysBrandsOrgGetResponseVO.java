package org.etocrm.dataManager.model.VO.SysBrands;

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
 * @Date 2020-09-01
 */
@ApiModel(value = "机构出参 " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysBrandsOrgGetResponseVO implements Serializable {

    private static final long serialVersionUID = 6580793330524771525L;

    @ApiModelProperty(value = "机构ID" )
    private Long id;

    @ApiModelProperty(value = "机构名称" )
    private String orgName;

    @ApiModelProperty(value = "机构英文名" )
    private String orgNameEn;

    @ApiModelProperty(value = "微信小程序的appid 可能存在多个 逗号隔开")
    private String appIds;

}