package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupChartTagDataInfoVO {
    @ApiModelProperty(value = "名称，e.g 男/女/未知")
    private String name;

    @ApiModelProperty(value = "数量")
    private String value;

    @ApiModelProperty(value = "百分比")
    private String percent;
}
