package org.etocrm.tagManager.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.util.DateUtil;
import org.etocrm.tagManager.listener.TagCustomizDataListener;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagExcelReadResponseVO;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/9/17 10:50
 */
public class ExcelUtil {

    private static final String CONSTANT_UNDERLINE = "_";

    public static void noModelWrite() throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        String dateTime = DateUtil.formatDateTimeForFile(new Date());
        String userName = "test";
        String tagGroupName = "tag";
        String fileName = URLEncoder.encode(dateTime + CONSTANT_UNDERLINE + userName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream()).head(head()).sheet(tagGroupName).doWrite(dataList());
    }

    public static void noModelWrite(String fileName, String sheetName, List<List<String>> head, List<List<Object>> dataList) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        String dateTime = DateUtil.formatDateTimeForFile(new Date());
        if (StringUtils.isBlank(fileName)) {
            fileName = URLEncoder.encode(dateTime + CONSTANT_UNDERLINE, "UTF-8").replaceAll("\\+", "%20");
        } else {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream()).head(head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
    }

    public static void noModelWrite(List<List<String>> head, List<List<Object>> dataList) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        String dateTime = DateUtil.formatDateTimeForFile(new Date());
        String userName = "tagGroup";
        String tagGroupName = "人群明细";
        String fileName = URLEncoder.encode(dateTime + CONSTANT_UNDERLINE + userName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream()).head(head).sheet(tagGroupName).doWrite(dataList);
    }

    private static List<List<String>> head() {
        String[] headStr = {"分包号", "会员编号", "姓名", "性别", "生日", "会员等级", "积分余额", "注册时间", "手机号"};
        List<List<String>> headList = new ArrayList<List<String>>();
        for (String str : headStr) {
            List<String> head = new ArrayList<String>();
            head.add(str);
            headList.add(head);
        }
        return headList;
    }

    private static List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (int i = 0; i < 10; i++) {
            List<Object> data = new ArrayList<Object>();
            data.add("A" + i);
            data.add(20200916 + i);
            data.add("测试员" + i);
            if (Math.random() > 0.1) {
                data.add("男");
            } else {
                data.add("女");
            }
            data.add(new Date());
            if (Math.random() > 0.1) {
                data.add("金牌会员");
            } else {
                data.add("银牌会员");
            }
            data.add(123.456);
            data.add("1234567891234679813245678913245679");
            data.add("12345678989");
            list.add(data);
        }
        return list;
    }

    public static TagExcelReadResponseVO tagCustomizRead(MultipartFile file, ExcelSaveData excelSaveData) throws IOException {
        //监听里面保存数据
        EasyExcel.read(file.getInputStream(), new TagCustomizDataListener(excelSaveData)).sheet().doRead();
        return excelSaveData.finish();
    }
}
