package org.etocrm.dataManager.model.VO.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;

/**
 * @author dkx
 * @Date 2020-09-01
 */
@ApiModel(value = "系统字典Page VO " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysDictPageVO extends TagPageInfo implements Serializable {

    private static final long serialVersionUID = -7730580593683496201L;

    @ApiModelProperty(value = "主键" )
    private Long id;

    @ApiModelProperty(value = "字典编码" )
    private String dictCode;

    @ApiModelProperty(value = "字典名称" )
    private String dictName;

    @ApiModelProperty(value = "启用状态" )
    private Integer dictStatus;

    @ApiModelProperty(value = "上级节点ID" )
    private Long dictParentId;

    @ApiModelProperty(value = "父字典编码" )
    private String dictParentCode ;



}