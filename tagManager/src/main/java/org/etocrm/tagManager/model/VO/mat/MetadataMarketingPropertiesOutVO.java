package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "提供对外属性VO ")
@Data
public class MetadataMarketingPropertiesOutVO implements Serializable {

    private static final long serialVersionUID = 7325898232249806542L;

    private Long id;
    /**
     * 属性名
     */
    private String propertyCode;
    /**
     * 属性显示名
     */
    private String propertyName;


}
