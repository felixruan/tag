package org.etocrm.tagManager.model.VO.tagCustomiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCustomizSaveUserVO {
    private Long orgId;
    private Long brandsId;
    private Long tagId;
    private Long propertyId;
    //更新类型
    private Integer updateType;
}
