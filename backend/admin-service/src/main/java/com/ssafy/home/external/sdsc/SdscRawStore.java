package com.ssafy.home.external.sdsc;

public record SdscRawStore(
        String bizesId,
        String bizesNm,
        String indsLclsNm,
        String indsMclsNm,
        String indsSclsNm,
        String lat,
        String lon,
        String rdnmAdr,
        String lnoAdr
) {
}
