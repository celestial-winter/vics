package com.infinityworks.webapp.clients.pdfserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PdfServerClientConfig {
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 60000;

    @Bean
    public RestTemplate pdfServerClient() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(CONNECT_TIMEOUT);
        httpRequestFactory.setReadTimeout(READ_TIMEOUT);
        httpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);

        return new RestTemplate(httpRequestFactory);
    }
}
