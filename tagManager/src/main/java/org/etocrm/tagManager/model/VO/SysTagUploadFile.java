package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:55
 */
@ApiModel(value = "上传标签")
@Data
public class SysTagUploadFile {

    /**
     * 标签名称
     */
    @NotBlank(message = "文件名称不得为空")
    @ApiModelProperty(value = "标签名称",required = true)
    private String folder_name;

    /**
     * 标签定义
     */
    @NotBlank(message = "appid不得为空")
    @ApiModelProperty(value = "标签定义",required = true)
    private String appid;

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

}
