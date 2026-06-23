package com.ssafy.home.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Edge {
    private final long fromId;
    private final long toId;
    private final double distanceM;
}
