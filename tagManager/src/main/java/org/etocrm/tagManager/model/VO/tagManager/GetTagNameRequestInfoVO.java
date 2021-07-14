package org.etocrm.tagManager.model.VO.tagManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author lingshuang.pang
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetTagNameRequestInfoVO implements Serializable {
    private static final long serialVersionUID = 8010134517038290463L;
    /**
     * 标签id
     */
    @NotNull(message = "标签id不能为空")
    private Long id;
    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String tagName;
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


}
