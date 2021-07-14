package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagClassesInfoVO implements Serializable {
    private static final long serialVersionUID = -3085843589511580064L;

    @ApiModelProperty(value = "分类名称")
    private String tagClassesName;

    @ApiModelProperty(value = "标签list")
    private List<TagInfoVO> tagList;
}
