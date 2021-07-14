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
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.FieldFill.*;
import static com.baomidou.mybatisplus.annotation.FieldFill.UPDATE;

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
public class SysTagGroupUserDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 3916946180175445475L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签群组ID")
    private Long tagGroupId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "分包号")
    private String subcontractNo;

}