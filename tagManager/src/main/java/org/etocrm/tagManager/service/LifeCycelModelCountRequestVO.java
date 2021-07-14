package org.etocrm.tagManager.service;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LifeCycelModelCountRequestVO {

    @NotNull(message = "机构id不能为空")
    private Long orgId;

    @NotNull(message = "品牌id不能为空")
    private Long brandsId;
}
