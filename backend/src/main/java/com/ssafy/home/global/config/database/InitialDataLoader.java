package com.ssafy.home.global.config.database;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.database",
        name = "seed-enabled",
        havingValue = "true",
        matchIfMissing = true)
public class InitialDataLoader implements ApplicationRunner {

    private final DataSource dataSource;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void run(ApplicationArguments args) {
        transactionTemplate.executeWithoutResult(status -> {
            ResourceDatabasePopulator populator =
                    new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
            populator.setContinueOnError(false);
            populator.execute(dataSource);
        });
    }
}
