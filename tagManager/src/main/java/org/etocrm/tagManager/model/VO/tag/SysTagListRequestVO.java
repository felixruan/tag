package org.etocrm.tagManager.model.VO.tag;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.etocrm.core.util.DateUtil;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysTagListRequestVO extends TagPageInfo implements Serializable {
    private static final long serialVersionUID = 584330739968210182L;

    @ApiModelProperty(value = "标签分类id")
    private Long tagClassesId;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签状态 0-停用 1-启用")
    private Integer tagStatus;

    @ApiModelProperty(value = "标签类型 0-会员 1-粉丝")
    private String  tagType;

    @ApiModelProperty(value = "appid")
    private String  appId;

    @DateTimeFormat(pattern= DateUtil.default_dateformat)
    private Date startTime;

    @DateTimeFormat(pattern=DateUtil.default_dateformat)
    private Date endTime;
}
