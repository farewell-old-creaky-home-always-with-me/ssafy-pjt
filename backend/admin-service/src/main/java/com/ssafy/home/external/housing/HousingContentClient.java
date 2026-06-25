package com.ssafy.home.external.housing;

import java.util.List;

public interface HousingContentClient {

    List<HousingRawContent> fetchAll(List<HousingContentSourceProperties.Source> sources);
}
