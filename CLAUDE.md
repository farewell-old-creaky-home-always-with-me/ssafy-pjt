# SSAFY HOME

## Project

Spring Boot 3.5, Java 17, Gradle, MyBatis, Lombok. Root package: `com.ssafy.home`.

## Commands

Run from `backend/`.

- Build: `./gradlew build`
- Test: `./gradlew test`
- Single test: `./gradlew test --tests "ClassName"`

## Conventions

Read only the relevant document before editing.

- Backend: `docs/conventions/backend.md`
- API: `docs/conventions/api.md`
- Test: `docs/conventions/test.md`
- Database: `docs/conventions/db.md`
- Architecture: `docs/conventions/architecture.md`

## Rules

- Controllers must stay thin.
- Business logic belongs in services.
- Transactions belong in services.
- Do not bypass authorization.
- Add or update tests for behavior changes.
- Do not introduce dependencies without justification.
- Run relevant tests before finishing.
- Mention checks that could not be run.
