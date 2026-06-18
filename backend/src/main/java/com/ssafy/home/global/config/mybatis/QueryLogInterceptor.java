package com.ssafy.home.global.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class QueryLogInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);

        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long end = System.currentTimeMillis();
        long executionTime = end - start;

        try {
            String formattedSql = getFormattedSql(mappedStatement, boundSql, parameter);
            log.info("[SQL] {} [Execution Time: {}ms]", formattedSql, executionTime);
        } catch (Exception e) {
            log.warn("[SQL Log Error] Failed to print SQL log: {}", e.getMessage());
        }

        return result;
    }

    private String getFormattedSql(MappedStatement mappedStatement, BoundSql boundSql, Object parameter) {
        String sql = boundSql.getSql();
        if (sql == null || sql.isEmpty()) {
            return "";
        }

        // Clean up whitespaces and newlines
        sql = sql.replaceAll("\\s+", " ").trim();

        if (parameter == null) {
            return sql;
        }

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            return sql;
        }

        org.apache.ibatis.session.Configuration configuration = mappedStatement.getConfiguration();
        org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        try {
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() == org.apache.ibatis.mapping.ParameterMode.OUT) {
                    continue;
                }

                String propertyName = parameterMapping.getProperty();
                Object value;

                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (typeHandlerRegistry.hasTypeHandler(parameter.getClass())) {
                    value = parameter;
                } else {
                    org.apache.ibatis.reflection.MetaObject metaObject = configuration.newMetaObject(parameter);
                    value = metaObject.getValue(propertyName);
                }

                String valueStr = getParameterValueString(value);
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(valueStr));
            }
        } catch (Exception e) {
            log.warn("[SQL Log Error] Failed to format SQL parameters: {}", e.getMessage());
            return boundSql.getSql().replaceAll("\\s+", " ").trim() + " /* Parameter binding log failed */";
        }

        return sql;
    }

    private String getParameterValueString(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
