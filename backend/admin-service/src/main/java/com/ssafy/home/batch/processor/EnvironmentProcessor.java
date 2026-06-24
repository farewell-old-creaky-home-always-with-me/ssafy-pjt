package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.NormalizedEnvironment;
import com.ssafy.home.external.seoul.SeoulRawEnvironment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.batch.item.ItemProcessor;

public class EnvironmentProcessor implements ItemProcessor<SeoulRawEnvironment, NormalizedEnvironment> {

    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    @Override
    public NormalizedEnvironment process(SeoulRawEnvironment item) {
        String category = required(item.category(), "category");
        String itemName = required(item.itemName(), "itemName");
        return new NormalizedEnvironment(
                category + " - " + itemName,
                parseOptionalDecimal(item.value(), "value"),
                blankToNull(item.unit()),
                parseOptionalDate(item.measuredDate()),
                parseRequiredDecimal(item.latitude(), "latitude"),
                parseRequiredDecimal(item.longitude(), "longitude")
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidEnvironmentException(field + " is required");
        }
        return value.trim();
    }

    private BigDecimal parseRequiredDecimal(String value, String field) {
        return parseDecimal(required(value, field), field);
    }

    private BigDecimal parseOptionalDecimal(String value, String field) {
        String normalized = blankToNull(value);
        return normalized == null ? null : parseDecimal(normalized, field);
    }

    private BigDecimal parseDecimal(String value, String field) {
        try {
            return new BigDecimal(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new InvalidEnvironmentException(field + " is not a valid number: " + value);
        }
    }

    private LocalDate parseOptionalDate(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            if (normalized.matches("\\d{8}")) {
                return LocalDate.parse(normalized, BASIC_DATE);
            }
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException e) {
            throw new InvalidEnvironmentException("measuredDate is not a valid date: " + value);
        }
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
