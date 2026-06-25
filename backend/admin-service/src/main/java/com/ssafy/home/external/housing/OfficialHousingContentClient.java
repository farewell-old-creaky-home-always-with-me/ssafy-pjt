package com.ssafy.home.external.housing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class OfficialHousingContentClient implements HousingContentClient {

    private final HttpClient httpClient;

    public OfficialHousingContentClient(HousingContentSourceProperties properties) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.timeout())
                .build();
    }

    @Override
    public List<HousingRawContent> fetchAll(List<HousingContentSourceProperties.Source> sources) {
        if (sources == null || sources.isEmpty()) {
            return List.of();
        }
        List<HousingRawContent> contents = new ArrayList<>();
        for (HousingContentSourceProperties.Source source : sources) {
            contents.addAll(fetch(source));
        }
        return contents;
    }

    private List<HousingRawContent> fetch(HousingContentSourceProperties.Source source) {
        if (source.url() == null) {
            return List.of();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(source.url())
                    .timeout(sourceTimeout())
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Failed to fetch housing content: " + response.statusCode());
            }
            return parse(source, response.body());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to fetch housing content from " + source.url(), exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching housing content from " + source.url(), exception);
        }
    }

    private Duration sourceTimeout() {
        return httpClient.connectTimeout().orElse(Duration.ofSeconds(10));
    }

    private List<HousingRawContent> parse(HousingContentSourceProperties.Source source, String body) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
            NodeList rssItems = document.getElementsByTagName("item");
            if (rssItems.getLength() > 0) {
                return parseRss(source, rssItems);
            }
            return parseAtom(source, document.getElementsByTagName("entry"));
        } catch (ParserConfigurationException | SAXException | IOException exception) {
            throw new IllegalStateException("Failed to parse housing content feed from " + source.url(), exception);
        }
    }

    private List<HousingRawContent> parseRss(
            HousingContentSourceProperties.Source source,
            NodeList items
    ) {
        List<HousingRawContent> contents = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            contents.add(new HousingRawContent(
                    source.information(),
                    text(item, "title"),
                    firstText(item, "description", "summary"),
                    text(item, "link"),
                    source.name(),
                    source.type(),
                    parseDate(firstText(item, "pubDate", "published", "updated"))
            ));
        }
        return contents;
    }

    private List<HousingRawContent> parseAtom(
            HousingContentSourceProperties.Source source,
            NodeList entries
    ) {
        List<HousingRawContent> contents = new ArrayList<>();
        for (int i = 0; i < entries.getLength(); i++) {
            Element entry = (Element) entries.item(i);
            contents.add(new HousingRawContent(
                    source.information(),
                    text(entry, "title"),
                    firstText(entry, "summary", "content"),
                    atomLink(entry),
                    source.name(),
                    source.type(),
                    parseDate(firstText(entry, "published", "updated"))
            ));
        }
        return contents;
    }

    private String firstText(Element element, String... tagNames) {
        for (String tagName : tagNames) {
            String value = text(element, tagName);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String text(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        Node node = nodes.item(0);
        return node.getTextContent();
    }

    private String atomLink(Element entry) {
        NodeList links = entry.getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            Element link = (Element) links.item(i);
            String href = link.getAttribute("href");
            if (href != null && !href.isBlank()) {
                return href;
            }
        }
        return text(entry, "link");
    }

    private LocalDateTime parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return ZonedDateTime.parse(trimmed, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // Try ISO-8601 below.
        }
        try {
            return OffsetDateTime.parse(trimmed).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
