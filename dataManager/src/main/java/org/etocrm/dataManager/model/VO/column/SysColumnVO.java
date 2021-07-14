package org.etocrm.dataManager.model.VO.column;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "系统配置VO " )
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysColumnVO implements Serializable {
    private static final long serialVersionUID = -3442402627914228068L;

    @ApiModelProperty(value = "主键" )
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字段分类id" )
    private Long classesId;

    @ApiModelProperty(value = "字段编码" )
    private String columnCode;

    @ApiModelProperty(value = "字段名称" )
    private String columnName;

    @ApiModelProperty(value = "字段Value" )
    private String columnValue;

    @ApiModelProperty(value = "字段描述" )
    private String columnMemo;

    @ApiModelProperty(value = "字段状态" )
    private Integer columnStatus;

    @ApiModelProperty(value = "创建时间" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime;

    @ApiModelProperty(value = "修改时间" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updatedTime;
}
