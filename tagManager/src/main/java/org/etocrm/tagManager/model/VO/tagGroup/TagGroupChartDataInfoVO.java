package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupChartDataInfoVO {

    @ApiModelProperty(value = "群组id")
    private Long tagGroupId;

    @ApiModelProperty(value = "群组名称")
    private String tagGroupName;

    @ApiModelProperty(value = "标签属性数据")
    private List<TagGroupChartTagDataInfoVO> tagData;

}


