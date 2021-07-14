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
public class SysSynchronizationConfigVO implements Serializable {

    private static final long serialVersionUID = -2260056843683342819L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "源数据源ID")
    private Long originDatabaseId;

    @ApiModelProperty(value = "目标数据源ID")
    private Long destinationDatabaseId;

    @ApiModelProperty(value = "源表名称")
    private String originTableName;

    @ApiModelProperty(value = "目标表名称")
    private String destinationTableName;

    @ApiModelProperty(value = "0未执行1执行中2执行已结束")
    private Long processStatus;

    @ApiModelProperty(value = "关联主键")
    private String primaryKey;

    @ApiModelProperty(value = "字段匹配规则")
    private List<ColumnData> columnData;

    @ApiModelProperty(value = "规则说明")
    private String syncMemo;

    @ApiModelProperty(value = "启用状态")
    private Integer syncStatus;

}