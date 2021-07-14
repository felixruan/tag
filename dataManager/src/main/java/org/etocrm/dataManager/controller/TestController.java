package org.etocrm.dataManager.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.batch.impl.BatchEtlServiceImpl;
import org.etocrm.dataManager.model.VO.job.SysJobVo;
import org.etocrm.dataManager.service.IYoungorService;
import org.etocrm.dynamicDataSource.model.DO.DictionaryJsonDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.model.DO.UpdateBatchDO;
import org.etocrm.dynamicDataSource.service.DictionaryService;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;


/**
 * @Author chengrong.yang
 * @date 2020/9/3 13:54
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    BatchEtlServiceImpl batchEtlService;

    @Autowired
    RedisUtil redisUtil;

    @Resource
    DictionaryService dictionaryService;

    @Autowired
    IKafkaTopicService kafkaTopicService;

    @Autowired
    IYoungorService youngorService;

    @RequestMapping("/log")
    public String jsp() {
        return "logging";
    }

    @GetMapping("/logging")
    @ResponseBody
    public String logging() {
        List<String> ab = new ArrayList<>();
        ab.add("logging");
        List<String> c = new ArrayList<>();
        c.add("id");
        c.add("level");
        c.add("created_time as createdTime");
        c.add("project");
        c.add("msg");
        c.add("classPath");
        c.add("method");
        c.add("thread_name as threadName");
        IPage iPage = dynamicService.selectListByPage(100L, 1L, ab, c, "is_delete = 0", "created_time");
        return JSONObject.toJSONString(iPage.getRecords());
    }

    @GetMapping("selectById")
    public String selectById() {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("sys_user");
        tableNames.add("sys_permission");
        tableNames.add("sys_role");
        List<String> columns = new ArrayList<String>();
        columns.add("sys_user.id id");
        columns.add("user_code");
        columns.add("user_account");
        columns.add("role_code");
        columns.add("role_name");
        String whereClause = "sys_user.id = sys_permission.user_id AND sys_role.id = sys_permission.role_id AND sys_user.id = 1";
        Map map = dynamicService.selectById(tableNames, columns, whereClause);
        return JsonUtil.toJson(map);
    }

    @GetMapping("selectList")
    public String selectList() {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("sys_synchronization_config a");
        tableNames.add("sys_brands b");
        tableNames.add("sys_data_source c");
        tableNames.add(" sys_data_source d");
        List<String> columns = new ArrayList<String>();
        columns.add("a.id");
        columns.add("b.brands_name");
        columns.add("c.data_name AS desName");
        columns.add("d.data_name AS ascName");
        columns.add(" a.created_time");
        columns.add(" a.origin_table_name");
        columns.add(" a.destination_table_name");
        columns.add(" a.updated_time");
        columns.add(" a.process_status");
        String whereClause = " a.origin_database_id = c.id \n" +
                "        AND a.destination_database_id = d.id \n" +
                "        AND b.id = c.brands_id\n" +
                "        \n" +
                "        and a.destination_table_name like  \"member%\"\n" +
                "        and a.origin_table_name like  \"member%\"\n" +
                "        and a.destination_database_id=10\n" +
                "        and a.origin_database_id=9\n" +
                "        and c.brands_id=76";
//        String orderStr = "user_id desc";
        List<TreeMap> map = dynamicService.selectList(tableNames, columns, whereClause, null);
        return JsonUtil.toJson(map);
    }

    @GetMapping("selectListByPage")
    public String selectListByPage() {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("sys_user a");
        List<String> columns = new ArrayList<String>();
        columns.add("a.id");
        columns.add("a.user_account");
        String whereClause = "1=1";
        IPage page = dynamicService.selectListByPage(1, 1, tableNames, columns, whereClause, null);
        return JsonUtil.toJson(new BasePage(page));
    }

    @GetMapping("insertRecord")
    public String insertRecord() {
        String tableName = "t_shops";
        Map columnAndValues = new HashMap();
        columnAndValues.put("open_date", "0000-00-00");
//        columnAndValues.put("user_account", "905116842");
        String whereClause = "";
        String result = dynamicService.insertRecord(tableName, columnAndValues, whereClause);
        return result;
    }

    @GetMapping("updateRecord")
    public int updateRecord(@RequestParam String id) {
        String tableName = "sys_user";
        Map columnAndValues = new HashMap();
        columnAndValues.put("user_code", "9999999");
        columnAndValues.put("user_account", "999999");
        String whereClause = "id='" + id + "'";
        int result = dynamicService.updateRecord(tableName, columnAndValues, whereClause);
        return result;
    }

    @GetMapping("deleteRecord")
    public int deleteRecord(@RequestParam String id) {
        String tableName = "sys_user";
        String whereClause = "id='" + id + "'";
        int result = dynamicService.deleteRecord(tableName, whereClause);
        return result;
    }

    @GetMapping("selectListBySelective")
    public String selectListBySelective() {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("sys_synchronization_config a");
        tableNames.add("sys_brands b");
        tableNames.add("sys_data_source c");
        tableNames.add(" sys_data_source d");
        List<String> columns = new ArrayList<String>();
        columns.add("a.id");
        columns.add("b.brands_name");
        columns.add("c.data_name AS desName");
        columns.add("d.data_name AS ascName");
        columns.add(" a.created_time");
        columns.add(" a.origin_table_name");
        columns.add(" a.destination_table_name");
        columns.add(" a.updated_time");
        columns.add(" a.process_status");
        Map whereClause = new HashMap();
        whereClause.put("a.origin_database_id", "c.id");
        whereClause.put("a.destination_database_id", "d.id");
        whereClause.put("b.id", "c.brands_id");
        String orderStr = "user_id desc";
        HashMap whereClauseOperMap = new HashMap();
        whereClauseOperMap.put("a.destination_table_name", "like");
        whereClauseOperMap.put("a.origin_table_name", "like");
        List list = dynamicService.selectListBySelective(tableNames, columns, whereClause, orderStr, whereClauseOperMap);
        return JsonUtil.toJson(list);
    }

    @GetMapping("createTable")
    public String createTable() {
        String tableName = "test_code";
        List<String> columns = new ArrayList<String>();
        columns.add("`id` int NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY");
        columns.add("`user_code` varchar(32) DEFAULT NULL COMMENT '用户编码'");
        columns.add("`user_account` varchar(32) DEFAULT NULL COMMENT '用户账号'");
        columns.add("`password` varchar(32) DEFAULT NULL COMMENT '用户密码'");
        dynamicService.createTable(tableName, columns);
        return "success";
    }


    @GetMapping("getAllTable")
    public String getAllTable(String schema, String tableName) {
        String whereClause = "table_schema='" + schema + "' AND table_name = '" + tableName + "'";
        List<String> columns = new ArrayList<String>();
        columns.add("table_name");
        List list = dynamicService.getAllTable(columns, whereClause);
        return JsonUtil.toJson(list);
    }

    @GetMapping("getAllColumn")
    public String getAllColumn(String column) {
        String whereClause = "table_schema='eto_crm_sys' and table_name='sys_db_source' AND column_name = '" + column + "'";
        List<String> columns = new ArrayList<String>();
        columns.add("column_name");
        columns.add("data_type");
        columns.add("column_type");
        columns.add("column_key");
        List list = dynamicService.getAllColumn(columns, whereClause);
        return JsonUtil.toJson(list);
    }

//    @GetMapping("addColumn")
//    public String addColumn() {
//        String tableName = "test_code";
//        String columns = "Column1 varchar(100)";
//        dynamicService.addColumn(tableName, columns);
//        return "success";
//    }

//    @GetMapping("testETL")
//    public BatchStatus testETL(@RequestParam String str) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
////        Map<String, JobParameter> maps = new HashMap<>();
////        maps.put("time", new JobParameter(System.currentTimeMillis()));
////        maps.put("key", new JobParameter(str));
////        JobParameters parameters = new JobParameters(maps);
//////        JobExecution jobExecution = jobLauncher.run(job, parameters);
//////        while (jobExecution.isRunning()) {
//////
//////        }
////        return jobExecution.getStatus();
//        return null;
//    }


    @GetMapping("findOriginColumnNameWherePk")
    public ResponseVO findAllOriginColumnName() {
        DictionaryJsonDO dictionaryJsonDO = new DictionaryJsonDO();
        dictionaryJsonDO.setTableName("dictionary");
        List<String> list = new ArrayList<>();
        list.add("id");
        list.add("JSON_EXTRACT(json ,\"$[*].originColumnName\")");
        //list.add("json->\"$[\0]\"");
        dictionaryJsonDO.setColumns(list);
        dictionaryJsonDO.setWhereClause("where");
        HashMap map = new HashMap<>();
        map.put("id", 1);
        dictionaryJsonDO.setWhereClauseMap(map);
        return dictionaryService.findOriginColumnNameWherePk(dictionaryJsonDO);
    }

//    @GetMapping("findAllOriginColumnNameByParameterTypeLike")
//    public ResponseVO findAllOriginColumnNameByParameterTypeLike() {
//        return dictionaryService.findAllOriginColumnNameByParameterTypeLike("name");
//    }

    @GetMapping("updateJsonValueByWhere")
    public ResponseVO updateJsonValueByWhere() {
        DictionaryJsonDO dictionaryJsonDO = new DictionaryJsonDO();
        dictionaryJsonDO.setTableName("dictionary");
        dictionaryJsonDO.setColumnName("json");
        Map map1 = new HashMap<>();
        List<Map> list = new ArrayList<>();
        map1.put("originColumnName", "Email");
        list.add(map1);
        dictionaryJsonDO.setColumnNameValue(JSONObject.toJSONString(list));
        dictionaryJsonDO.setWhereClause("where");
        HashMap map = new HashMap<>();
        map.put("id", 1);
        dictionaryJsonDO.setWhereClauseMap(map);
        return dictionaryService.updateJsonValueByWhere(dictionaryJsonDO);
    }

    @GetMapping("updateOriginColumnNameByPk")
    public ResponseVO updateOriginColumnNameByPk() {
        DictionaryJsonDO dictionaryJsonDO = new DictionaryJsonDO();
        dictionaryJsonDO.setTableName("dictionary");
        dictionaryJsonDO.setColumnName("json");
        dictionaryJsonDO.setColumnNameValue("\"$[0].originColumnName\"");
        dictionaryJsonDO.setWhereClause("where");
        HashMap map = new HashMap<>();
        map.put("id", 1);
        dictionaryJsonDO.setWhereClauseMap(map);
        dictionaryJsonDO.setNewColumnNameValue("name");
        return dictionaryService.updateOriginColumnNameByPk(dictionaryJsonDO);
    }

    @GetMapping("deleteOriginColumnNameByPk")
    public ResponseVO deleteOriginColumnNameByPk() {
        DictionaryJsonDO dictionaryJsonDO = new DictionaryJsonDO();
        dictionaryJsonDO.setTableName("dictionary");
        dictionaryJsonDO.setColumnName("json");
        dictionaryJsonDO.setColumnNameValue("\"$[0].originColumnName\"");
        dictionaryJsonDO.setWhereClause("where");
        HashMap map = new HashMap<>();
        map.put("id", 1);
        dictionaryJsonDO.setWhereClauseMap(map);
        return dictionaryService.deleteOriginColumnNameByPk(dictionaryJsonDO);
    }

    @GetMapping("/testDB")
    @ResponseBody
    public String tsetDB() {
        List<String> ab = new ArrayList<>();
        ab.add("t_shops");

        List<String> c = new ArrayList<>();
        c.add("open_date as open_date");
        c.add("id as shop_id");
//        c.add("open_date as open_date");
//        c.add("open_date as open_date");
//        , id as shop_id, address as address, province as province, city as city, shop_code as shop_code, district as district, name as name, brand_name as brand_name, type as type, status as status, brand_id as brand_id");

        List<TreeMap> a = dynamicService.selectList(ab, c, "brand_id=16", "id");
        return JSONObject.toJSONString(a);
    }

    @GetMapping("tetVo")
    public String testVo(@Valid SysJobVo vo) {
        return vo.toString();
    }

    @GetMapping("testRedis")
    public String testRedis(String param) {
        List<SysDbSourceDO> list = new ArrayList<SysDbSourceDO>();
        SysDbSourceDO d1 = new SysDbSourceDO();
        d1.setId(1L);
        SysDbSourceDO d2 = new SysDbSourceDO();
        d1.setId(2L);
        SysDbSourceDO d3 = new SysDbSourceDO();
        d1.setId(3L);
        list.add(d1);
        list.add(d2);
        list.add(d3);
        switch (param) {
            case "add":
                redisUtil.publish("dataBasic_add", list);
                break;
            case "update":
                redisUtil.publish("dataBasic_update", list);
                break;
            case "del":
                redisUtil.publish("dataBasic_del", list);
                break;
            default:
                break;
        }
        return "success";
    }


    @GetMapping("testKafka")
    public String testKafka() {
        batchEtlService.run();
        return "success";
    }

    @GetMapping("createTopic")
    public boolean createTopic(String topicName, int partions,
                               short broker) {
        boolean topic = kafkaTopicService.createTopic(topicName, partions, broker);
        return topic;
    }

    @GetMapping("/getAllTopic")
    public Set<String> getAllTopic() {
        Set<String> allTopic = kafkaTopicService.getAllTopic();
        return allTopic;
    }

    @GetMapping("/deleteTopic")
    public boolean deleteTopic(String topicName) {
        boolean b = kafkaTopicService.deleteTopic(topicName);
        return b;
    }


    @GetMapping("/updateBatch")
    public String updateBatch() {
        UpdateBatchDO batchDO = new UpdateBatchDO();
        batchDO.setTableName("members");
        batchDO.setOrgId(1L);
        batchDO.setBrandsId(2L);
        batchDO.setWhereCol("name");
        List<String> colNameList = new ArrayList<>();
        colNameList.add("name");
        colNameList.add("age");
        batchDO.setUpdateColNameList(colNameList);
        Map map = new HashMap();
        map.put("id", 387063);
        map.put("name", "pls1");
        map.put("age", 2);
        List<Map> data = new ArrayList<>();
        data.add(map);
        batchDO.setDataList(data);

        dynamicService.updateBatch(batchDO);
        return "success";
    }


    @GetMapping("/saveYgrTag")
    public ResponseVO saveYgrTag(Integer count, Integer start, Integer size) {
        youngorService.save(count, start, size);
        return ResponseVO.success();
    }
}
