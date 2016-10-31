package com.infinityworks.webapp.clients.gmaps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinityworks.webapp.config.AppProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class MapsClientConfig {
    @Bean
    public MapsClient mapsClient(AppProperties appProperties, ObjectMapper objectMapper) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder().addHeader("X-Authorization", appProperties.getAddressLookupToken()).build();
            return chain.proceed(request);
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(appProperties.getAddressLookupBaseUrl())
                .client(client)
                .build();

        return retrofit.create(MapsClient.class);
    }
}
