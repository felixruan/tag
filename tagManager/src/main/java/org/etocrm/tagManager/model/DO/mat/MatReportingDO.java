package org.etocrm.tagManager.model.DO.mat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("mat_event_reporting_data")
public class MatReportingDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -3651562627828332023L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /** 事件code */
    private String eventCode ;
    /** 事件属性json */
    private String eventProperty ;
    /** 用户属性json */
    private String userProperty ;
    /** 模块code */
    private String modCode ;
    /** 模块id */
    private Long modId ;
    /** 上报时间 */
    private Date reportingTime ;
}
