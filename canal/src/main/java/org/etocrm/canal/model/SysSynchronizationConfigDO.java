package org.etocrm.canal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;


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
@TableName("sys_synchronization_config")
public class SysSynchronizationConfigDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -5344947018582987937L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "源数据源ID")
    private Long originDatabaseId;

    @ApiModelProperty(value = "目标数据源ID")
    private Long destinationDatabaseId;

    @ApiModelProperty(value = "机构ID")
    private Long orgId;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "源表名称")
    private String originTableName;

    @ApiModelProperty(value = "目标表名称")
    private String destinationTableName;

    @ApiModelProperty(value = "0未执行1执行中2执行已结束")
    private Long processStatus;

    @ApiModelProperty(value = "关联主键")
    private String primaryKey;

    @ApiModelProperty(value = "字段匹配规则")
    private String columnData;

    @ApiModelProperty(value = "规则说明")
    private String syncMemo;

    @ApiModelProperty(value = "启用状态")
    private Integer syncStatus;

    @ApiModelProperty(value = "源表主键")
    private String originTablePrimaryKey;

    @ApiModelProperty(value = "appid")
    private String appId;

}