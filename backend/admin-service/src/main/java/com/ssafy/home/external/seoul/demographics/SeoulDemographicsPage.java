package com.ssafy.home.external.seoul.demographics;

import java.util.List;

public record SeoulDemographicsPage<T>(List<T> rows, int totalCount) {}
