package org.etocrm.tagManager.model.VO.mat;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "查询筛选属性列表出参VO ")
@Data
public class MatPropertyScreenColumnOutVO implements Serializable {

    private static final long serialVersionUID = -8861364436149764907L;
    /**
     * 属性id
     */
    @ApiModelProperty(value = "属性id")
    private Long id;

    /**
     * 属性显示名
     */
    @ApiModelProperty(value = "属性显示名")
    private String modelProperty;

    /**
     * 逻辑运算类型
     */
    @ApiModelProperty(value = "逻辑运算类型")
    private JSONArray logicalOperations;



}
