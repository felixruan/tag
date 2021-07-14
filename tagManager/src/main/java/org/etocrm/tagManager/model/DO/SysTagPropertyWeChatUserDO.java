package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.core.util.DateUtil;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_tag_property_wechat_user")
public class SysTagPropertyWeChatUserDO implements Serializable {

    private static final long serialVersionUID = -5803779886266311758L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签属性ID")
    private Long propertyId;

    @ApiModelProperty(value = "标签ID")
    private Long tagId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    private String openId;

    @JsonFormat(pattern = DateUtil.default_datemillisformat,timezone="GMT+8")
    private Date createdTime;


    @TableField(value = "is_delete")
    private Integer deleted;

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SysTagPropertyWeChatUserDO)) {
            return false;
        }
        SysTagPropertyWeChatUserDO userObj = (SysTagPropertyWeChatUserDO) obj;
        if (this == userObj) {
            return true;
        }
        if (userObj.userId.equals(this.userId)) {
            return true;
        } else {
            return false;
        }
    }

}