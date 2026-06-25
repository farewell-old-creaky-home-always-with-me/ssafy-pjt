package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ssafy.home.batch.domain.HousingInfoType;
import com.ssafy.home.batch.domain.HousingNewsCategory;
import com.ssafy.home.batch.domain.NormalizedHousingInfo;
import com.ssafy.home.batch.domain.NormalizedHousingNews;
import com.ssafy.home.external.housing.HousingRawContent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HousingContentProcessorTest {

    private final HousingContentProcessor processor = new HousingContentProcessor();

    @Test
    @DisplayName("뉴스 원천 데이터를 정규화한다")
    void processNormalizesNews() {
        // Given
        HousingRawContent raw = raw(false, "  시장 동향  ", "  요약  ", "  https://news.example.com/1  ",
                "  국토부  ", "MARKET", LocalDateTime.of(2026, 6, 24, 9, 0));

        // When
        Object result = processor.process(raw);

        // Then
        assertThat(result).isInstanceOf(NormalizedHousingNews.class);
        NormalizedHousingNews news = (NormalizedHousingNews) result;
        assertThat(news.title()).isEqualTo("시장 동향");
        assertThat(news.summary()).isEqualTo("요약");
        assertThat(news.sourceUrl()).isEqualTo("https://news.example.com/1");
        assertThat(news.sourceName()).isEqualTo("국토부");
        assertThat(news.category()).isEqualTo(HousingNewsCategory.MARKET);
        assertThat(news.publishedAt()).isEqualTo(LocalDateTime.of(2026, 6, 24, 9, 0));
    }

    @Test
    @DisplayName("알 수 없는 뉴스 카테고리는 GENERAL로 정규화한다")
    void processUnknownNewsCategoryUsesGeneral() {
        // Given
        HousingRawContent raw = raw(false, "제목", "요약", "https://news.example.com/2",
                "기관", "UNKNOWN", null);

        // When
        Object result = processor.process(raw);

        // Then
        NormalizedHousingNews news = (NormalizedHousingNews) result;
        assertThat(news.category()).isEqualTo(HousingNewsCategory.GENERAL);
    }

    @Test
    @DisplayName("정보 원천 데이터를 정규화한다")
    void processNormalizesInfo() {
        // Given
        HousingRawContent raw = raw(true, "  생활 편의 확인법  ", "  본문  ", "  https://info.example.com/1  ",
                "  서울시  ", "LIVING", LocalDateTime.of(2026, 6, 24, 10, 0));

        // When
        Object result = processor.process(raw);

        // Then
        assertThat(result).isInstanceOf(NormalizedHousingInfo.class);
        NormalizedHousingInfo info = (NormalizedHousingInfo) result;
        assertThat(info.title()).isEqualTo("생활 편의 확인법");
        assertThat(info.content()).isEqualTo("본문");
        assertThat(info.sourceUrl()).isEqualTo("https://info.example.com/1");
        assertThat(info.sourceName()).isEqualTo("서울시");
        assertThat(info.infoType()).isEqualTo(HousingInfoType.LIVING);
        assertThat(info.publishedAt()).isEqualTo(LocalDateTime.of(2026, 6, 24, 10, 0));
    }

    @Test
    @DisplayName("알 수 없는 정보 유형은 POLICY로 정규화한다")
    void processUnknownInfoTypeUsesPolicy() {
        // Given
        HousingRawContent raw = raw(true, "제목", "본문", "https://info.example.com/2",
                "기관", "UNKNOWN", null);

        // When
        Object result = processor.process(raw);

        // Then
        NormalizedHousingInfo info = (NormalizedHousingInfo) result;
        assertThat(info.infoType()).isEqualTo(HousingInfoType.POLICY);
    }

    @Test
    @DisplayName("제목이나 출처 URL이 비어 있으면 예외를 던진다")
    void processRejectsBlankRequiredFields() {
        // Given
        HousingRawContent blankTitle = raw(false, " ", "요약", "https://news.example.com/3",
                "기관", "GENERAL", null);
        HousingRawContent blankSourceUrl = raw(false, "제목", "요약", " ",
                "기관", "GENERAL", null);

        // When / Then
        assertThatThrownBy(() -> processor.process(blankTitle))
                .isInstanceOf(InvalidHousingContentException.class);
        assertThatThrownBy(() -> processor.process(blankSourceUrl))
                .isInstanceOf(InvalidHousingContentException.class);
    }

    private HousingRawContent raw(
            boolean information,
            String title,
            String body,
            String sourceUrl,
            String sourceName,
            String type,
            LocalDateTime publishedAt
    ) {
        return new HousingRawContent(information, title, body, sourceUrl, sourceName, type, publishedAt);
    }
}
