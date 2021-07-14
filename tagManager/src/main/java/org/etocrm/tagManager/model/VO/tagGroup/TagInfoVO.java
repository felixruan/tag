package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagInfoVO implements Serializable {
    private static final long serialVersionUID = 7984210540448948704L;

    @ApiModelProperty(value = "标签id")
    private Long id;

    @ApiModelProperty(value = "标签名称")
    private String name;

    @ApiModelProperty(value = "标签code")
    private String code;
}
