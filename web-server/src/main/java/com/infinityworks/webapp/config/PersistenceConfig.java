package com.infinityworks.webapp.config;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"com.infinityworks.webapp.domain"})
@EnableJpaRepositories(basePackages = {"com.infinityworks.webapp.repository"})
@EnableTransactionManagement
public class PersistenceConfig {

}
