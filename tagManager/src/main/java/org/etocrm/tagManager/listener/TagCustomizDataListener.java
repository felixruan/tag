package org.etocrm.tagManager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import org.etocrm.tagManager.util.ExcelSaveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 直接用map接收数据
 *
 * @author Jiaju Zhuang
 */
public class TagCustomizDataListener extends AnalysisEventListener<Map<Integer, String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagCustomizDataListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     * 不分批，直接一次处理  如果分批，保存的逻辑要改！
     */
//    private int BATCH_COUNT;

    List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();

    private ExcelSaveData excelSaveData;


    public TagCustomizDataListener(ExcelSaveData excelSaveData) {
        this.excelSaveData = excelSaveData;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        //{0:"22222222222",1:"6",2:"6",3:"6"}
//        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        list.clear();
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        excelSaveData.saveData(list);
        LOGGER.info("存储数据库成功！");
    }

}
