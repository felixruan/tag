package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsListResponseVO implements Serializable {

    private static final long serialVersionUID = 6635722179511406225L;

    @ApiModelProperty(value = "品牌id")
    private Long id;

    @ApiModelProperty(value = "品牌名称")
    private String brandsName;


}