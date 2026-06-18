package com.ssafy.home.external.molit;

import java.util.List;

public record MolitHouseDealPage(List<MolitRawHouseDeal> items, int totalCount) {
    public MolitHouseDealPage {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public static MolitHouseDealPage empty() {
        return new MolitHouseDealPage(List.of(), 0);
    }
}
