package com.ssafy.home.external.seoul.cctv;

public record SeoulRawCctv(
        String purpose,
        String cameraCount,
        String address,
        String latitude,
        String longitude
) {
}
