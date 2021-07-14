package org.etocrm.tagManager.model.VO.tagGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "子群组覆盖信息 ")
public class SysTagGroupSonUserInfo implements Serializable {
    private static final long serialVersionUID = 632369498319539012L;

    @ApiModelProperty(value = "分组名称")
    private String name;

    @ApiModelProperty(value = "分组覆盖人数")
    private Integer count;
}
