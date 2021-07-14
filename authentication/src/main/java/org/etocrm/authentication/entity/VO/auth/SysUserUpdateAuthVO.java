package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "用户角色关联VO ")
@Data
public class SysUserUpdateAuthVO implements Serializable {

    private static final long serialVersionUID = -497743501773555165L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "角色id集合")
    private List<Integer> roleIds;

}
