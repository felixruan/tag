package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysUserAllVO implements Serializable {

    private static final long serialVersionUID = 1679045503218936443L;
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    @ApiModelProperty(value = "用户名")
    private String userName;
}
