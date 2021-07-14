package org.etocrm.dataManager.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@ApiModel(value = "系统配置DO " )
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_column")
public class SysColumnDO extends BasePojo implements Serializable {
    private static final long serialVersionUID = -5873338562899937134L;

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

    @ApiModelProperty(value = "字段状态" )
    private Integer columnStatus;

    @ApiModelProperty(value = "字段Value" )
    private String columnValue;


}
