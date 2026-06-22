package com.ssafy.home.place.mapper.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceResult {

    private Long id;
    private Long memberId;
    private String placeType;
    private String name;
    private String address;
    private String regionCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
