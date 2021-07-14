package org.etocrm.tagManager.model.VO.tagProperty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.core.util.DateUtil;
import org.etocrm.dynamicDataSource.util.BasePojo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyEditVO  implements Serializable {

    private static final long serialVersionUID = 671318662681269076L;

    @ApiModelProperty(value = "属性id,update 时传")
    private Long id;

    @NotBlank(message = "属性名称不能为空")
    @ApiModelProperty(value = "属性名称",required = true)
    private String propertyName;

    @NotNull(message = "请选择逻辑关系")
    @ApiModelProperty(value = "逻辑运算关系id",required = true)
    private Integer ruleRelationshipId;

    @ApiModelProperty(value = "属性条件",required = true)
    @Valid
    @NotNull(message = "属性条件不能为空")
    @Size(min=1,message = "属性条件不能为空")
    private List<SysTagPropertyRuleEditVO> rule;

}