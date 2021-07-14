package org.etocrm.dataManager.model.VO.dict;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Date 2020/9/17 15:48
 */
@Data
@Api(value = "添加字典VO")
public class SysDictAddVO implements Serializable {

    private static final long serialVersionUID = -7730580593683496201L;

    @ApiModelProperty(value = "字典编码" )
    private String dictCode;

    @ApiModelProperty(value = "字典名称" )
    private String dictName;

    @ApiModelProperty(value = "字典值" )
    private String dictValue;

    @ApiModelProperty(value = "字典描述" )
    private String dictMemo;

    @ApiModelProperty(value = "是否子节点" )
    private String isLeaf;

    @ApiModelProperty(value = "上级节点ID" )
    private Long dictParentId;

    @ApiModelProperty(value = "排序编号" )
    private Long orderNumber;

    @ApiModelProperty(value = "父字典编码" )
    private String dictParentCode ;
}
