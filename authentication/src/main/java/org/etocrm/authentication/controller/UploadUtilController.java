package org.etocrm.authentication.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.util.UpLoadUtil;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Api(value = "上传文件，图片模块",tags = "上传文件，图片模块")
@RefreshScope
@RestController
@RequestMapping
public class UploadUtilController {


    @ApiOperation(value = "上传图片", notes = "上传图片")
    @PostMapping("/upLoad")
    @ResponseBody
    public ResponseVO sfile(@RequestParam MultipartFile file) {
            String url= UpLoadUtil.getNewPath(file);
            if(url==null||url.equals("erro")) {
                return ResponseVO.error(ResponseEnum.MSG_FILE_FAIL);
            }else {
                return ResponseVO.success(url);
            }
    }
}
