package org.etocrm.dataManager.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.etocrm.dynamicDataSource.util.BasePojo;

/**
 * @author dkx
 * @Date 2020-09-01
 */
@ApiModel(value = "系统字典DO " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_dict")
public class SysDictDO extends BasePojo implements Serializable {


    private static final long serialVersionUID = -5153794759614775159L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字典编码" )
    private String dictCode;

    @ApiModelProperty(value = "字典名称" )
    private String dictName;

    @ApiModelProperty(value = "字典值" )
    private String dictValue;

    @ApiModelProperty(value = "启用状态" )
    private Integer dictStatus;

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