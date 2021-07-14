package org.etocrm.dynamicDataSource.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.*;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.json.JSONUtil;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.service.IDbSourceService;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @Author chengrong.yang
 * @Date 2020/11/13 16:26
 */
@Service
@Slf4j
public class SourceDBServiceImpl implements ISourceDBService {

    @Autowired
    IDbSourceService dbSourceService;

    @Value("${ygr.db.username}")
    private String ygrDbUsername;

    @Value("${ygr.db.password}")
    private String ygrDbPassword;

    @Value("${ygr.db.url}")
    private String ygrDbUrl;

    @Value("${ygr.ssh.host}")
    private String ygrSshHost;

    @Value("${ygr.ssh.prot}")
    private Integer ygrSshPort;

    @Value("${ygr.ssh.user}")
    private String ygrSshUser;

    @Value("${ygr.ssh.password}")
    private String ygrSshPassword;

    @Value("${ygr.db.local_url}")
    private String localUrl;

    @Value("${ygr.db.lport}")
    private Integer lport;

    Session session;

    @Override
    public List getAllTable(Long id) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("SELECT table_name as tableName from information_schema.tables where table_schema = '%s'", dbSourceDO.getDbName());
        return execute(dbSourceDO, sql, new EntityListHandler());
    }

    @Override
    public List verifyTableExists(Long id, String tableName) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("SELECT table_name as columnName from information_schema.columns where table_schema='%s' and table_name='%s' ", dbSourceDO.getDbName(), tableName);
        return execute(dbSourceDO, sql, new EntityListHandler());
    }

    @Override
    @Transactional
    public List getAllColumnByTable(Long id, String tableName) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("SELECT column_name as columnName from information_schema.columns where table_schema='%s' and table_name='%s' ", dbSourceDO.getDbName(), tableName);
        return execute(dbSourceDO, sql, new EntityListHandler());
    }

    @Override
    public Integer getMaxId(Long id, String tableName, String primaryKey) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("SELECT %s from %s order by %s desc limit 1 ", primaryKey, tableName, primaryKey);
        return execute(dbSourceDO, sql, new NumberHandler()).intValue();
    }

    @Override
    public String getTablePrimaryKey(Long id, String tableName) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("SELECT column_name FROM INFORMATION_SCHEMA.`KEY_COLUMN_USAGE` WHERE table_name = '%s' AND CONSTRAINT_SCHEMA='%s' AND constraint_name='PRIMARY' ", tableName, dbSourceDO.getDbName());
        return execute(dbSourceDO, sql, new StringHandler());
    }

    @Override
    public Integer count(Long id, String tableName) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("select count(1) from %s", tableName);
        return execute(dbSourceDO, sql, new NumberHandler()).intValue();
    }

    @Override
    public List<LinkedHashMap<String, Object>> selectList(Long id, List<String> tableNames, List<String> columns, String whereClause, String orderByStr) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = createSql(tableNames, columns, whereClause, orderByStr);
        List<Entity> list = execute(dbSourceDO, sql, new EntityListHandler());
        return Convert.convert(new TypeReference<List<LinkedHashMap<String, Object>>>() {
        }, list);
    }

    @Override
    public List<LinkedHashMap<String, Object>> selectEtlListId(Long id, String tableNames, List<String> columns, int start, int end, String primaryKey) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = createEtlSql(tableNames, columns, start, end, primaryKey);
        List<Entity> list = execute(dbSourceDO, sql, new EntityListHandler());
        return Convert.convert(new TypeReference<List<LinkedHashMap<String, Object>>>() {
        }, list);
    }

    @Override
    public List verifyColumnsExists(Long id, String tableName) {
        SysDbSourceDO dbSourceDO = dbSourceService.selectSysDbSourceById(id);
        String sql = String.format("SELECT table_name as tableName, table_schema as tableSchema, column_comment as columnComment, column_name as columnName, data_type as dataType, column_type as columnType, column_key as columnKey from information_schema.columns where table_schema='%s' and table_name='%s' ", dbSourceDO.getDbName(), tableName);
        return execute(dbSourceDO, sql, new EntityListHandler());
    }

    private String createEtlSql(String tableNames, List<String> columns, int start, int end, String primaryKey) {
        if (StrUtil.isEmpty(tableNames) || CollUtil.isEmpty(columns)) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder("select ");
            for (int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i));
                if (i + 1 != columns.size()) {
                    sb.append(", ");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("from ");
            sb.append(tableNames);
            sb.append(" ").append("where ");
//            String startSql = String.format("%s >= (select %s from %s order by %s asc limit %s,1 )",primaryKey,primaryKey,tableNames,primaryKey,start);
            String startSql = String.format("%s >= %s", primaryKey, start);
            sb.append(startSql);
            if (0 != end) {
//                String endSql = String.format("AND %s < (select %s from %s order by %s asc limit %s,1 )", primaryKey, primaryKey, tableNames, primaryKey, end);
                String endSql = String.format("AND %s < %s", primaryKey, end);
                sb.append(" ").append(endSql);
            }
            String s = sb.toString();
            sb.setLength(0);
            return s;
        }
    }

    private String createSql(List<String> tableNames, List<String> columns, String whereClause, String orderByStr) {
        if (CollUtil.isEmpty(tableNames) || CollUtil.isEmpty(columns)) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder("select ");
            for (int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i));
                if (i + 1 != columns.size()) {
                    sb.append(", ");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("from ");
            for (int i = 0; i < tableNames.size(); i++) {
                sb.append(tableNames.get(i));
                if (i + 1 != tableNames.size()) {
                    sb.append(", ");
                } else {
                    sb.append(" ");
                }
            }
            if (StrUtil.isNotEmpty(whereClause)) {
                sb.append("where ").append(whereClause);
            }
            return sb.toString();
        }

    }

    public <T> T execute(SysDbSourceDO dbSourceDO, String sql, RsHandler<T> rsh) {
        String driverClassName = dbSourceDO.getDbDriverClassName();
        String url = dbSourceDO.getDbUrl();
        String username = dbSourceDO.getDbUsername();
        String password = dbSourceDO.getDbPassword();

        try (Connection con = DriverManager.getConnection(url, username, password)) {
            assert con != null;
            Class.forName(driverClassName);
            return SqlExecutor.query(con, sql, rsh);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<LinkedHashMap<String, Object>> selectYoungorListId(String tableNames, List<String> columns, int start, int end) {
        String sql = createYgrEtlSql(tableNames, columns, start, end);
        List<Entity> list = executeSession(sql, new EntityListHandler());
        return Convert.convert(new TypeReference<List<LinkedHashMap<String, Object>>>() {
        }, list);
    }

    private String createYgrEtlSql(String tableNames, List<String> columns, int start, int end) {
        if (StrUtil.isEmpty(tableNames) || CollUtil.isEmpty(columns)) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder("select ");
            for (int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i));
                if (i + 1 != columns.size()) {
                    sb.append(", ");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("from ");
            sb.append(tableNames);
            sb.append(String.format(" limit %s,%s",start,end ));

            return sb.toString();
        }
    }


    public <T> T executeSession( String sql, RsHandler<T> rsh) {
        this.getSession();
        String driverClassName = "com.mysql.cj.jdbc.Driver";

        try (Connection con = DriverManager.getConnection(localUrl, ygrDbUsername, ygrDbPassword)) {
            assert con != null;
            Class.forName(driverClassName);
            return SqlExecutor.query(con, sql, rsh);
        } catch (Exception e) {
            log.error("sql:{},e:{}",sql,e.getMessage(), e);
        }
        return null;
    }


    private  void getSession() {
        if (null == session || !session.isConnected()) {
            try {
                session = new JSch().getSession(ygrSshUser, ygrSshHost, ygrSshPort);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword(ygrSshPassword);
                session.connect();
                session.setPortForwardingL(lport, ygrDbUrl, 3306);
            } catch (JSchException e) {
                log.error("youngor get ssh session error,e:{}",e);
            }
        }
    }

    @Override
    public void closeSession() {
        if (null != session) {
            session.disconnect();
        }
    }

    public static void main(String[] args) {
        SysDbSourceDO dbSourceDO = new SysDbSourceDO();
        dbSourceDO.setDbDriverClassName("com.mysql.cj.jdbc.Driver");
        dbSourceDO.setDbUrl("jdbc:mysql://localhost:3306/eto_crm_sys?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false");
        dbSourceDO.setDbUsername("root");
        dbSourceDO.setDbPassword("root");
        dbSourceDO.setDbName("eto_crm_sys");
        SourceDBServiceImpl service = new SourceDBServiceImpl();
//        String sql = String.format("SELECT table_name as tableName from information_schema.tables where table_schema = '%s'", dbSourceDO.getDbName());
        List<String> columns = new ArrayList<String>();
        columns.add("user_id");
        columns.add("user_name");
        columns.add("full_name");
        String sql = service.createEtlSql("eto_crm_sys.sys_user", columns, 3, 6, "user_id");
        List list = service.execute(dbSourceDO, sql, new EntityListHandler());
        List<TreeMap> l = Convert.convert(new TypeReference<List<TreeMap>>(){}, list);
        System.out.println(JSONUtil.parse(l));

    }
}
