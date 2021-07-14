package org.etocrm.tagManager.model.VO.tagGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserSaveBatchVO implements Serializable {
    private static final long serialVersionUID = -7127838951253342341L;

    /**
     * 用户id list
     */
    private Set<Long> userIdList;

    /**
     * 群组id
     */
    private Long groupId;
}
