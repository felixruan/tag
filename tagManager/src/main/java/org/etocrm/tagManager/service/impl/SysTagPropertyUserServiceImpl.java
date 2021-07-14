package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.mapper.ISysTagPropertyUserMapper;
import org.etocrm.tagManager.model.DO.*;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagProperty.FileTagVo;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyUserDetailPageVO;
import org.etocrm.tagManager.service.ISysTagPropertyUserService;
import org.etocrm.tagManager.util.BrandsInfoUtil;
import org.etocrm.tagManager.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 系统标签属性用户映射表  服务实现类
 * </p>
 */
@Service
@Slf4j
public class SysTagPropertyUserServiceImpl extends ServiceImpl<ISysTagPropertyUserMapper, SysTagPropertyUserDO> implements ISysTagPropertyUserService {

    @Resource
    private IDynamicService dynamicService;

    @Autowired
    private BrandsInfoUtil brandsInfoUtil;

    @Autowired
    TagGroupServiceUserImpl tagGroupUserService;

    @Autowired
    private ISysTagPropertyUserMapper iSysTagPropertyUserMapper;

    @Value("${CUSTOM.file.upload}")
    private String fileUpload;

    @Value("${CUSTOM.file.folderName}")
    private String folderName;


    /**
     * 根据标签id查询人群数量
     *
     * @param tagId
     * @return
     */
    @Override
    public Integer getPeopleCountByTagId(Long tagId) {
        List<String> tableName = new ArrayList<>();
        tableName.add("sys_tag_property_user");
        List<String> column = new ArrayList<>();
        column.add("count(distinct(user_id)) as peopleNum");
        String whereClause = " tag_id=" + tagId + " ";

        Map result = dynamicService.selectById(tableName, column, whereClause);
        return Integer.valueOf(String.valueOf(result.get("peopleNum")));
    }

    @Override
    public Integer getCountByPropertyId(Long propertyId) {
        return iSysTagPropertyUserMapper.selectCount(new LambdaQueryWrapper<SysTagPropertyUserDO>().eq(SysTagPropertyUserDO::getPropertyId, propertyId));
    }

    @Override
    public Integer deleteByTagId(Long tagId, String tagType, Integer deleteTotal) {
        log.info("此处tableName ：sys_tag_property_user   column:tag_id 已经固定了 tagType = " + tagType);
        if (tagType.equals(BusinessEnum.MEMBERS.getCode().toString())) {
            return dynamicService.deleteRecordByLimit("sys_tag_property_user", "tag_id", deleteTotal, tagId);
        } else {
            return dynamicService.deleteRecordByLimit("sys_tag_property_wechat_user", "tag_id", deleteTotal, tagId);
        }
    }

    /**
     * 标签导出人群明细
     *
     * @param excelJson
     * @return
     */
    @Override
    public ResponseVO exportUser(String excelJson) {
        try {
            TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
            if (null != brandsInfo.getResponseVO()) {
                return brandsInfo.getResponseVO();
            }

            JSONObject excelObj = JSONObject.parseObject(excelJson);
            Object tagIdObj = excelObj.get("tagId");
            if (null == tagIdObj) {
                return ResponseVO.errorParams("标签id不能为空");
            }
            //todo 检查标签是否存在 ?? 是否需要
            Long tagId = Long.parseLong(tagIdObj.toString());

            //获取生成excel的数据
            Map<String, List> map = tagGroupUserService.getUsersDetailToExecl(excelObj, BusinessEnum.TAG_EXCEL_EXPORT.getCode(), brandsInfo);

            //表标题数据
            List<List<String>> headList = (List<List<String>>) map.get("head");

            //表格数据
            List<List<Object>> dataList = (List<List<Object>>) map.get("data");

            //根据获取的数据生成excel文件
            ExcelUtil.noModelWrite(headList, dataList);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.FALL_BACK_INFO);
        }
    }

    @Override
    public ResponseVO fileUpload(FileTagVo fileTagVo) {
        try {
            List<String> tableNames = new ArrayList<>();
            tableNames.add("sys_tag_property_wechat_user");
            List<String> columnName = new ArrayList<>();
            columnName.add("open_id");
            String id = "";
            for (Long propertyId : fileTagVo.getPropertyIds()) {
                id = id + propertyId + ",";
            }
            String whereClause = "property_id in ( " + id.substring(0, id.lastIndexOf(",")) + " ) ";
            List<TreeMap> treeMaps = dynamicService.selectList(tableNames, columnName, whereClause, "");

            if (CollectionUtil.isEmpty(treeMaps)) {
                return ResponseVO.errorParams("没有粉丝数据");
            }
            List<List<Object>> dataList = new ArrayList<>();
            List<Object> list1;
            for (TreeMap treeMap : treeMaps) {
                list1 = new ArrayList<>();
                list1.add(treeMap.get("open_id"));
                dataList.add(list1);
            }

            ResponseEntity<String> entity = this.fileUpload(fileTagVo.getAppId(), dataList);
            if (200 == entity.getStatusCodeValue()) {
                log.info(entity.getBody());
                JSONObject jsonObject = JSONObject.parseObject(entity.getBody());
                Object errcode = jsonObject.get("errcode");
                if(errcode.toString().equals("0")){
                    return ResponseVO.success("上传成功！");
                }else {
                    return ResponseVO.errorParams("woaap系统" +jsonObject.get("errmsg"));
                }
            } else {
                log.error(entity.toString());
                return ResponseVO.errorParams("上传失败！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams("上传失败！");
        }
    }

    /**
     * 上传文件
     *
     * @param appid
     * @return
     * @throws IOException
     */
    private ResponseEntity<String> fileUpload(String appid, List<List<Object>> dataList) throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        String newLine = System.getProperty("line.separator");
        dataList.forEach(data -> {
            try {
                outputStream.write(String.valueOf(data.get(0)).getBytes("UTF-8"));
                outputStream.write(newLine.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        String dateTime = DateUtil.formatDateTimeForFile(new Date());
        Random random = new Random();
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
        String fileName1 = URLEncoder.encode(dateTime + rannum, "UTF-8").replaceAll("\\+", "%20");
        fileName1 = fileName1 + ".csv";
        MultipartFile file = new MockMultipartFile(fileName1, fileName1, MediaType.MULTIPART_FORM_DATA_VALUE, parse(outputStream));
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", file.getResource());
        map.add("appid", appid);
        map.add("folder_name", folderName);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        log.info("准备上传woaap系统,文件名：" + fileName1 + " 文件数据一共：" + dataList.size());
        //this.main(dataList);
        return restTemplate.postForEntity(fileUpload, request, String.class);
    }

//    public void main(List<List<Object>> dataList) throws Exception{
//        OutputStream outputStream = new ByteArrayOutputStream();
//        String newLine = System.getProperty("line.separator");
//        dataList.forEach(data -> {
//            try {
//                outputStream.write(String.valueOf(data.get(0)).getBytes());
//                outputStream.write(newLine.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        ByteArrayInputStream parse = parse(outputStream);
//        InputStream input1 = parse;
//        File file = new File("test.csv");
//        FileOutputStream fo = new FileOutputStream(file);
//        byte[] buffer = new byte[dataList.size()];
//        int bytesRead;
//        byte[] bytes =new byte[] {(byte)0xEF,(byte)0xBB,(byte)0xBF}; //utf8编码
//        fo.write(bytes);
//        while ((bytesRead = input1.read(buffer)) != -1) {
//            fo.write(buffer, 0, bytesRead);
//        }
//    }

    // outputStream转inputStream
    public ByteArrayInputStream parse(final OutputStream out) throws Exception {
        ByteArrayOutputStream baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }

    /**
     * 批量插入
     *
     * @param
     * @return
     */
    @Override
    @Async
    public void batchInsert(List<SysTagPropertyUserPO> sysTagPropertyUserDOS) {
        try {
            if (CollectionUtil.isNotEmpty(sysTagPropertyUserDOS)) {
                this.asyncInstallTagData(sysTagPropertyUserDOS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 批量插入
     *
     * @param
     * @return
     */
    @Override
    @Async
    public void batchInsertWeChat(List<SysTagPropertyWeChatUserPO> sysTagPropertyUserDOS) {
        try {
            if (CollectionUtil.isNotEmpty(sysTagPropertyUserDOS)) {
                this.asyncInstallTagDataWeChat(sysTagPropertyUserDOS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @param sysTagPropertyUserDOS
     */
    private void asyncInstallTagData(List<SysTagPropertyUserPO> sysTagPropertyUserDOS) {
        log.info("tagManager保存的标签人数 进入了异步处理");
        List<HashMap<String, Object>> hashMaps = JsonUtil.JsonToMapList(JsonUtil.toJson(sysTagPropertyUserDOS));
        Set<String> strings = hashMaps.get(0).keySet();
        String column = humpToLine2(StringUtils.join(strings, ","));
        List<String> strings1 = Arrays.asList(column.split(","));
        TableName table = SysTagPropertyUserDO.class.getAnnotation(TableName.class);
        String tableName = "";
        if (table != null) {
            tableName = table.value();
        }
        dynamicService.insertPlusRecord(tableName, strings1, hashMaps, null);
    }

    /**
     * @param sysTagPropertyUserDOS
     */
    private void asyncInstallTagDataWeChat(List<SysTagPropertyWeChatUserPO> sysTagPropertyUserDOS) {
        log.info("wechat保存的标签人数 进入了异步处理");
        List<HashMap<String, Object>> hashMaps = JsonUtil.JsonToMapList(JsonUtil.toJson(sysTagPropertyUserDOS));
        Set<String> strings = hashMaps.get(0).keySet();
        String column = humpToLine2(StringUtils.join(strings, ","));
        List<String> strings1 = Arrays.asList(column.split(","));
        TableName table = SysTagPropertyWeChatUserDO.class.getAnnotation(TableName.class);
        String tableName = "";
        if (table != null) {
            tableName = table.value();
        }
        dynamicService.insertPlusRecord(tableName, strings1, hashMaps, null);
    }

    public static String humpToLine2(String str) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取人群明细，品牌用户才有数据
     *
     * @param pageVO
     * @return
     */
    @Override
    public ResponseVO getUsersDetailByPage(SysTagPropertyUserDetailPageVO pageVO) {
        try {
            TagBrandsInfoVO dataSourceVO = brandsInfoUtil.getBrandsInfo();
            if (null != dataSourceVO.getResponseVO()) {
                return dataSourceVO.getResponseVO();
            }
            IPage page = new Page(pageVO.getSize(), pageVO.getCurrent());
            boolean masterFlag = dataSourceVO.getSystemFlag();
            if (masterFlag) {
                return ResponseVO.success(new BasePage<>(page));
            }

            List<String> tableNames = new ArrayList<String>();
            tableNames.add("sys_tag_property_user");
            tableNames.add("members");
            List<String> columns = new ArrayList<String>();
            columns.add("distinct sys_tag_property_user.user_id userId");
            columns.add("members.number number");
            columns.add("members.name name");
            columns.add("members.gender gender");
            columns.add("members.birthday birthday");
            columns.add("members.vip_level vipLevel");
            columns.add("members.integral integral");
            columns.add("members.registered_time registeredTime");
            columns.add("members.mobile mobile");
            String whereClause = "sys_tag_property_user.user_id = members.id AND sys_tag_property_user.tag_id = " + pageVO.getId();

            page = dynamicService.selectListByPage(VoParameterUtils.getSize(pageVO.getSize()),
                    VoParameterUtils.getCurrent(pageVO.getCurrent()), tableNames, columns, whereClause, null);
            BasePage basePage = new BasePage(page);

            List<HashMap> records = basePage.getRecords();
            for (HashMap map : records) {
                map.put("userId", null);

                //// TODO: 2020/9/21 性别根据模型字段设定的规则来翻译
                String sex = String.valueOf(map.get("gender"));
                if (sex.equals("1")) {
                    map.put("gender", "男");
                } else if (sex.equals("2")) {
                    map.put("gender", "女");
                } else {
                    map.put("gender", "未知");
                }

                //手机号掩码
                String mobile = String.valueOf(map.getOrDefault("mobile", ""));
                map.put("mobile", dealMobile(mobile));

                //注册时间转码
                String registeredTime = String.valueOf(map.getOrDefault("registeredTime", ""));
                if (StringUtils.isNotBlank(registeredTime)) {
                    map.put("registeredTime", registeredTime.substring(0, 10));
                }
            }

            basePage.setRecords(records);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
        }
    }

    /**
     * 手机号掩码处理
     *
     * @param mobile
     */
    private static String dealMobile(String mobile) {
        if (StringUtils.isNotBlank(mobile)) {
            mobile = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobile;
    }

}
