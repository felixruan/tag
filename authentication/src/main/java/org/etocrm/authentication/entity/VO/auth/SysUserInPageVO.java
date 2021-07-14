package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "用户条件分页VO ")
@Data
public class SysUserInPageVO extends TagPageInfo implements Serializable {


    private static final long serialVersionUID = -8943507749790322885L;

    @ApiModelProperty(value = "根据姓名模糊查询")
    private String userName;


}
