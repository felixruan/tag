package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_tag_property_wechat_user")
public class SysTagPropertyWeChatUserPO implements Serializable {

    private static final long serialVersionUID = -5803779886266311758L;

    //@ApiModelProperty(value = "标签属性ID")
    private Long propertyId;

   // @ApiModelProperty(value = "标签ID")
    private Long tagId;

   // @ApiModelProperty(value = "用户id")
    private Long userId;

    private String openId;

}