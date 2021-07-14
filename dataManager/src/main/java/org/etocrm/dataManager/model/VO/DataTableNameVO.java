package org.etocrm.dataManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class DataTableNameVO implements Serializable {
    private static final long serialVersionUID = -4017847876726633343L;

    @ApiModelProperty(value = "数据源ID")
    @NotNull(message = "数据源ID不可为空")
    private Long id;

    @ApiModelProperty(value = "数据表名称")
    @NotBlank(message = "数据表名称不可为空")
    private String tableName;
}
