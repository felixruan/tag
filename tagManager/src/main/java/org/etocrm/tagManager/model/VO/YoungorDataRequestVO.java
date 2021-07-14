package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class YoungorDataRequestVO {
    @ApiModelProperty(name = "标签数据列表",required = true)
    @Valid
    @NotNull(message = "至少要有一条数据")
    @Size(min = 1, message = "至少要有一条数据")
    private List<YoungorDataVO> dataList;
}
