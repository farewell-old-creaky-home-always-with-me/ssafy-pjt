package com.ssafy.home.external.seoul;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SeoulEnvironmentProperties.class)
public class SeoulEnvironmentClientConfig {

    @Bean("seoulEnvironmentRestClient")
    public RestClient seoulEnvironmentRestClient(
            RestClient.Builder builder,
            SeoulEnvironmentProperties properties
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    @Bean
    public SeoulEnvironmentClient seoulEnvironmentClient(
            @Qualifier("seoulEnvironmentRestClient") RestClient client,
            ObjectMapper objectMapper,
            SeoulEnvironmentProperties properties
    ) {
        return new SeoulEnvironmentClient(client, objectMapper, properties);
    }
}
