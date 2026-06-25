package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.NormalizedPopulation;
import com.ssafy.home.external.seoul.demographics.SeoulRawPopulation;
import org.springframework.batch.item.ItemProcessor;

public class PopulationProcessor implements ItemProcessor<SeoulRawPopulation, NormalizedPopulation> {

    @Override
    public NormalizedPopulation process(SeoulRawPopulation item) {
        String sidoName = required(item.sidoName(), "sidoName");
        String sigunguName = required(item.sigunguName(), "sigunguName");
        String dongName = required(item.dongName(), "dongName");
        String referenceDate = required(item.referenceDate(), "referenceDate");
        return new NormalizedPopulation(
                sidoName, sigunguName, dongName,
                parseOptionalInt(item.totalPopulation(), "totalPopulation"),
                parseOptionalInt(item.householdCount(), "householdCount"),
                parseOptionalInt(item.seniorCount(), "seniorCount"),
                referenceDate
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidPopulationException(field + " is required");
        }
        return value.trim();
    }

    private Integer parseOptionalInt(String value, String field) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new InvalidPopulationException(field + " is not a valid integer: " + value);
        }
    }
}
