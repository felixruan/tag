package org.etocrm.tagManager.model.VO.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.etocrm.core.util.ResponseVO;

import javax.validation.constraints.NotNull;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagBrandsInfoVO {

    private ResponseVO responseVO;

    private Boolean systemFlag;

    @NotNull(message = "机构id不能为空")
    private Long orgId;

    @NotNull(message = "品牌id不能为空")
    private Long brandsId;
}
