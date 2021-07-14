package org.etocrm.tagManager.model.VO.tagProperty;

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
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyRuleVO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -1838201101029139617L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "属性ID")
    private Long propertyId;

    @ApiModelProperty(value = "业务模型表ID")
    private Long modelTableId;

    @ApiModelProperty(value = "业务模型表")
    private String modelTable;

    @ApiModelProperty(value = "业务模型表显示名称")
    private String modelTableName;

    @ApiModelProperty(value = "字段运算关系ID")
    private Integer columnRelationshipId;

    @ApiModelProperty(value = "字段信息")
    private String columns;

    @ApiModelProperty(value = "当前页" )
    private Long current;

    @ApiModelProperty(value = "每页的数量" )
    private Long size;

}