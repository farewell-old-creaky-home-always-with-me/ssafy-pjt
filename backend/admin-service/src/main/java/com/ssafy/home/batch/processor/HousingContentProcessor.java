package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.HousingInfoType;
import com.ssafy.home.batch.domain.HousingNewsCategory;
import com.ssafy.home.batch.domain.NormalizedHousingContent;
import com.ssafy.home.batch.domain.NormalizedHousingInfo;
import com.ssafy.home.batch.domain.NormalizedHousingNews;
import com.ssafy.home.external.housing.HousingRawContent;
import org.springframework.batch.item.ItemProcessor;

public class HousingContentProcessor implements ItemProcessor<HousingRawContent, NormalizedHousingContent> {

    @Override
    public NormalizedHousingContent process(HousingRawContent item) {
        String title = required(item.title(), "title");
        String sourceUrl = required(item.sourceUrl(), "sourceUrl");
        String body = blankToNull(item.body());
        String sourceName = required(item.sourceName(), "sourceName");
        if (item.information()) {
            return new NormalizedHousingInfo(
                    title,
                    body,
                    sourceUrl,
                    sourceName,
                    HousingInfoType.from(item.type()),
                    item.publishedAt()
            );
        }
        return new NormalizedHousingNews(
                title,
                body,
                sourceUrl,
                sourceName,
                HousingNewsCategory.from(item.type()),
                item.publishedAt()
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidHousingContentException(field + " is required");
        }
        return value.trim();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
