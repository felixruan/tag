package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.tagProperty.FileTagVo;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyUserDetailPageVO;
import org.etocrm.tagManager.service.ISysTagPropertyUserService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;


@Api(value = "标签属性人群", tags = "2期有修改==== 标签属性人群 ")
@RestController
@RefreshScope
@RequestMapping("/sysTagPropertyUser")
public class SysTagPropertyUserController {

    @Resource
    private ISysTagPropertyUserService sysTagPropertyUserService;


    /**
     * 根据标签id获取人群明细列表
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "根据标签id获取人群明细列表", notes = "根据标签id获取人群明细列表")
    @GetMapping("/getUsersDetailByPage")
    public ResponseVO getUsersDetailByPage(@Valid SysTagPropertyUserDetailPageVO pageVO) {
        return sysTagPropertyUserService.getUsersDetailByPage(pageVO);
    }

    /**
     * 标签导出人群明细
     *
     * @param
     * @return
     */
    @ApiOperation(value = "2期==== 标签导出人群明细", notes = "勾选的属性加入，没有勾选不要加入，tagId属性必加：" +
            "{\n" +
            "\t\"tagId\":\"2\",\n" +
            "\t\"会员编号\":\"number\",\n" +
            "\t\"姓名\":\"name\",\n" +
            "\t\"性别\":\"gender\",\n" +
            "\t\"生日\":\"birthday\",\n" +
            "\t\"会员等级\":\"vip_level\",\n" +
            "\t\"积分余额\":\"integral\",\n" +
            "\t\"注册时间\":\"registered_time\",\n" +
            "\t\"手机号\":\"mobile\"\n" +
            "}" +
            "如果接口调用成功，会返回二进制文件流"
    )
    @GetMapping("/export")
    public ResponseVO exportUser(@RequestParam("excelJson") String excelJson) {
        return sysTagPropertyUserService.exportUser(excelJson);
    }

    @ApiOperation(value = "2期有增加====粉丝数据文件上传")
    @PostMapping("file/upload")
    public ResponseVO file(@RequestBody FileTagVo fileTagVo) {
        if (fileTagVo.getPropertyIds().isEmpty()) {
            return ResponseVO.errorParams("参数错误！");
        }
        return sysTagPropertyUserService.fileUpload(fileTagVo);
    }
}