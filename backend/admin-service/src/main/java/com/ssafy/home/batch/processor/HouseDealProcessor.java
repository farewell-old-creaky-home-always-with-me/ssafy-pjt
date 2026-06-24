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
            String region = resolveRegionCode(item);
            String name = required(item.name(), "name");
            String jibun = required(item.jibun(), "jibun");
            if (!mapper.existsRegionCode(region)) {
                throw new InvalidHouseDealException(
                        "UNKNOWN_REGION_CODE",
                        "Unknown region code: regionCode=" + region
                                + ", lawdCode=" + item.lawdCode()
                                + ", dongName=" + item.legalDongName()
                                + ", name=" + item.name()
                );
            }
            String amountText = required(item.dealAmount(), "dealAmount");
            if (!amountText.matches("\\d+|\\d{1,3}(,\\d{3})+")) {
                throw new InvalidHouseDealException("INVALID_AMOUNT", "Invalid deal amount");
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
            throw new InvalidHouseDealException("INVALID_DATE", "Invalid deal date", exception);
        }
    }

    private String resolveRegionCode(MolitRawHouseDeal item) {
        String region = item.legalDongCode();
        if (region != null && !region.isBlank()) {
            return region.trim();
        }
        String lawdCode = required(item.lawdCode(), "lawdCode");
        String dongName = required(item.legalDongName(), "legalDongName");
        String found = mapper.findRegionCodeByLawdCodeAndDongName(lawdCode, dongName);
        if (found == null || found.isBlank()) {
            throw new InvalidHouseDealException(
                    "UNKNOWN_REGION_CODE",
                    "Unknown region code: lawdCode=" + lawdCode
                            + ", dongName=" + dongName
                            + ", name=" + item.name()
                            + ", jibun=" + item.jibun()
            );
        }
        return found.trim();
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidHouseDealException("MISSING_REQUIRED_FIELD", field + " is required");
        }
        return value.trim();
    }

    private int integer(String value, String field) {
        try {
            return Integer.parseInt(required(value, field));
        } catch (NumberFormatException exception) {
            throw new InvalidHouseDealException(
                    "INVALID_NUMBER",
                    field + " must be an integer",
                    exception
            );
        }
    }

    private int positiveInt(String value, String field) {
        int result = integer(value, field);
        if (result <= 0) {
            throw new InvalidHouseDealException("INVALID_NUMBER", field + " must be positive");
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
                throw new InvalidHouseDealException("INVALID_AREA", field + " must be positive");
            }
            return result;
        } catch (NumberFormatException exception) {
            throw new InvalidHouseDealException(
                    "INVALID_AREA",
                    field + " must be a decimal",
                    exception
            );
        }
    }
}
