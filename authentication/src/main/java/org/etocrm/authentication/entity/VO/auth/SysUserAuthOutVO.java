package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "认证统一出参VO ")
@Data
public class SysUserAuthOutVO implements Serializable {


    private static final long serialVersionUID = 2665124887109074083L;
    @ApiModelProperty(value = "登录名" )
    private String userAccount ;
    @ApiModelProperty(value = "姓名")
    private String userName;
    /*@ApiModelProperty(value = "用户英文名")
    private String userNameEn;*/
    @ApiModelProperty(value = "所属品牌id")
    private Long brandsId;
    @ApiModelProperty(value = "所属机构id")
    private Long organization;
    @ApiModelProperty(value = "所属品牌名称")
    private String brandsName;
    @ApiModelProperty(value = "所属机构名称")
    private String organizationName;

}
