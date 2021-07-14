package org.etocrm.tagManager.model.DO.mat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_user_property")
public class MetadataUserPropertyDO extends BasePojo implements Serializable {


    private static final long serialVersionUID = 8564389834305710100L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /**
     * 属性名
     */
    private String propertyCode;
    /**
     * 属性显示名
     */
    @ApiModelProperty(value = "属性名")
    private String propertyName;

    /**
     * 数据类型 必填单选 1、String   2、NUMBER  3、BOOL   4、DATETIME  5、LIST
     */
    @ApiModelProperty(value = "数据类型")
    private Integer dataType;

    /**
     * 单位/格式;统计值的单位，设置后会在分析详情和概览中显示。
     */
    @ApiModelProperty(value = "单位/格式")
    private String unit;

    /**
     * 属性值说明或示例
     */
    @ApiModelProperty(value = "属性值说明或示例")
    private String valueExplain;

    /**
     * 是否为公共属性;公共属性是每个事件都有的固定属性
     */
   /* @ApiModelProperty(value = "是否为公共属性")
    private Integer isPublic;*/

    /**
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态")
    private Integer status;

    /**
     * 上报数据;有无上报数据
     */
    @ApiModelProperty(value = "上报数据")
    private Integer isReport;
}
