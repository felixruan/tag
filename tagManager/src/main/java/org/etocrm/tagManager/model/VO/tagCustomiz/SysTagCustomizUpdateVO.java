package org.etocrm.tagManager.model.VO.tagCustomiz;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:55
 */
@ApiModel(value = "自定义标签添加入参")
@Data
public class SysTagCustomizUpdateVO {

    /**
     * 标签id
     */
    @NotNull(message = "标签id不得为空")
    @ApiModelProperty(value = "标签id",required = true)
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不得为空")
    @ApiModelProperty(value = "标签名称",required = true)
    private String tagName;

    /**
     * 标签定义
     */
    @NotBlank(message = "标签定义不得为空")
    @ApiModelProperty(value = "标签定义",required = true)
    private String tagMemo;

    @ApiModelProperty(value = "更新机制，0-全量覆盖，1-增量覆盖")
    private Integer updateType;

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

}
