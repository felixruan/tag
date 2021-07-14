package org.etocrm.tagManager.model.VO.utm;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Create By peter.li
 */
@ApiModel(value = "utm链接新增VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UrlLinkUpdateVO implements Serializable {

    private static final long serialVersionUID = 4821243311988821678L;
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
