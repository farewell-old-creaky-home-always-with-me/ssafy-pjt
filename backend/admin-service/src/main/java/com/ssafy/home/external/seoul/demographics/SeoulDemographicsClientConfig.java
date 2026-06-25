package com.ssafy.home.external.seoul.demographics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SeoulDemographicsProperties.class)
public class SeoulDemographicsClientConfig {

    @Bean("seoulDemographicsRestClient")
    public RestClient seoulDemographicsRestClient(
            RestClient.Builder builder,
            SeoulDemographicsProperties properties
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    @Bean
    public SeoulDemographicsClient seoulDemographicsClient(
            @Qualifier("seoulDemographicsRestClient") RestClient client,
            ObjectMapper objectMapper,
            SeoulDemographicsProperties properties
    ) {
        return new SeoulDemographicsClient(client, objectMapper, properties);
    }
}
