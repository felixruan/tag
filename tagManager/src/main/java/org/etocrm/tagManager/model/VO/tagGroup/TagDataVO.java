package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDataVO {
    @ApiModelProperty(value = "标签id")
    private Long tagId;

    @ApiModelProperty(value = "标签code")
    private Long tagCode;
}
