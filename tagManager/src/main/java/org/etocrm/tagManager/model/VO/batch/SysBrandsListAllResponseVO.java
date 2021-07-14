package org.etocrm.tagManager.model.VO.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Data
public class SysBrandsListAllResponseVO implements Serializable {

    private static final long serialVersionUID = 6635722179511406225L;

    @ApiModelProperty(value = "品牌id")
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long orgId;
}