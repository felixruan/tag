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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


/**
 * @author lingshuang.pang
 * @Date 2020-09-03
 */
@ApiModel(value = "系统数据同步规则 ")
@Data
public class SysSynchronizationConfigByIdVO implements Serializable {


    private static final long serialVersionUID = 7505422395443052946L;
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "源数据源名称")
    private String originDatabaseName;

//    @ApiModelProperty(value = "目标数据源名称")
//    private String destinationDatabaseName;

    @ApiModelProperty(value = "源数据源名称")
    private Long originDatabaseId;

    @ApiModelProperty(value = "目标数据源名称")
    private Long destinationDatabaseId;

    @ApiModelProperty(value = "源表名称")
    private String originTableName;

    @ApiModelProperty(value = "目标表名称")
    private String destinationTableName;

    @ApiModelProperty(value = "源表主键")
    private String originTablePrimaryKey;

    @ApiModelProperty(value = "机构ID")
    private Long orgId;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;


    @ApiModelProperty(value = "字段匹配规则")
    private List<ColumnData> columnData;

    @ApiModelProperty(value = "数据源类型 0会员库1粉丝库")
    private Integer dataFlag;


}