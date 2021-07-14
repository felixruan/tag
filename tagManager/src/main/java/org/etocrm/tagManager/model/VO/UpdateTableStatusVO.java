package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UpdateTableStatusVO implements Serializable {


    private static final long serialVersionUID = -2117756200985983915L;
    @ApiModelProperty(value = "主键id")
    @NotNull(message = "主键不能为空")
    private Long id;

    @ApiModelProperty(value = "启用状态（0 未启用 1启用）")
    @NotNull(message = "状态不能为空")
    private Long syncStatus;
}
