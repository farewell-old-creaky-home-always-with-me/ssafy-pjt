package com.ssafy.home.place.mapper.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceCreateParam {

    private Long id;
    private Long memberId;
    private String placeType;
    private String name;
    private String address;
    private String regionCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
