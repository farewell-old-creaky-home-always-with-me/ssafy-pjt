package com.ssafy.home.house.service;

import com.ssafy.home.global.exception.CustomException;
import static com.ssafy.home.global.exception.ErrorCode.COMMON_INVALID_PAGE;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_AMOUNT_MAX;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_AMOUNT_MIN;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_AMOUNT_RANGE;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_DEAL_TYPE;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_REGION;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_REGION_LENGTH;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_REGION_REQUIRED;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_INVALID_TYPE;
import static com.ssafy.home.global.exception.ErrorCode.HOUSE_NOT_FOUND;
import lombok.RequiredArgsConstructor;
import com.ssafy.home.global.response.PageResponse;
import com.ssafy.home.house.mapper.dto.HouseDealResult;
import com.ssafy.home.house.dto.HouseDetailResponse;
import com.ssafy.home.house.mapper.dto.HouseDetailResult;
import com.ssafy.home.house.mapper.dto.HouseSearchParam;
import com.ssafy.home.house.dto.HouseSummaryResponse;
import com.ssafy.home.house.mapper.dto.HouseSummaryResult;
import com.ssafy.home.house.mapper.HouseMapper;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HouseService {

    private static final Set<String> ALLOWED_HOUSE_TYPES = Set.of("아파트", "다세대");
    private static final Set<String> ALLOWED_DEAL_TYPES = Set.of("매매", "전세", "월세");

    private final HouseMapper houseMapper;

    @Transactional(readOnly = true)
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

        HouseSearchParam condition = new HouseSearchParam();
        condition.setRegionCode(normalizedRegionCode);
        condition.setHouseType(normalizeNullable(houseType));
        condition.setDealType(normalizeNullable(dealType));
        condition.setMinAmount(minAmount);
        condition.setMaxAmount(maxAmount);
        condition.setPage(page);
        condition.setSize(size);
        condition.setOffset((page - 1) * size);

        long total = houseMapper.countBySearch(condition);
        List<HouseSummaryResponse> items = houseMapper.search(condition)
                .stream()
                .map(HouseSummaryResponse::from)
                .toList();

        return PageResponse.of(items, total, page, size);
    }

    @Transactional(readOnly = true)
    public HouseDetailResponse getHouseDetail(Long houseId) {
        HouseDetailResult house = houseMapper.findById(houseId);
        if (house == null) {
            throw new CustomException(HOUSE_NOT_FOUND);
        }

        List<HouseDetailResponse.HouseDealResponse> deals = houseMapper.findAllByHouseId(houseId)
                .stream()
                .map(HouseDetailResponse.HouseDealResponse::from)
                .toList();

        return HouseDetailResponse.from(house, deals);
    }

    private void validateRegionCode(String regionCode) {
        if (!houseMapper.existsByRegionCode(regionCode)) {
            throw new CustomException(HOUSE_INVALID_REGION);
        }
    }

    private void validateHouseType(String houseType) {
        if (houseType != null && !ALLOWED_HOUSE_TYPES.contains(houseType.trim())) {
            throw new CustomException(HOUSE_INVALID_TYPE);
        }
    }

    private void validateDealType(String dealType) {
        if (dealType != null && !ALLOWED_DEAL_TYPES.contains(dealType.trim())) {
            throw new CustomException(HOUSE_INVALID_DEAL_TYPE);
        }
    }

    private void validateAmounts(Integer minAmount, Integer maxAmount) {
        if (minAmount != null && minAmount < 0) {
            throw new CustomException(HOUSE_INVALID_AMOUNT_MIN);
        }
        if (maxAmount != null && maxAmount < 0) {
            throw new CustomException(HOUSE_INVALID_AMOUNT_MAX);
        }
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            throw new CustomException(HOUSE_INVALID_AMOUNT_RANGE);
        }
    }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CustomException(COMMON_INVALID_PAGE);
        }
    }

    private String normalizeRegionCode(String regionCode) {
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new CustomException(HOUSE_INVALID_REGION_REQUIRED);
        }
        String normalized = regionCode.trim();
        if (normalized.length() != 10) {
            throw new CustomException(HOUSE_INVALID_REGION_LENGTH);
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        return value == null ? null : value.trim();
    }


}
