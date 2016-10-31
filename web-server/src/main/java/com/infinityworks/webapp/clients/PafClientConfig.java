package com.infinityworks.webapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.webapp.config.AppProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class PafClientConfig {

    @Bean
    public PafClient pafClient(Retrofit retrofit) {
        return retrofit.create(PafClient.class);
    }

    @Bean
    public OkHttpClient okHttpClient(AppProperties appProperties) {
        return new OkHttpClient.Builder()
                .hostnameVerifier((s, sslSession) -> true)
                .readTimeout(appProperties.getPafApiTimeout(), TimeUnit.SECONDS)
                .writeTimeout(appProperties.getPafApiTimeout(), TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("X-Authorization", appProperties.getPafApiToken())
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    @Bean
    public Retrofit retrofit(OkHttpClient client, ObjectMapper objectMapper, AppProperties appProperties) {
        return new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(appProperties.getPafApiBaseUrl())
                .client(client)
                .build();
    }
}
