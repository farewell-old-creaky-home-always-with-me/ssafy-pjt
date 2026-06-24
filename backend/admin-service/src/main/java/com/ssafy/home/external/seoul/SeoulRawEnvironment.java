package com.ssafy.home.external.seoul;

public record SeoulRawEnvironment(
        String datasetKey,
        String category,
        String itemName,
        String value,
        String unit,
        String measuredDate,
        String latitude,
        String longitude
) {
}
