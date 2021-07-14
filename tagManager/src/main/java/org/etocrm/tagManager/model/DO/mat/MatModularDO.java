package org.etocrm.tagManager.model.DO.mat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("mat_modular_info")
public class MatModularDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 7557651261869869648L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /** 模块名：公众号(official)、小程序(app)、电商(online_shop)、CRM(crm)、导购通(shop_guide) */
    @ApiModelProperty(value = "模块名")
    private String modCode ;

    /** 模块显示名;公众号、小程序、电商、CRM、导购通 */
    @ApiModelProperty(value = "模块显示名")
    private String name ;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks ;
}
