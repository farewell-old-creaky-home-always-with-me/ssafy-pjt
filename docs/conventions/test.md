# Test Conventions

## Strategy per Layer

| Layer | Annotation |
|-------|-----------|
| Controller | `@WebMvcTest` |
| Service | `@ExtendWith(MockitoExtension.class)` |
| Mapper | `@MybatisTest` |

Controller tests verify HTTP status, validation, and request/response shape.
Service tests are pure unit tests — no Spring context.
Mapper tests use H2 by default; switch to Testcontainers when SQL uses DB-specific syntax.

## Naming

Test class: `{TargetClass}Test`. Method: camelCase. Intent: `@DisplayName` in Korean.

## Rules

| Item | Rule |
|------|------|
| Structure | Given / When / Then with comments |
| Assertions | AssertJ only (`assertEquals`/`assertTrue` forbidden) |
| Stubbing | `BDDMockito.given().willReturn()` |
| Mock declaration | `@Mock` |
| Subject instantiation | Constructor in `@BeforeEach` (not `@InjectMocks`) |
| `verify` | Only for external side effects not verifiable via return value |
| Fixtures | Inline if used once; private factory method if used twice or obscures intent |

## Fixtures

**Controller** — use private factory methods for request/response objects.

```java
private MemberCreateRequest createRequest() {
    return new MemberCreateRequest("user@example.com", "password", "홍길동");
}

private MemberDetailResponse memberResponse() {
    return new MemberDetailResponse(1L, "user@example.com");
}
```

**Service** — private factory method returning the mapper's `XxxResult` DTO.

```java
private MemberDetailResult memberDetailResult(Long id, String email) {
    return new MemberDetailResult(id, email, false);
}

// stubbing
given(memberMapper.findById(1L)).willReturn(memberDetailResult(1L, "user@example.com"));
```

**Mapper** — `@MybatisTest` + `@Sql` for test data.

```java
@MybatisTest
@Sql("member-data.sql")
class MemberMapperTest {

    @Autowired MemberMapper memberMapper;

    @Test
    @DisplayName("ID로 회원을 조회한다")
    void findById() {
        MemberDetailResult found = memberMapper.findById(1L);
        assertThat(found.getEmail()).isEqualTo("user@example.com");
    }
}
```

## Security in Controller Tests

Disable the security filter when auth is not the test subject. Use `@WithMockUser` or import minimal security config when testing auth behavior.
