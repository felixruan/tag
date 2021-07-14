package org.etocrm.dynamicDataSource.config;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBAppenderBase;
import ch.qos.logback.core.db.DBHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class LogDBAppender extends DBAppenderBase<ILoggingEvent> {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected static final Method GET_GENERATED_KEYS_METHOD;
    //插入sql
    protected String insertSQL;

    //用户名
    static final int USERNAME_INDEX = 1;
    //level等级
    static final int LEVEL = 2  ;
    //请求接口的服务ip
    static final int SERVERIP_INDEX = 3;
    //请求地址
    static final int URL_INDEX = 4;
    //请求地址
    static final int REQUESTIP_INDEX = 5;
    //工程名
    static final int PROJECT_INDEX = 6;
    //类名
    static final int CLASS_INDEX = 7;
    //路径
    static final int CLASSPATH_INDEX = 8;
    //方法名
    static final int METHOD_INDEX = 9;
    //线程名
    static final int THREADNAME_INDEX = 10;
    //日志信息
    static final int MSG_INDEX = 11;
    //创建时间
    static final int CREATEDATE_INDEX = 12;

    static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

    static {
        Method getGeneratedKeysMethod;
        try {
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class[]) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }

    @Override
    public void start() {
        insertSQL = buildInsertSQL();
        super.start();
    }


    private static String buildInsertSQL() {
       return  "INSERT INTO logging (user,level,server_ip,url,request_ip,project,class,classpath,method,thread_name,msg,created_time)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    }

    @Override
    protected Method getGeneratedKeysMethod() {
        return GET_GENERATED_KEYS_METHOD;
    }

    @Override
    protected String getInsertSQL() {
        return insertSQL;
    }


    @Override
    protected void subAppend(ILoggingEvent eventObject, Connection connection, PreparedStatement insertStatement) throws Throwable {
        bindLoggingMyInfoWithPreparedStatement(insertStatement, eventObject);
        int updateCount = insertStatement.executeUpdate();
        if (updateCount != 1) {
            addWarn("Failed to insert loggingEvent");
        }
    }

    /**
     * 主要修改的方法
     *
     * @param stmt
     * @throws SQLException
     */
    private void bindLoggingMyInfoWithPreparedStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
        stmt.setString(LEVEL, event.getLevel().toString());
        stmt.setString(PROJECT_INDEX, event.getLoggerContextVO().getPropertyMap().get("app.name"));
        StackTraceElement caller = extractFirstCaller(event.getCallerData());
        stmt.setString(CLASS_INDEX, caller.getFileName());
        stmt.setString(CLASSPATH_INDEX, caller.getClassName());
        stmt.setString(METHOD_INDEX, caller.getMethodName());
        stmt.setString(THREADNAME_INDEX, event.getThreadName());
        stmt.setString(MSG_INDEX, event.getFormattedMessage());
        stmt.setString(CREATEDATE_INDEX, df.format(new Date()));
        stmt.setString(USERNAME_INDEX, "");
        stmt.setString(SERVERIP_INDEX, "");
        stmt.setString(URL_INDEX, "");
        stmt.setString(REQUESTIP_INDEX, "");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String account = request.getHeader("Authorization");
            String ip = getIpAddress(request);
            stmt.setString(USERNAME_INDEX, account);
            stmt.setString(SERVERIP_INDEX, ip);
            stmt.setString(URL_INDEX, request.getRequestURL().toString());
            stmt.setString(REQUESTIP_INDEX, request.getRemoteAddr());
        }
    }

    private String getIpAddress(HttpServletRequest req) {
        String ip = String.valueOf(req.getHeader("X-REQUEST-TRUE-IP"));
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "null".equals(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

    private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
        StackTraceElement caller = EMPTY_CALLER_DATA;
        if (hasAtLeastOneNonNullElement(callerDataArray))
            caller = callerDataArray[0];
        return caller;
    }

    private boolean hasAtLeastOneNonNullElement(StackTraceElement[] callerDataArray) {
        return callerDataArray != null && callerDataArray.length > 0 && callerDataArray[0] != null;
    }

    @Override
    protected void secondarySubAppend(ILoggingEvent eventObject, Connection connection, long eventId) throws Throwable {
    }

    @Override
    public void append(ILoggingEvent eventObject) {
        Connection connection = null;
        try {
            connection = connectionSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement insertStatement;
            insertStatement = connection.prepareStatement(getInsertSQL());
            // inserting an event and getting the result must be exclusive
            synchronized (this) {
                subAppend(eventObject, connection, insertStatement);
            }
            // we no longer need the insertStatement
            if (insertStatement != null) {
                insertStatement.close();
            }
            connection.commit();
        } catch (Throwable sqle) {
            addError("problem appending event", sqle);
        } finally {
            DBHelper.closeConnection(connection);
        }
    }
}