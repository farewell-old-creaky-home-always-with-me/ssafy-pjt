package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.NormalizedForeignResident;
import com.ssafy.home.external.seoul.demographics.SeoulRawForeignResident;
import org.springframework.batch.item.ItemProcessor;

public class ForeignResidentProcessor implements ItemProcessor<SeoulRawForeignResident, NormalizedForeignResident> {

    @Override
    public NormalizedForeignResident process(SeoulRawForeignResident item) {
        String sidoName = required(item.sidoName(), "sidoName");
        String sigunguName = required(item.sigunguName(), "sigunguName");
        String dongName = required(item.dongName(), "dongName");
        String referenceDate = required(item.referenceDate(), "referenceDate");
        return new NormalizedForeignResident(
                sidoName, sigunguName, dongName,
                parseOptionalInt(item.foreignCount(), "foreignCount"),
                referenceDate
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidForeignResidentException(field + " is required");
        }
        return value.trim();
    }

    private Integer parseOptionalInt(String value, String field) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new InvalidForeignResidentException(field + " is not a valid integer: " + value);
        }
    }
}
