package org.etocrm.tagManager.model.VO.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagBrandsInfoVO {

    @NotNull(message = "机构id不能为空")
    private Long orgId;

    @NotNull(message = "品牌id不能为空")
    private Long brandsId;
}
