package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupChartResponseVO implements Serializable {

    private static final long serialVersionUID = 6578888332295355845L;

    @ApiModelProperty(value = "标签id")
    private Long tagId;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签code")
    private String tagCode;

    @ApiModelProperty(value = "标签数据")
    private List<TagGroupChartDataInfoVO> dataInfo;

    @ApiModelProperty(value = "1：生命周期规则发生了变化，数据还未执行 0:正常")
    private Integer oldData;
}
