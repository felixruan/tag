package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.core.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "系统标签群组明细DO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
@TableName("sys_tag_group_user")
public class SysTagGroupUserPO implements Serializable {

    private static final long serialVersionUID = 3916946180175445475L;

    @ApiModelProperty(value = "标签群组ID")
    private Long tagGroupId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

}