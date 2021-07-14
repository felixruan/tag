package org.etocrm.tagManager.model.VO.tagCustomiz;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.etocrm.core.util.DateUtil;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lingshuang.pang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCustomizListRequestVO extends TagPageInfo implements Serializable {
    private static final long serialVersionUID = 4529641108471748663L;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @DateTimeFormat(pattern= DateUtil.default_dateformat)
    @ApiModelProperty(value = "开始时间,yyyy-MM-dd")
    private Date startTime;

    @DateTimeFormat(pattern=DateUtil.default_dateformat)
    @ApiModelProperty(value = "结束时间,yyyy-MM-dd")
    private Date endTime;
}
