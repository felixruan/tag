package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统用户启停VO ")
@Data
public class SysUserChangeStatusVO implements Serializable {

    private static final long serialVersionUID = 2412357773492160029L;
    @ApiModelProperty(value = "用户id" )
    @NotNull(message = "用户id不能为空")
    private Integer id ;
    @ApiModelProperty(value = "启/停状态，0：启，1：停" )
    @NotNull(message = "状态码不能为空,只能是0或1")
    private Integer status ;


}
