package org.etocrm.dataManager.model.VO.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Date 2020/9/16 15:56
 */
@Data
@Api(value="查询字典vo")
public class DictFindAllVO {
    @ApiModelProperty(value = "主键" )
    private Long id;

    @ApiModelProperty(value = "字典编码" )
    private String dictCode;

    @ApiModelProperty(value = "字典名称" )
    private String dictName;

    @ApiModelProperty(value = "是否子节点" )
    private String isLeaf;

    @ApiModelProperty(value = "上级节点ID" )
    private Long dictParentId;

    @ApiModelProperty(value = "父字典编码" )
    private String dictParentCode ;
}
