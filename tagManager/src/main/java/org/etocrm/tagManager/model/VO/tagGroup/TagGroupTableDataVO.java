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
public class TagGroupTableDataVO implements Serializable {

    @ApiModelProperty(value = "名称，e.g 人数")
    private String name;

    @ApiModelProperty(value = "数量")
    private List<String> value;
}
