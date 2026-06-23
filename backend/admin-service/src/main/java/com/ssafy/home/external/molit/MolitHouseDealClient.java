package com.ssafy.home.external.molit;

import com.ssafy.home.batch.domain.HouseType;

public interface MolitHouseDealClient {
    boolean supports(HouseType houseType);
    MolitHouseDealPage fetch(String regionCode, String yearMonth, int pageNumber);
}
