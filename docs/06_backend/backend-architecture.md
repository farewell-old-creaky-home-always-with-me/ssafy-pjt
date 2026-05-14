# 백엔드 아키텍처

- 상태: 초안
- 작성자:
- 마지막 수정일: 2026-05-14
- 관련 요구사항: 전체
- 관련 문서: [package-structure.md](package-structure.md), [class-diagram.md](class-diagram.md), [error-handling.md](error-handling.md), [../05_api/api-overview.md](../05_api/api-overview.md)

---

## 아키텍처 개요

SSAFY HOME 백엔드는 Spring Boot 기반의 3-레이어 아키텍처로 구성된다.

```
[HTTP 요청]
    │
    ▼
Controller (요청 수신, 응답 변환)
    │
    ▼
Service (비즈니스 로직)
    │
    ▼
Mapper (SQL 실행, MyBatis)
    │
    ▼
MySQL
```

외부 공공 데이터 API는 별도 `collector` 또는 `client` 클래스에서 HTTP 호출을 담당하고, Service에서 호출한다.

---

## 레이어별 책임

### Controller

- HTTP 요청을 수신하고 요청 파라미터·본문을 검증한다.
- Service를 호출하고 결과를 공통 응답 포맷으로 변환해 반환한다.
- 예외 처리는 Controller에서 직접 하지 않는다. `@ControllerAdvice`로 통합한다.
- 인증 체크는 Interceptor 또는 AOP에서 처리하고, Controller는 인증이 완료된 요청만 받는다.

### Service

- 비즈니스 로직을 처리하는 레이어다.
- 여러 Mapper 호출을 조합하거나 외부 API 클라이언트를 호출한다.
- 트랜잭션은 Service 레이어에서 `@Transactional`로 관리한다.
- 도메인 규칙(중복 확인, 권한 검사 등)을 이 레이어에서 처리한다.

### Mapper

- MyBatis를 사용해 SQL을 실행하는 레이어다.
- 인터페이스로 정의하고 XML Mapper에 SQL을 작성한다.
- Java 코드 내에 SQL 문자열을 인라인으로 작성하지 않는다.
- 단일 테이블 CRUD 또는 단순 조인을 처리한다. 복잡한 비즈니스 조합은 Service에서 수행한다.

---

## DTO / 도메인 분리

| 클래스 유형 | 역할 |
|------------|------|
| `*Request` | HTTP 요청 본문 매핑. Controller 입력 |
| `*Response` | HTTP 응답 본문 구성. Controller 출력 |
| `*Dto` | Service-Mapper 간 데이터 전달 |
| `*` (도메인) | 도메인 개념을 표현하는 순수 Java 클래스. 알고리즘 등에서 사용 |

---

## MyBatis 사용 원칙

- Mapper 인터페이스는 `@Mapper` 어노테이션을 붙인다.
- SQL은 `src/main/resources/mapper/{domain}Mapper.xml`에 작성한다.
- 동적 쿼리는 `<if>`, `<choose>`, `<foreach>` 태그를 사용한다.
- ResultMap을 사용해 컬럼명과 Java 필드명 차이를 명시적으로 매핑한다.

---

## 공통 응답 처리

`ApiResponse<T>` 공통 래퍼 클래스로 모든 API 응답을 감싼다.

```java
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private ErrorDetail error;
}
```

Controller에서 직접 `ApiResponse`를 생성하거나, `ResponseEntity<ApiResponse<T>>`를 반환한다.

---

## 예외 처리 요약

`@ControllerAdvice`를 적용한 `GlobalExceptionHandler`에서 예외를 처리한다.

| 예외 클래스 | HTTP 상태 | 설명 |
|------------|-----------|------|
| `ResourceNotFoundException` | 404 | 리소스 없음 |
| `DuplicateResourceException` | 409 | 중복 리소스 |
| `UnauthorizedException` | 401 | 미인증 |
| `ForbiddenException` | 403 | 권한 없음 |
| `ValidationException` | 400 | 입력 유효성 실패 |
| `RouteNotFoundException` | 422 | 경로 없음 |
| `Exception` (전체) | 500 | 처리되지 않은 예외 |

자세한 내용은 [error-handling.md](error-handling.md) 참조.

---

## 인증 처리

Spring MVC Interceptor를 사용해 인증이 필요한 API에 대해 세션 유효성을 검사한다.

```
요청 → DispatcherServlet → HandlerInterceptor (세션 검사) → Controller
```

- `HandlerInterceptor.preHandle()`에서 세션 존재 여부를 확인한다.
- 세션이 없으면 `UnauthorizedException`을 던진다.
- `@LoginRequired` 어노테이션(커스텀)으로 인증 필요 여부를 Controller 메서드에 표시한다 (미정).

---

## 외부 API 클라이언트

공공 데이터 API 호출은 별도 클라이언트 클래스에서 담당한다.

```
HouseCollectService → MolitApiClient → 국토교통부 API
CommercialService → CommercialApiClient → 공공 상권 API
EnvironmentService → SeoulOpenApiClient → 서울 열린데이터 API
```

외부 API 장애 시 Service에서 catch해 기본값 또는 DB 캐시 데이터를 반환한다.
