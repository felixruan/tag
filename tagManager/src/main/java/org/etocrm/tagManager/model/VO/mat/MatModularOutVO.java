package org.etocrm.tagManager.model.VO.mat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MatModularOutVO implements Serializable {

    private static final long serialVersionUID = 858933031394302675L;
    /**
     * 主键id
     */
    private Long id;

    /** 模块名：公众号(official)、小程序(app)、电商(online_shop)、CRM(crm)、导购通(shop_guide) */
    private String modCode ;

    /** 模块显示名;公众号、小程序、电商、CRM、导购通 */
    private String name ;

}
