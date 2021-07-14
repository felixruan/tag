package org.etocrm.tagManager.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 11:20
 */
@ApiModel(value = "数据库同步规则表对应字段实体" )
@Data
public class ColumnData {

    @ApiModelProperty(value = "源字段名称")
    private String originColumnName;

    @ApiModelProperty(value = "目标字段名称")
    private String destinationColumnName;

    @ApiModelProperty(value = "映射名称")
    private String displayName;

}
