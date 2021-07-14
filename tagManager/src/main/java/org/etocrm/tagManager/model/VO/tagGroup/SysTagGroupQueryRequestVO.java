package org.etocrm.tagManager.model.VO.tagGroup;

import com.baomidou.mybatisplus.annotation.TableField;
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
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

/**
 * @author lingshuang.pang
 * @Date 2020-09-14
 */
@ApiModel(value = "查询标签群组列表入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagGroupQueryRequestVO  implements Serializable {

    private static final long serialVersionUID = 4463448147232071573L;

    @ApiModelProperty(value = "群组名称")
    private String tagGroupName;

    @ApiModelProperty(value = "群组类型")
    private Integer tagGroupType;

    @DateTimeFormat(pattern=DateUtil.default_datetimeformat)
    @ApiModelProperty(value = "开始时间" )
    private Date startTime;

    @DateTimeFormat(pattern=DateUtil.default_datetimeformat)
    @ApiModelProperty(value = "结束时间" )
    private Date endTime;

    @ApiModelProperty(value = "群组状态")
    private Integer tagGroupStatus;

    @ApiModelProperty(value = "创建人员")
    private String createdByName;

    @ApiModelProperty(value = "当前页" )
    private Long current;

    @ApiModelProperty(value = "每页的数量" )
    private Long size;

}