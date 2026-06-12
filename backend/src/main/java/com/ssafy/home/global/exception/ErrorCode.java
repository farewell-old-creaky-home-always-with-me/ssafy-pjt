package com.ssafy.home.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    MEMBER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다"),

    HOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주택을 찾을 수 없습니다"),
    HOUSE_INVALID_REGION(HttpStatus.BAD_REQUEST, "유효하지 않은 행정구역 코드입니다"),
    HOUSE_INVALID_REGION_REQUIRED(HttpStatus.BAD_REQUEST, "행정구역 코드는 필수입니다"),
    HOUSE_INVALID_REGION_LENGTH(HttpStatus.BAD_REQUEST, "행정구역 코드는 10자리여야 합니다"),
    HOUSE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 주택 유형입니다"),
    HOUSE_INVALID_DEAL_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 거래 유형입니다"),
    HOUSE_INVALID_AMOUNT_MIN(HttpStatus.BAD_REQUEST, "최소 금액은 0 이상이어야 합니다"),
    HOUSE_INVALID_AMOUNT_MAX(HttpStatus.BAD_REQUEST, "최대 금액은 0 이상이어야 합니다"),
    HOUSE_INVALID_AMOUNT_RANGE(HttpStatus.BAD_REQUEST, "최소 금액은 최대 금액보다 클 수 없습니다"),

    FAVORITE_DUPLICATE(HttpStatus.CONFLICT, "이미 등록된 관심 지역입니다"),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 관심 지역을 찾을 수 없습니다"),
    FAVORITE_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 관심 지역만 삭제할 수 있습니다"),

    PLACE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 장소 유형입니다"),
    PLACE_INVALID_NAME(HttpStatus.BAD_REQUEST, "장소 이름은 필수입니다"),
    PLACE_INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "주소는 필수입니다"),
    PLACE_INVALID_COORDINATE(HttpStatus.BAD_REQUEST, "유효하지 않은 좌표입니다"),
    PLACE_INVALID_REGION(HttpStatus.BAD_REQUEST, "유효하지 않은 행정구역 코드입니다"),
    PLACE_DUPLICATE_TYPE(HttpStatus.CONFLICT, "이미 등록된 장소 유형입니다"),
    PLACE_OTHER_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "기타 장소는 최대 5개까지 등록할 수 있습니다"),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다"),
    PLACE_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 장소만 수정하거나 삭제할 수 있습니다"),

    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다"),

    COMMERCIAL_INVALID_COORDINATE(HttpStatus.BAD_REQUEST, "유효하지 않은 좌표입니다"),
    COMMERCIAL_INVALID_RADIUS(HttpStatus.BAD_REQUEST, "반경은 0보다 커야 합니다"),

    ENV_INVALID_COORDINATE(HttpStatus.BAD_REQUEST, "유효하지 않은 좌표입니다"),
    ENV_INVALID_RADIUS(HttpStatus.BAD_REQUEST, "반경은 0보다 커야 합니다"),
    ROUTE_NO_FACILITIES(HttpStatus.UNPROCESSABLE_ENTITY, "등록된 시설물이 없습니다"),
    ROUTE_UNREACHABLE(HttpStatus.UNPROCESSABLE_ENTITY, "출발지 또는 도착지 1km 내에 시설물이 없습니다"),
    ROUTE_NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY, "경로를 찾을 수 없습니다"),

    COMMON_INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    COMMON_INVALID_PAGE(HttpStatus.BAD_REQUEST, "페이지 조건이 올바르지 않습니다"),
    COMMON_DATA_CONFLICT(HttpStatus.CONFLICT, "요청을 처리할 수 없습니다"),
    COMMON_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
    ;

    private final HttpStatus status;
    private final String message;
}
