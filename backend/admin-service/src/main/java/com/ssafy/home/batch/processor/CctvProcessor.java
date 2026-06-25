package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.NormalizedCctv;
import com.ssafy.home.external.seoul.cctv.SeoulRawCctv;
import java.math.BigDecimal;
import org.springframework.batch.item.ItemProcessor;

public class CctvProcessor implements ItemProcessor<SeoulRawCctv, NormalizedCctv> {

    @Override
    public NormalizedCctv process(SeoulRawCctv item) {
        String purpose = required(item.purpose(), "purpose");
        BigDecimal latitude = parseRequiredDecimal(item.latitude(), "latitude");
        BigDecimal longitude = parseRequiredDecimal(item.longitude(), "longitude");
        Integer cameraCount = parseOptionalInt(item.cameraCount(), "cameraCount");
        String address = blankToNull(item.address());
        return new NormalizedCctv(purpose, cameraCount, address, latitude, longitude);
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidCctvException(field + " is required");
        }
        return value.trim();
    }

    private BigDecimal parseRequiredDecimal(String value, String field) {
        String normalized = required(value, field);
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            throw new InvalidCctvException(field + " is not a valid decimal: " + value);
        }
    }

    private Integer parseOptionalInt(String value, String field) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return Integer.parseInt(normalized.replace(",", "").trim());
        } catch (NumberFormatException e) {
            throw new InvalidCctvException(field + " is not a valid integer: " + value);
        }
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
