package org.etocrm.dataManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_etl_processing_rule")
public class EtlProcessingRuleDO extends BasePojo {

    private Long id;

    private String name;

    private Integer status;

    private String paramTable;

    private String paramColumn;

    private String processingSql;

    private String targetTable;

    private String targetColumns;

    private String targetWhereColumn;

    /**
     * 更新where 条件值的来源，是 paramColumn 中的一个值
     */
    private String targetWhereColumnValueSource;

    /**
     * 一次影响一行的，updateSize set 大一点。
     * 一次影响多行的，根据行数大小判断 set updateSize
     */
    private Integer updateSize;

    private Integer querySize;

}
