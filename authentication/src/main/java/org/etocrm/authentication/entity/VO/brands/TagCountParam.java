package org.etocrm.authentication.entity.VO.brands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long brandsId;
    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 排除masterId  true 排除  false 不排除
     */
    private Boolean excludeMasterId;
}
