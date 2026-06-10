package com.ssafy.home.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Node {
    private final long id;
    private final double lat;
    private final double lng;
    private final String name;
}
