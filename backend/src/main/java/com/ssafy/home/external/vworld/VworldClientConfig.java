package com.ssafy.home.external.vworld;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(VworldProperties.class)
public class VworldClientConfig {

    @Bean("vworldRestClient")
    public RestClient vworldRestClient(
            RestClient.Builder builder,
            VworldProperties properties
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    @Bean
    public VworldLegalRegionClient vworldLegalRegionClient(
            @Qualifier("vworldRestClient") RestClient client,
            ObjectMapper objectMapper,
            VworldProperties properties
    ) {
        return new VworldLegalRegionClient(client, objectMapper, properties);
    }
}
