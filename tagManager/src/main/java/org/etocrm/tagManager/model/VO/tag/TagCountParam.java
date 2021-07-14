package org.etocrm.tagManager.model.VO.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author lingshuang.pang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagCountParam implements Serializable {
    /**
     * 品牌id
     */
    @NotNull(message = "品牌id不能为空")
    private Long brandsId;
    /**
     * 机构id
     */
    @NotNull(message = "机构id不能为空")
    private Long orgId;

    private Boolean excludeMasterId;
}
