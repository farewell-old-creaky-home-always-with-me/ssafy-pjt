package com.ssafy.home.global.config.mybatis;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class QueryLogInterceptor implements Interceptor {

    private static final long SLOW_QUERY_THRESHOLD_MS = 500;

    private final MeterRegistry meterRegistry;

    public QueryLogInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);

        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long executionTime = System.currentTimeMillis() - start;

        String mapperId = extractMapperId(mappedStatement.getId());

        Timer.builder("mybatis.query")
                .tag("mapper", mapperId)
                .register(meterRegistry)
                .record(executionTime, TimeUnit.MILLISECONDS);

        if (executionTime >= SLOW_QUERY_THRESHOLD_MS) {
            try {
                String sql = boundSql.getSql().replaceAll("\\s+", " ").trim();
                log.warn("[SQL] slow query mapper={} executionTime={}ms sql={}", mapperId, executionTime, sql);
            } catch (Exception e) {
                log.warn("[SQL Log Error] Failed to print slow query log: {}", e.getMessage());
            }
        }

        return result;
    }

    private String extractMapperId(String fullId) {
        int lastDot = fullId.lastIndexOf('.');
        if (lastDot <= 0) {
            return fullId;
        }
        int secondLastDot = fullId.lastIndexOf('.', lastDot - 1);
        return secondLastDot < 0 ? fullId.substring(lastDot + 1) : fullId.substring(secondLastDot + 1);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
