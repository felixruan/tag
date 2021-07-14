package org.etocrm.authentication.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysUserAllVO implements Serializable {

    private static final long serialVersionUID = -1188870916852494764L;
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    @ApiModelProperty(value = "用户名")
    private String userName;
}
