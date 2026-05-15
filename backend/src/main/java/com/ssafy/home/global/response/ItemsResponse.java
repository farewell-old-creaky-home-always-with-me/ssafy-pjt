package com.ssafy.home.global.response;

import java.util.List;

public record ItemsResponse<T>(List<T> items) {
}
