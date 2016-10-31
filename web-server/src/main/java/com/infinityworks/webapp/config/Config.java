package com.infinityworks.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.filter.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Configuration
@EnableAsync
public class Config {
    public static final ObjectMapper objectMapper;
    public static final ObjectWriter objectWriter;

    static {
        objectMapper = new ObjectMapper()
                .registerModules(new JavaTimeModule(), new Jdk8Module(), new GuavaModule())
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectWriter = objectMapper.writer();
    }

    @Bean
    public Maps ukMapTopoJson() throws IOException {
        return new Maps(ImmutableMap.of("gb", load("json/wpc.json")));
    }

    private String load(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }

    @Bean
    public RestErrorHandler errorHandler() {
        return new RestErrorHandler();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PafRequestExecutor pafRequestExecutor() {
        return new PafRequestExecutor();
    }

    @Bean
    public RequestValidator requestValidator(ObjectMapper mapper) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return new RequestValidator(factory.getValidator(), mapper);
    }

    @Bean
    public CorsConfig allowedHostsForCORS(Environment env) {
        String allowedHosts = env.getRequiredProperty("canvass.cors.hosts");
        String methods = env.getRequiredProperty("canvass.cors.methods");
        Set<String> hosts = new HashSet<>(asList(allowedHosts.split(",")));
        return new CorsConfig(hosts, methods);
    }
}
