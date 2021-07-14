package org.etocrm.dataManager.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 11:20
 */
@ApiModel(value = "数据库同步规则表对应字段实体" )
@Data
public class ColumnData implements Serializable {

    private static final long serialVersionUID = -8201126358988607120L;
    @ApiModelProperty(value = "源字段名称")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$",message = "名称不合法(第一位是大小写字母，中间为大小写字母，数字和下划线，结尾不可为下划线，长度为2~64)")
    private String originColumnName;

    @ApiModelProperty(value = "目标字段名称")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$",message = "名称不合法(第一位是大小写字母，中间为大小写字母，数字和下划线，结尾不可为下划线，长度为2~64)")
    private String destinationColumnName;

    @ApiModelProperty(value = "映射名称")
    private String displayName;

}
