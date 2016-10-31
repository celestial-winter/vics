package com.infinityworks.webapp.clients.email;

import com.infinityworks.webapp.config.AppProperties;
import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailClientConfig {
    @Bean
    public SendGrid sendGridEmailClient(AppProperties properties) {
        return new SendGrid(properties.getSendGridKey());
    }
}
