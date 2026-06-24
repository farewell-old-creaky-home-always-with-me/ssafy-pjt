package com.ssafy.home.external.seoul;

import java.util.List;

public record SeoulEnvironmentPage(
        List<SeoulRawEnvironment> rows,
        int totalCount
) {
}
