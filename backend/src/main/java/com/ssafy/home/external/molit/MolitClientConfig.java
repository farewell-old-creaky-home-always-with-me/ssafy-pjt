package com.ssafy.home.external.molit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MolitProperties.class)
public class MolitClientConfig {

    @Bean("molitRestClient")
    public RestClient molitRestClient(
            RestClient.Builder builder,
            MolitProperties properties
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeout());
        factory.setReadTimeout(properties.timeout());
        return builder.requestFactory(factory).build();
    }

    @Bean
    public ApartmentSaleClient apartmentSaleClient(
            @Qualifier("molitRestClient") RestClient client,
            ObjectMapper objectMapper,
            MolitProperties properties
    ) {
        return new ApartmentSaleClient(client, objectMapper, properties);
    }

    @Bean
    public MultiFamilySaleClient multiFamilySaleClient(
            @Qualifier("molitRestClient") RestClient client,
            ObjectMapper objectMapper,
            MolitProperties properties
    ) {
        return new MultiFamilySaleClient(client, objectMapper, properties);
    }
}
