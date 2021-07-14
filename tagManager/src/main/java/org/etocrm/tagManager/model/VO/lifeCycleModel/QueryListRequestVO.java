package org.etocrm.tagManager.model.VO.lifeCycleModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;

@Data
public class QueryListRequestVO extends TagPageInfo implements Serializable {


    private static final long serialVersionUID = -5334851300059889458L;

    @ApiModelProperty(value = "名称")
    private String name;

}
