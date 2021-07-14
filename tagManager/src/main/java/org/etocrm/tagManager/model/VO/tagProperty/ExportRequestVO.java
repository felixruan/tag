package org.etocrm.tagManager.model.VO.tagProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequestVO implements Serializable {

    private static final long serialVersionUID = -3899106277370610645L;

    @ApiModelProperty(value = "标签id",required = true)
    private Long tagId;

    @ApiModelProperty(value = "列名称,e.g.[\"姓名\",\"手机号\"]",required = true)
    private List<String> cols;

}
