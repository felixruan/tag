package org.etocrm.tagManager.model.VO.tagGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagGroupProcessVO {
    /**
     * 品牌id
     */
    private Long brandsId;

    /**
     * 机构id
     */
    private Long orgId;

    /**
     * 群组id
     */
    private Long tagGroupId;

    /**
     * 群组类型
     */
    private Integer tagGroupType;

    /**
     * 群组用户人群
     */
    private Set<Long> userIdSet;
}
