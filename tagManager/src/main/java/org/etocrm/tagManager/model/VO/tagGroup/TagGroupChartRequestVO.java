package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupChartRequestVO implements Serializable {

    private static final long serialVersionUID = 5615208957055452404L;

    @ApiModelProperty(value = "群组ids",required = true)
    @NotEmpty(message = "群组id不能为空")
    @Size(min = 1,message = "群组id不能为空")
    private List<Long> tagGroupIds;

    @NotBlank(message = "标签code不能为空")
    @ApiModelProperty(value = "标签code",required = true)
    private String tagCode;
}
