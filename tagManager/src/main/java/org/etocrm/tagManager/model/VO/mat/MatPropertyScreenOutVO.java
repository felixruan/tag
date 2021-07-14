package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "查询模型列表出参VO ")
@Data
public class MatPropertyScreenOutVO implements Serializable {

    private static final long serialVersionUID = 4105067363933956147L;
    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private Long id;

    /**
     * 模型显示名
     */
    @ApiModelProperty(value = "模型显示名")
    private String modelName;


    //private List<MetadataPropertyScreenColumnOutVO> modelColumns;



}
