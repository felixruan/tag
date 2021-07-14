package org.etocrm.tagManager.model.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: dkx
 * @Date: 14:05 2020/10/16
 * @Desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysTagUser {

    private List<SysTagPropertyUserPO> sysTagPropertyUserDOS;

    private List<SysTagPropertyWeChatUserPO> sysTagPropertyWeChatUserPOS;

    private Long tagId;

    private String tagType;
}
