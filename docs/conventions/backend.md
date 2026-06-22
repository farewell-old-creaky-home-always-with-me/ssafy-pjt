# Backend Conventions

## Imports

No wildcard imports. Static imports precede regular imports. Reference `ErrorCode` constants via static import.

## Naming

**URL paths:** kebab-case (`/user-profile`, not `/userProfile`).

**DTO classes:** `{Domain}{Action}{Suffix}` pattern.

| Suffix | Location | Purpose |
|--------|----------|---------|
| `XxxRequest` | `dto/` | client request body |
| `XxxResponse` | `dto/` | client response body |
| `XxxParam` | `mapper/dto/` | SQL input |
| `XxxResult` | `mapper/dto/` | SQL output |

e.g. `MemberCreateRequest`, `MemberDetailResponse`, `MemberCreateParam`, `MemberDetailResult`.

**Mapper methods:** class name already contains the domain — do not repeat it in the method name.

| Purpose | Pattern | Example |
|---------|---------|---------|
| Single fetch | `findBy{Condition}` | `findById` |
| List fetch | `findAllBy{Condition}` | `findAllByMemberId` |
| Dynamic search | `search` | `search(MemberSearchParam)` |
| Insert | `insert` | `insert(MemberCreateParam)` |
| Update | `update{Target}By{Condition}` | `updatePasswordById` |
| Delete | `deleteBy{Condition}` | `deleteById` |
| Soft delete | `softDeleteBy{Condition}` | `softDeleteById` |
| Count | `countBy{Condition}` | `countByStatus` |
| Exists | `existsBy{Condition}` | `existsByEmail` |

**DTO factory methods:** `from(single param)` / `of(multiple params)`.

## Controller

Return type must be `ResponseEntity<T>`.

## Service

Annotate read methods with `@Transactional(readOnly = true)`, write methods with `@Transactional`. Place private helper methods immediately after the public method that calls them.

## DTO

Use Java `record` for simple response DTOs.

## Exception

Define errors in `ErrorCode` enum with HTTP status + message. Throw via `new CustomException(ERROR_CODE)`.

All exceptions are handled by `global/exception/GlobalExceptionHandler`. Do not add `@ExceptionHandler` directly to domain controllers or services.

Error response body:
```json
{ "code": "MEMBER_NOT_FOUND", "message": "회원을 찾을 수 없습니다." }
```
Validation errors include a `fields` array:
```json
{ "code": "COMMON_INVALID_INPUT", "message": "...", "fields": [{ "field": "email", "message": "..." }] }
```

## MyBatis XML

Location: `src/main/resources/mapper/{Domain}Mapper.xml`.

- `namespace` must match the mapper interface's fully qualified name.
- `id` must exactly match the interface method name.
- Write `resultMap` only when auto-mapping fails (i.e. column name differs from camelCase field name after underscore conversion). Id uses camelCase: `memberResultMap`.

## Authentication

Use `@LoginMemberId` (parameter type: `Long`) to inject the authenticated member's ID into controller parameters. Never access `HttpSession` directly in controllers.
