package org.etocrm.dynamicDataSource.config;


import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;

/**
 * @Author chengrong.yang
 * @date 2020/9/12 17:29
 */
@Component
@Intercepts(@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class MybatisInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String sql = getSqlByInvocation(invocation);
        if (StringUtils.isBlank(sql)) {
            return invocation.proceed();
        }
        // sql交由处理类处理  对sql语句进行处理  此处是范例  不做任何处理
        String sql2Reset = sql;
        Map<String, Object> columnMap = new HashMap<>();
        if(!sql.contains("is_delete = 0") ){
            columnMap.put("is_delete",0);
            sql2Reset = contactConditions(sql,columnMap);
            // 包装sql后，重置到invocation中
            resetSql2Invocation(invocation, sql2Reset);
        }else if(sql.contains("COUNT") && !sql.contains("is_delete = 0")){
            columnMap.put("is_delete",0);
            sql2Reset = contactConditions(sql,columnMap);
            // 包装sql后，重置到invocation中
            resetSql2Invocation(invocation, sql2Reset);
        }
        // 返回，继续执行
        return invocation.proceed();
    }

    private String getSqlByInvocation(Invocation invocation) {
        final Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        return boundSql.getSql();
    }

    private void resetSql2Invocation(Invocation invocation, String sql) throws SQLException {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = statement.getBoundSql(parameterObject);
        MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSql));
        MetaObject msObject =  MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSql.sql", sql);
        args[0] = newStatement;
    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    private static String contactConditions(String sql, Map<String, Object> columnMap) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);
        if (stmt instanceof SQLSelectStatement) {
            StringBuilder constraintsBuffer = new StringBuilder();
            Set<String> keys = columnMap.keySet();
            Iterator<String> keyIter = keys.iterator();
            if (keyIter.hasNext()) {
                String key = keyIter.next();
                constraintsBuffer.append(key).append(" = " + getSqlByClass(columnMap.get(key)));
            }
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                constraintsBuffer.append(" AND ").append(key).append(" = " + getSqlByClass(columnMap.get(key)));
            }
            SQLExprParser constraintsParser = SQLParserUtils.createExprParser(constraintsBuffer.toString(), JdbcUtils.MYSQL);
            SQLExpr constraintsExpr = constraintsParser.expr();

            SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
            // 拿到SQLSelect
            SQLSelect sqlselect = selectStmt.getSelect();
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();
            List<SQLSelectItem> list = query.getSelectList();
            for(SQLSelectItem item : list){
                if("is_delete AS deleted".equals(item.computeAlias()) || "deleted".equals(item.getAlias()) || sql.contains("COUNT")){
                    SQLExpr whereExpr = query.getWhere();
                    // 修改where表达式
                    if (whereExpr == null) {
                        query.setWhere(constraintsExpr);
                    } else {
                        SQLBinaryOpExpr newWhereExpr = new SQLBinaryOpExpr(whereExpr, SQLBinaryOperator.BooleanAnd, constraintsExpr);
                        query.setWhere(newWhereExpr);
                    }
                    sqlselect.setQuery(query);
                    return sqlselect.toString();
                }
            }
        }

        return sql;
    }

    private static String getSqlByClass(Object value){

        if(value instanceof Number){
            return value + "";
        }else if(value instanceof String){
            return "'" + value + "'";
        }

        return "'" + value.toString() + "'";
    }
}
