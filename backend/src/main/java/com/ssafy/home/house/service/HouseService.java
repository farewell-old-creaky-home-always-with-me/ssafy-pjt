package com.ssafy.home.house.service;

import com.ssafy.home.global.exception.ResourceNotFoundException;
import com.ssafy.home.global.exception.ValidationException;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.house.dto.HouseDealRow;
import com.ssafy.home.house.dto.HouseDetailResponse;
import com.ssafy.home.house.dto.HouseDetailRow;
import com.ssafy.home.house.dto.HouseSearchCondition;
import com.ssafy.home.house.dto.HouseSummaryResponse;
import com.ssafy.home.house.dto.HouseSummaryRow;
import com.ssafy.home.house.mapper.HouseMapper;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class HouseService {

    private static final Set<String> ALLOWED_HOUSE_TYPES = Set.of("아파트", "다세대");
    private static final Set<String> ALLOWED_DEAL_TYPES = Set.of("매매", "전세", "월세");

    private final HouseMapper houseMapper;

    public HouseService(HouseMapper houseMapper) {
        this.houseMapper = houseMapper;
    }

    public PageResponse<HouseSummaryResponse> searchHouses(
            String regionCode,
            String houseType,
            String dealType,
            Integer minAmount,
            Integer maxAmount,
            int page,
            int size
    ) {
        String normalizedRegionCode = normalizeRegionCode(regionCode);
        validateRegionCode(normalizedRegionCode);
        validateHouseType(houseType);
        validateDealType(dealType);
        validateAmounts(minAmount, maxAmount);
        validatePage(page, size);

        HouseSearchCondition condition = new HouseSearchCondition();
        condition.setRegionCode(normalizedRegionCode);
        condition.setHouseType(normalizeNullable(houseType));
        condition.setDealType(normalizeNullable(dealType));
        condition.setMinAmount(minAmount);
        condition.setMaxAmount(maxAmount);
        condition.setPage(page);
        condition.setSize(size);
        condition.setOffset((page - 1) * size);

        long total = houseMapper.countHouses(condition);
        List<HouseSummaryResponse> items = houseMapper.findHouseSummaries(condition)
                .stream()
                .map(this::toSummaryResponse)
                .toList();

        return new PageResponse<>(items, total, page, size);
    }

    public HouseDetailResponse getHouseDetail(Long houseId) {
        HouseDetailRow house = houseMapper.findHouseById(houseId);
        if (house == null) {
            throw new ResourceNotFoundException("HOUSE_NOT_FOUND", "해당 주택을 찾을 수 없습니다");
        }

        List<HouseDetailResponse.HouseDealResponse> deals = houseMapper.findHouseDealsByHouseId(houseId)
                .stream()
                .map(this::toDealResponse)
                .toList();

        return new HouseDetailResponse(
                house.getHouseId(),
                house.getAptName(),
                house.getRegionCode(),
                house.getJibun(),
                house.getBuildYear(),
                house.getHouseType(),
                house.getLatitude(),
                house.getLongitude(),
                deals
        );
    }

    private void validateRegionCode(String regionCode) {
        if (!houseMapper.existsRegionCode(regionCode)) {
            throw new ValidationException("HOUSE_INVALID_REGION", "유효하지 않은 행정구역 코드입니다");
        }
    }

    private void validateHouseType(String houseType) {
        if (houseType != null && !ALLOWED_HOUSE_TYPES.contains(houseType.trim())) {
            throw new ValidationException("COMMON_INVALID_INPUT", "유효하지 않은 주택 유형입니다");
        }
    }

    private void validateDealType(String dealType) {
        if (dealType != null && !ALLOWED_DEAL_TYPES.contains(dealType.trim())) {
            throw new ValidationException("COMMON_INVALID_INPUT", "유효하지 않은 거래 유형입니다");
        }
    }

    private void validateAmounts(Integer minAmount, Integer maxAmount) {
        if (minAmount != null && minAmount < 0) {
            throw new ValidationException("COMMON_INVALID_INPUT", "최소 금액은 0 이상이어야 합니다");
        }
        if (maxAmount != null && maxAmount < 0) {
            throw new ValidationException("COMMON_INVALID_INPUT", "최대 금액은 0 이상이어야 합니다");
        }
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            throw new ValidationException("COMMON_INVALID_INPUT", "최소 금액은 최대 금액보다 클 수 없습니다");
        }
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new ValidationException("COMMON_INVALID_INPUT", "페이지 조건이 올바르지 않습니다");
        }
    }

    private String normalizeRegionCode(String regionCode) {
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new ValidationException("HOUSE_INVALID_REGION", "행정구역 코드는 필수입니다");
        }
        String normalized = regionCode.trim();
        if (normalized.length() != 10) {
            throw new ValidationException("HOUSE_INVALID_REGION", "행정구역 코드는 10자리여야 합니다");
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }

    private HouseSummaryResponse toSummaryResponse(HouseSummaryRow row) {
        return new HouseSummaryResponse(
                row.getHouseId(),
                row.getAptName(),
                row.getJibun(),
                row.getBuildYear(),
                row.getHouseType(),
                new HouseSummaryResponse.LatestDealResponse(
                        row.getLatestDealType(),
                        row.getLatestDealAmount(),
                        row.getLatestDepositAmount(),
                        row.getLatestMonthlyRent(),
                        row.getLatestDealDate(),
                        row.getLatestArea(),
                        row.getLatestFloor()
                )
        );
    }

    private HouseDetailResponse.HouseDealResponse toDealResponse(HouseDealRow row) {
        return new HouseDetailResponse.HouseDealResponse(
                row.getDealId(),
                row.getDealType(),
                row.getDealAmount(),
                row.getDepositAmount(),
                row.getMonthlyRent(),
                row.getDealDate(),
                row.getArea(),
                row.getFloor()
        );
    }
}
