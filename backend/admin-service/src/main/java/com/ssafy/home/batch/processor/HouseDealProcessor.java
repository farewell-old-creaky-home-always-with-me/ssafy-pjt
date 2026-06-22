package com.ssafy.home.batch.processor;

import com.ssafy.home.batch.domain.HouseType;
import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import org.springframework.batch.item.ItemProcessor;

public class HouseDealProcessor implements ItemProcessor<MolitRawHouseDeal, NormalizedHouseDeal> {

    private final HouseDealBatchMapper mapper;
    private final HouseType houseType;

    public HouseDealProcessor(HouseDealBatchMapper mapper, HouseType houseType) {
        this.mapper = mapper;
        this.houseType = houseType;
    }

    @Override
    public NormalizedHouseDeal process(MolitRawHouseDeal item) {
        try {
            String region = required(item.legalDongCode(), "regionCode");
            String name = required(item.name(), "name");
            String jibun = required(item.jibun(), "jibun");
            if (!mapper.existsRegionCode(region)) {
                throw new InvalidHouseDealException("Unknown region code");
            }
            String amountText = required(item.dealAmount(), "dealAmount");
            if (!amountText.matches("\\d+|\\d{1,3}(,\\d{3})+")) {
                throw new InvalidHouseDealException("Invalid deal amount");
            }
            int amount = positiveInt(amountText.replace(",", ""), "dealAmount");
            BigDecimal area = positiveDecimal(item.area(), "area");
            int floor = integer(required(item.floor(), "floor"), "floor");
            Integer buildYear = optionalInteger(item.buildYear(), "buildYear");
            LocalDate date = LocalDate.of(
                    integer(item.dealYear(), "dealYear"),
                    integer(item.dealMonth(), "dealMonth"),
                    integer(item.dealDay(), "dealDay")
            );
            return new NormalizedHouseDeal(
                    region, name, jibun, buildYear, houseType.getName(),
                    "매매", amount, null, 0, date, area, floor
            );
        } catch (DateTimeException exception) {
            throw new InvalidHouseDealException("Invalid deal date", exception);
        }
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidHouseDealException(field + " is required");
        }
        return value.trim();
    }

    private int integer(String value, String field) {
        try {
            return Integer.parseInt(required(value, field));
        } catch (NumberFormatException exception) {
            throw new InvalidHouseDealException(field + " must be an integer", exception);
        }
    }

    private int positiveInt(String value, String field) {
        int result = integer(value, field);
        if (result <= 0) {
            throw new InvalidHouseDealException(field + " must be positive");
        }
        return result;
    }

    private Integer optionalInteger(String value, String field) {
        return value == null || value.isBlank() ? null : integer(value, field);
    }

    private BigDecimal positiveDecimal(String value, String field) {
        try {
            BigDecimal result = new BigDecimal(required(value, field));
            if (result.signum() <= 0) {
                throw new InvalidHouseDealException(field + " must be positive");
            }
            return result;
        } catch (NumberFormatException exception) {
            throw new InvalidHouseDealException(field + " must be a decimal", exception);
        }
    }
}
