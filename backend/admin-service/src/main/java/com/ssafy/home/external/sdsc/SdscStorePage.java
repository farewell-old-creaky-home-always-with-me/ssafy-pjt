package com.ssafy.home.external.sdsc;

import java.util.List;

public record SdscStorePage(List<SdscRawStore> stores, int totalCount) {
}
