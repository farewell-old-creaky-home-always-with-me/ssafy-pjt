package com.ssafy.home.external.sdsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SdscProperties.class)
public class SdscClientConfig {

    @Bean("sdscRestClient")
    public RestClient sdscRestClient(
            RestClient.Builder builder,
            SdscProperties properties
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    @Bean
    public SdscStoreClient sdscStoreClient(
            @Qualifier("sdscRestClient") RestClient client,
            ObjectMapper objectMapper,
            SdscProperties properties
    ) {
        return new SdscStoreClient(client, objectMapper, properties);
    }
}
