package com.ssafy.home.cctv.mapper.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CctvResult {

    private Long id;
    private String purpose;
    private Integer cameraCount;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double distance;
}
