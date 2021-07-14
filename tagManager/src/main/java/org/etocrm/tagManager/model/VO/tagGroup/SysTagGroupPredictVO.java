package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "新增标签群组入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupPredictVO implements Serializable {

    private static final long serialVersionUID = 2694648501660382871L;

    @NotNull(message = "群组间关系不能为空")
    @ApiModelProperty(value = "标签群组规则关系id", required = true)
    private Long tagGroupRuleRelationshipId;

    @Valid
    @Size(min = 1, message = "群组规则不能为空")
    @NotNull(message = "群组规则不能为空")
    @ApiModelProperty(value = "标签群组规则", required = true)
    private List<SysTagGroupRuleRequestVO> rule;

}