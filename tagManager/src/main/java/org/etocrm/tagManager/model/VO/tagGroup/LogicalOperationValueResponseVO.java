package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LogicalOperationValueResponseVO implements Serializable {

    private static final long serialVersionUID = 3603779534101341764L;

    @NotNull(message = "标签属性不能为空")
    @ApiModelProperty(value = "标签属性id", required = true)
    private Long tagPropertyId;

    /**
     * 考虑是否必须
     */
    @ApiModelProperty(value = "标签属性名称")
    private String tagPropertyName;
}
