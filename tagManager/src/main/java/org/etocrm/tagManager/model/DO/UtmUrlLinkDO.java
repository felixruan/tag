package org.etocrm.tagManager.model.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("utm_url_link")
public class UtmUrlLinkDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -4249263899299487156L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键")
    @TableId
    private Long id;

    /**
     * 链接地址
     */
    @ApiModelProperty(value = "链接地址")
    private String linkUrl;

    /**
     * 链接显示名
     */
    @ApiModelProperty(value = "链接显示名")
    private String linkUrlName;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;


}
