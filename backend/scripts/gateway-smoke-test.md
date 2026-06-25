# Gateway Smoke Test

This guide verifies the local MSA path through the Gateway on port `8080`.

## Prerequisites

Build the jars used by the local Docker Compose stack.

```powershell
.\gradlew.bat :gateway:bootJar :admin-service:bootJar :main-service:bootJar
```

Start the MSA services with the shared MySQL database.

```powershell
docker compose -f docker-compose.local.yml up -d mysql main-service admin-service gateway
```

The smoke script generates HS256 JWTs locally with the same default secret used by docker compose:

```text
test-jwt-secret-key-for-ssafy-home-project-2026
```

Override it when needed:

```powershell
$env:JWT_SECRET = "your-local-secret-with-enough-length"
```

## Run

```powershell
.\scripts\gateway-smoke-test.ps1
```

Optional parameters:

```powershell
.\scripts\gateway-smoke-test.ps1 -BaseUrl http://localhost:8080 -AdminMemberId 1 -UserMemberId 2
```

## Scenarios

| Scenario | Request | Expected |
| --- | --- | --- |
| Missing JWT is rejected by Gateway | `GET /api/notices` | `401` |
| Invalid JWT is rejected by Gateway | `GET /api/notices` | `401` |
| User JWT cannot access admin route | `POST /api/admin/batch/region-codes` | `403` |
| Notice read routes to main-service | `GET /api/notices?page=1&size=5` | `200` |
| General API routes to main-service | `GET /api/houses?regionCode=1168010100&page=1&size=5` | `200` |
| User JWT cannot write notice | `POST /api/notices` | `403` |
| Admin JWT can write notice | `POST /api/notices` | `201` |
| Admin JWT can clean up created notice | `DELETE /api/notices/{noticeId}` | `204` |

## Notes

- Gateway validates the JWT signature for every non-`OPTIONS` `/api/**` request.
- `admin-service` validates `isAdmin=true` for `@AdminOnly` APIs.
- `GET /api/notices/**` is routed to `main-service`.
- `POST`, `PUT`, and `DELETE /api/notices/**` are routed to `admin-service`.
- The write smoke test assumes local seed data includes member `1` as an admin-compatible author.
