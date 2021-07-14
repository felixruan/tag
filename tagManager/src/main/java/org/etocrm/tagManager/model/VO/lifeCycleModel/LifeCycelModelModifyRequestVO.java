package org.etocrm.tagManager.model.VO.lifeCycleModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LifeCycelModelModifyRequestVO {

    @ApiModelProperty(value = "模型id", required = true)
    @NotNull(message = "模型id不能为空")
    private Long id;

    @ApiModelProperty(value = "机构id", required = true)
    @NotNull(message = "请选择机构")
    private Long orgId;

    @ApiModelProperty(value = "品牌id", required = true)
    @NotNull(message = "请选择品牌")
    private Long brandsId;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "请输入名称")
    @Length(max = 200, message = "名称限制长度200个字符")
    private String name;

    @ApiModelProperty(value = "更新频率，0-不更新，1-每天一次，2-每周一次，3-每月一次")
    @NotNull(message = "请选择更新频率")
    private Integer updateType;

    @ApiModelProperty(value = "每周几/每月哪天")
    private Integer updateValue;

    @ApiModelProperty(value = "规则")
    private List<LifeCycleModelRuleVO> rule;
}
