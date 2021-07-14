package org.etocrm.dataManager.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.db.sql.SqlExecutor;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.service.DBWriterService;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DBWriterServiceImpl implements DBWriterService {

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    ISourceDBService iSourceDBService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    DataSource dataSource;

    private static final String COMMON_DATE = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter format = DateTimeFormatter.ofPattern(COMMON_DATE);

//    @Value("${ETL.MAX_NUMBER}")
//    private int MAX_NUMBER;
//
//    @Value("${ETL.DELETETOTAL}")
//    private int DELETETOTAL;


    private static final String MAX_NUMBER = "max_number";


    private static final String DELETETOTAL = "deletetotal";

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Override
    public ResponseVO dbWriter(TableData tableData) {
        StringBuilder sb = new StringBuilder();
        try {
            String tableName = tableData.getDestinationTableName();
//            Long destinationDatabaseId = tableData.getDestinationDatabaseId();
            List<LinkedHashMap<String, Object>> originData = tableData.getOriginData();
//            log.info("目标数据库：" + destinationDatabaseId);
            log.info("准备插入表名：{}", tableName);
            log.info("接收到的数据条数: {}", originData.size());
//            List<String> data = new ArrayList<>(MAX_NUMBER);
            StringJoiner joiner = new StringJoiner("),(");
            final String substring = this.packingColumn(originData);
//            ExecutorService executor = ExecutorBuilder.create()
//                    .setCorePoolSize(4)
//                    .setMaxPoolSize(8)
//                    .useSynchronousQueue()
//                    .build();
//            int flag = 0;
//            for (LinkedHashMap<String, Object> originDatum : originData) {
//                //包装data数据
////                data.add(this.packingData(sb, originDatum));
//                joiner.add(this.packingData(sb, originDatum));
//                if(flag >=2000 || flag == originData.size()-1){
//                    flag = 0;
//                    StringJoiner finalJoiner = joiner;
//                    executor.execute(() -> insertRecord(tableName, substring, finalJoiner.toString()));
//                    joiner = new StringJoiner("),(");
//                }
//                flag ++;
//            }
//            executor.shutdown();

            for (LinkedHashMap<String, Object> originDatum : originData) {
                joiner.add(this.packingData(sb, originDatum));
            }
            insertRecord(tableName, substring, joiner.toString());
//            int count = 0;
//            int flag = 0;
//            List<String> sqlList = new ArrayList<>();
//            for (LinkedHashMap<String, Object> originDatum : originData) {
//                joiner.add(this.packingData(sb, originDatum));
//                if(flag >=10000){
//                    flag = 0;
//                    count ++;
//                    sqlList.add(createSql(tableName, substring, joiner.toString()));
//                    joiner = new StringJoiner("),(");
//                }
//                flag ++;
//                if(count == 5){
//                    List<String> destList=new ArrayList<>(sqlList);
//                    sqlList.clear();
//                    count = 0;
//                    executor.execute(() -> insertRecord(destList));
//                }
//            }
//            executor.shutdown();
//            if(joiner.length()>0){
//                sqlList.add(createSql(tableName, substring, joiner.toString()));
//            }
//            if(CollUtil.isNotEmpty(sqlList)){
//                insertRecord(sqlList);
//            }


            sb.setLength(0);
            log.info("列名：{}", substring);
            log.info("分割数据: {}", originData.size());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        sb.setLength(0);
        return ResponseVO.success();
    }

    private void insertRecord(List<String> sqlList) {
        String sql = sqlList.get(0);
        try (Connection conn = DriverManager.getConnection(dbUrl,username,password); PreparedStatement ptmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            if(sqlList.size()>1){
                for(int i =1;i< sqlList.size();i++){
                    ptmt.addBatch(sqlList.get(i));
                }
            }
            ptmt.executeBatch();
            conn.commit();
            ptmt.clearBatch();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private PreparedStatement packingData(PreparedStatement ptmt, LinkedHashMap<String, Object> originDatum) {
        int i = 1;
        try {
            for (Map.Entry<String, Object> o : originDatum.entrySet()) {
                Object v = o.getValue();
                if (v instanceof String) {
                    String str = v.toString();
                    if (str.contains("\'")) {
                        str = str.replace("\'", "\'\'");
                    }
                    if (str.contains("\\")) {
                        str = str.replace("\\", "\\\\");
                    }
                    if (str.contains("\"")) {
                        str = str.replace("\"", "\\\"");
                    }
                    ptmt.setString(i, str);
                } else {
                    ptmt.setObject(i, v);
                }
                i++;
            }
            String date = DateUtil.now();
            ptmt.setObject(i,date);
            i++;
            ptmt.setObject(i,date);
            i++;
            ptmt.setObject(i,BusinessEnum.NOTDELETED.getCode());
            return ptmt;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void insertRecord(String tableName, String column, String values) {
        String sql = createSql(tableName, column, values);
//        try (Connection conn = dataSource.getConnection(); PreparedStatement ptmt = conn.prepareStatement(sql)) {
        try (Connection conn = DriverManager.getConnection(dbUrl,username,password); PreparedStatement ptmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            ptmt.addBatch();
            ptmt.executeBatch();
            conn.commit();
            ptmt.clearBatch();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createSql(String tableName, String column) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName);
        sb.append("(").append(column);
        String s = sb.toString();
        sb.setLength(0);
        return s;
    }

    private String createSql(String tableName, String column, String values) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName);
        sb.append("(").append(column).append(")");
        sb.append("values");
        sb.append("(").append(values).append(")");
        String s = sb.toString();
        sb.setLength(0);
        return s;
    }

    private String packingData(StringBuilder sb, LinkedHashMap<String, Object> originDatum) {
        sb.setLength(0);
//        originDatum.put("created_time", LocalDateTime.now().format(format));
//        originDatum.put("updated_time", LocalDateTime.now().format(format));
//        originDatum.put("is_delete", BusinessEnum.NOTDELETED.getCode());
        for (Map.Entry<String, Object> o : originDatum.entrySet()) {
            Object v = o.getValue();
            if (v instanceof String) {
                String str = v.toString();
                if (str.contains("\'")) {
                    str = str.replace("\'", "\'\'");
                }
                if (str.contains("\\")) {
                    str = str.replace("\\", "\\\\");
                }
                if (str.contains("\"")) {
                    str = str.replace("\"", "\\\"");
                }
                sb.append('\'').append(str).append('\'').append(",");
            } else {
                sb.append(v).append(",");
            }
        }
        String date = DateUtil.now();
        return sb.append("'").append(date).append("','").append(date).append("',").append(BusinessEnum.NOTDELETED.getCode()).toString();
    }

    /**
     * @param
     * @param originData
     * @return
     */
    private String packingColumn(List<LinkedHashMap<String, Object>> originData) {
        StringJoiner column = new StringJoiner(",");
        if (CollUtil.isNotEmpty(originData)) {
            LinkedHashMap<String, Object> treeMap = originData.get(0);
            for (String o : treeMap.keySet()) {
                column.add(o);
            }
            column.add("created_time").add("updated_time").add("is_delete");
        }
        String s = column.toString();
        column = null;
        return s;
    }

    /**
     * @param data
     * @param tableName
     * @param substring
     */
    private void installData(List<String> data, String tableName, String substring) {
        log.info("进入批量  tableName {} 列名 ", tableName, substring);
        int limit = countStep(data.size());
        List<List<String>> mglist = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            mglist.add(data.stream().skip(i * Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString())).limit(Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString())).collect(Collectors.toList()));
        });
        log.info("分割成几组数据: {}", mglist.size());
        for (List<String> list : mglist) {
            //log.info("保存的列数据：" + list.get(0));
            dynamicService.insertRecordBatch(tableName, substring, list);
        }
    }

    @Override
    public ResponseVO deleteTableData(TableData tableDatum) {
        // TODO: 2020/10/30 改成循环删除
//        String whereClause;
        StringBuilder sb = new StringBuilder(" org_id = ").append(tableDatum.getOrgId()).append(" and  brands_id = ").append(tableDatum.getBrandsId());
        if (org.apache.commons.lang3.StringUtils.isEmpty(tableDatum.getAppId())) {
//            whereClause = " org_id = " + tableDatum.getOrgId() + " and  brands_id = " + tableDatum.getBrandsId() + " limit " + Integer.valueOf(redisUtil.getValueByKey(DELETETOTAL).toString());
            sb.append(" limit ").append(Integer.valueOf(redisUtil.getValueByKey(DELETETOTAL).toString()));
        } else {
//            whereClause = " org_id = " + tableDatum.getOrgId() + " and  brands_id = " + tableDatum.getBrandsId() + " and  wechat_appid = '" + tableDatum.getAppId()+"'"+ " limit " + Integer.valueOf(redisUtil.getValueByKey(DELETETOTAL).toString());
            sb.append(" and  wechat_appid = '").append(tableDatum.getAppId()).append("'").append(" limit ").append(Integer.valueOf(redisUtil.getValueByKey(DELETETOTAL).toString()));
        }
        boolean deleteFlag = true;
        while (deleteFlag) {
            //批量保存之前先删除原数据
            int deleteCount = dynamicService.deleteRecord(tableDatum.getDestinationTableName(), sb.toString());
            sb.setLength(0);
            deleteFlag = deleteCount > 0;
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO getCountByTableName(String tableName) {
        int count = dynamicService.count(tableName);
        return ResponseVO.success(count);
    }

    @Override
    public Map<String, Object> getAllIdsByTableName(Long id, String tableName, String primaryKey) {
        Map<String, Object> map = new HashMap<>();
//        DynamicDataSource.setDataSource(id);
//        String primaryKey = dynamicService.getTablePrimaryKey(tableName);
        //TODO 暂时弃用
//        String primaryKey =iSourceDBService.getTablePrimaryKey(id,tableName);
        Integer maxid = 0;
        if (!StringUtils.isEmpty(primaryKey)) {
//            DynamicDataSource.setDataSource(id);
//            maxid = dynamicService.selectMaxId(tableName,primaryKey);
            maxid = iSourceDBService.getMaxId(id, tableName, primaryKey);
            map.put("key", primaryKey);
        }
        map.put("id", maxid);
        return map;
    }

    /**
     * 计算切分次数
     */
    private Integer countStep(Integer size) {
        return (size + Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString()) - 1) / Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString());
    }


}
