package org.etocrm.dataManager.model.VO.SynchronizationConfig;

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
import org.etocrm.dataManager.util.ColumnData;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


/**
 * @author lingshuang.pang
 * @Date 2020-09-03
 */
@ApiModel(value = "系统数据同步规则 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class UpdateSysSynchronizationConfigVO implements Serializable {


    private static final long serialVersionUID = 4118606572883358113L;
    @ApiModelProperty(value = "主键")
    @NotNull(message = "主键不能为空")
    private Long id;

    @ApiModelProperty(value = "源数据源ID")
    @NotNull(message = "源数据源ID不能为空")
    private Long originDatabaseId;


    @ApiModelProperty(value = "源表名称")
    @NotBlank(message = "源表名称不能为空")
    private String originTableName;

    @ApiModelProperty(value = "源表主键")
    @NotBlank(message = "源表主键不能为空")
    private String originTablePrimaryKey;

    @ApiModelProperty(value = "目标表名称")
    @NotBlank(message = "目标表名称不能为空")
    private String destinationTableName;

    @ApiModelProperty(value = "字段匹配规则")
    @Size(min = 1,message = "规则集合不能为空")
    private List<ColumnData> columnData;


    @ApiModelProperty(value = "数据源类型 0会员库1粉丝库")
    @NotNull(message = "数据源类型不能为空")
    private Integer dataFlag;

}