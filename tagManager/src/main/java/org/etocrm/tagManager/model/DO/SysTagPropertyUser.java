package org.etocrm.tagManager.model.DO;

import lombok.Data;

import java.util.List;

/**
 * @Author: dkx
 * @Date: 11:17 2020/9/30
 * @Desc:
 */
@Data
public class SysTagPropertyUser {

    private List<SysTagPropertyUserPO> sysTagPropertyUserDOS;
    private Long tagId;
}
