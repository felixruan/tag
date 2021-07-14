package org.etocrm.dynamicDataSource.model.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "统一用户权限认证出参VO ")
@Data
public class UniteUserAuthOutVO implements Serializable {

    private static final long serialVersionUID = 4255147288445553300L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    @ApiModelProperty(value = "所属品牌")
    private Long brandsId;

    @ApiModelProperty(value = "所属机构")
    private Long orgId;

}
