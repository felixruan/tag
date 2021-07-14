package org.etocrm.dataManager.model.VO.column;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Api(value = "添加系统配置VO")
public class SysColumnAddVO implements Serializable {

    private static final long serialVersionUID = -7900174258564508560L;
//    @ApiModelProperty(value = "字段分类id" )
//    private Long classesId;

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
//
//    @ApiModelProperty(value = "字段状态" )
//    private Integer columnStatus;
}
