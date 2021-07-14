package org.etocrm.authentication.entity.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统用户表DO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class SysUserDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 2599153422139972233L;

    @ApiModelProperty(value = "主键" )
    @TableId
    private Integer id ;
    @ApiModelProperty(value = "用户编码" )
    private String userCode ;
    @ApiModelProperty(value = "用户账号" )
    private String userAccount ;
    @ApiModelProperty(value = "用户密码" )
    private String password ;
    @ApiModelProperty(value = "用户名" )
    private String userName ;
    @ApiModelProperty(value = "用户英文名" )
    private String userNameEn ;
    @ApiModelProperty(value = "所属品牌" )
    private Long brandsId ;
    @ApiModelProperty(value = "所属机构")
    private Long organization;
    @ApiModelProperty(value = "出生日期" )
    private String brithday ;
    @ApiModelProperty(value = "手机号" )
    private String phone ;
    @ApiModelProperty(value = "邮箱" )
    private String email ;
    @ApiModelProperty(value = "性别" )
    private String sex ;
    @ApiModelProperty(value = "最后登录时间" )
    private Date lastLoginTime ;
    @ApiModelProperty(value = "用户状态;数据字典" )
    private Integer status ;
    @ApiModelProperty(value = "单点登录id" )
    private Long ssoId;

    public SysUserDO(Integer userId)
    {
        this.id = userId;
    }
    public boolean isAdmin()
    {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Integer userId)
    {
        return userId != null && 1L == userId;
    }

}
