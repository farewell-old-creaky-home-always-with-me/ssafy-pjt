package com.ssafy.home.external.housing;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OfficialHousingContentClientTest {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("공식 RSS 피드를 주택 콘텐츠로 변환한다")
    void fetchAllParsesRssFeed() throws Exception {
        // Given
        URI feedUri = startServer("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rss version="2.0">
                    <channel>
                        <item>
                            <title>주택 시장 동향</title>
                            <link>https://example.gov/news/1</link>
                            <description>시장 동향 요약</description>
                            <pubDate>Wed, 24 Jun 2026 10:00:00 GMT</pubDate>
                        </item>
                    </channel>
                </rss>
                """);
        HousingContentSourceProperties properties = new HousingContentSourceProperties(
                Duration.ofSeconds(5),
                100,
                List.of()
        );
        OfficialHousingContentClient client = new OfficialHousingContentClient(properties);
        HousingContentSourceProperties.Source source = new HousingContentSourceProperties.Source(
                "국토교통부",
                feedUri,
                false,
                "MARKET"
        );

        // When
        List<HousingRawContent> contents = client.fetchAll(List.of(source));

        // Then
        assertThat(contents).hasSize(1);
        HousingRawContent content = contents.get(0);
        assertThat(content.title()).isEqualTo("주택 시장 동향");
        assertThat(content.body()).isEqualTo("시장 동향 요약");
        assertThat(content.sourceUrl()).isEqualTo("https://example.gov/news/1");
        assertThat(content.sourceName()).isEqualTo("국토교통부");
        assertThat(content.type()).isEqualTo("MARKET");
        assertThat(content.information()).isFalse();
        assertThat(content.publishedAt()).isEqualTo(LocalDateTime.of(2026, 6, 24, 10, 0));
    }

    private URI startServer(String body) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/feed", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/rss+xml; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/feed");
    }
}
