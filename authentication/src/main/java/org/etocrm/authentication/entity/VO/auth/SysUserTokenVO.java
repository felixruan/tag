package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author peter.li
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "用户tokenVO ")
@Data
public class SysUserTokenVO implements Serializable {

    private static final long serialVersionUID = 295099928276451263L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "登录名")
    private String userAccount;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "所属品牌id")
    private Long brandsId;
}
