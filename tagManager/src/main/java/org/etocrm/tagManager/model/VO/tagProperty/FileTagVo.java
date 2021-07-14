package org.etocrm.tagManager.model.VO.tagProperty;

import lombok.Data;

import java.util.Set;

/**
 * @Author: dkx
 * @Date: 18:33 2020/12/23
 * @Desc:
 */
@Data
public class FileTagVo {

    private Set<Long> propertyIds;
    private String appId;
}
