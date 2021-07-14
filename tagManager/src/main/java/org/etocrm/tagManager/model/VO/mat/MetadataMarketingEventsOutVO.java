package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "提供对外事物VO ")
@Data
public class MetadataMarketingEventsOutVO implements Serializable {

    private static final long serialVersionUID = -6063829911447729380L;

    private Long id;

    /**
     * 属性名
     */
    private String eventCode;

    /**
     * 属性显示名
     */
    private String eventName;


}
