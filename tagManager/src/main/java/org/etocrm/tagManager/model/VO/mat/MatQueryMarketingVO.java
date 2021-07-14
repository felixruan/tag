package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;

@ApiModel(value = "分页查询流程营销数据VO ")
@Data
public class MatQueryMarketingVO extends TagPageInfo implements Serializable {

    private static final long serialVersionUID = 5155363507769770368L;

    /**
     * 流程id
     */
    @ApiModelProperty(value = "流程id")
    private Integer workId;


}
