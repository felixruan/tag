package org.etocrm.dataManager.model.VO.column;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Api(value = "更新系统配置VO")
public class SysColumnUpdateVO implements Serializable {

    private static final long serialVersionUID = 1542455761692622635L;
    @ApiModelProperty(value = "主键id" )
    @NotNull(message = "主键不能为空")
    private Long id;

    @ApiModelProperty(value = "字段编码" )
//    @NotBlank(message = "字段编码不能为空")
    private String columnCode;

    @ApiModelProperty(value = "字段名称" )
    @NotBlank(message = "字段编码不能为空")
    private String columnName;

    @ApiModelProperty(value = "字段描述" )
    private String columnMemo;

    @ApiModelProperty(value = "字段Value" )
    @NotBlank(message = "字段Value不能为空")
    private String columnValue;
}
