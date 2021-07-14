package org.etocrm.dataManager.model.VO.column;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;

@ApiModel(value = "系统配置VO " )
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysColumnPageVO extends TagPageInfo implements Serializable {
    private static final long serialVersionUID = -4628299401182148038L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字段分类id" )
    private Long classesId;

    @ApiModelProperty(value = "字段编码" )
    private String columnCode;

    @ApiModelProperty(value = "字段名称" )
    private String columnName;

    @ApiModelProperty(value = "字段描述" )
    private String columnMemo;

    @ApiModelProperty(value = "字段Value" )
    private String columnValue;

    @ApiModelProperty(value = "字段状态" )
    private Integer columnStatus;
}
