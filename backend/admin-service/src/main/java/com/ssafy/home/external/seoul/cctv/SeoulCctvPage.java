package com.ssafy.home.external.seoul.cctv;

import java.util.List;

public record SeoulCctvPage(
        List<SeoulRawCctv> rows,
        int totalCount
) {
}
