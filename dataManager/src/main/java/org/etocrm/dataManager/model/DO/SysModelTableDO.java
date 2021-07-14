package org.etocrm.dataManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_model_table" )
public class SysModelTableDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -3088465597253122347L;
    @ApiModelProperty(value = "主键" )
    @TableId
    private Long id;

    @ApiModelProperty(value = "模型表名称" )
    private String modelTable;

    @ApiModelProperty(value = "显示名称" )
    private String modelTableName;

    @ApiModelProperty(value = "关联关系" )
    private String relationRule;

    @ApiModelProperty(value = "启用状态" )
    private Long modelStatus;

}
