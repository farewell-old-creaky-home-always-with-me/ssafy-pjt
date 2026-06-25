package com.ssafy.home.external.seoul.cctv;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SeoulCctvProperties.class)
public class SeoulCctvClientConfig {

    @Bean("seoulCctvRestClient")
    public RestClient seoulCctvRestClient(
            RestClient.Builder builder,
            SeoulCctvProperties properties
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    @Bean
    public SeoulCctvClient seoulCctvClient(
            @Qualifier("seoulCctvRestClient") RestClient client,
            ObjectMapper objectMapper,
            SeoulCctvProperties properties
    ) {
        return new SeoulCctvClient(client, objectMapper, properties);
    }
}
