package com.ssafy.home.external.vworld;

import java.util.List;

public record VworldRegionPage(
        List<VworldRawRegion> regions,
        int totalCount
) {
}
