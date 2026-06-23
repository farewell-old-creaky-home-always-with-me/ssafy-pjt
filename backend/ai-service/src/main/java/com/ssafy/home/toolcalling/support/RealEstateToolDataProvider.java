package com.ssafy.home.toolcalling.support;

public interface RealEstateToolDataProvider {

    String getRegionStats(String regionName, String metric, String period);

    String searchHouses(String regionName, String houseType, Integer maxPrice);
}
