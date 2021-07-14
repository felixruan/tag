package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.tagManager.util.Relations;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.LinkedList;

@ApiModel(value = "业务模型实体类")
@Data
public class UpdateSysModelTableVO implements Serializable {
    private static final long serialVersionUID = -7736440648541369635L;

    @NotNull(message = "主键不可为空")
    @ApiModelProperty(value = "主键" )
    private Long id;

    @NotBlank(message = "模型表名称不能为空")
    @ApiModelProperty(value = "模型表名称" )
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$",message = "名称不合法(第一位是大小写字母，中间为大小写字母，数字和下划线，结尾不可为下划线，长度为2~64)")
    private String modelTable;

    @NotBlank(message = "显示名称不能为空")
    @ApiModelProperty(value = "显示名称" )
    private String modelTableName;

    @ApiModelProperty(value = "关联关系" )
    private LinkedList<Relations> relationRule;

    @ApiModelProperty(value = "启用状态" )
    private Long modelStatus;

    @NotNull(message = "模型类型不能为空")
    @ApiModelProperty(value = "模型名称类型 0会员库1粉丝库" )
    private Integer dataFlag;

}
