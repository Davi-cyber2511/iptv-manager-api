package com.iptvmanager.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource; // Ou jakarta.sql.DataSource, dependendo do seu projeto

@Configuration
public class FlywayConfig {

    // Removemos o construtor que injetava o Environment, pois não é necessário aqui.

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .defaultSchema("public")
                .baselineOnMigrate(true) // Opcional: cria a tabela flyway_schema_history se não existir
                .load();
    }
}
