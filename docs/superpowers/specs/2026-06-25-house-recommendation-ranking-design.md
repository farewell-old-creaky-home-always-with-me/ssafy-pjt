# House Recommendation Ranking Design

## Context

REQ-ROUTE-002 requires at least two algorithm applications in the existing SSAFY HOME product. A* route search already exists in the route domain. This design adds a second algorithm to the house search flow, where users already compare candidate homes.

## Goal

Add a recommendation sort option to the house search screen using a weighted scoring algorithm over existing house and latest-deal fields.

## Non-Goals

- Do not add a new user preference model.
- Do not add new tables.
- Do not change the existing search API response shape unless the implementation needs to display the score.
- Do not replace existing sorts for name, area, floor, price, or date.

## Algorithm

Use a weighted recommendation score calculated for each searched house:

```text
recommendScore =
  priceFitScore
+ areaScore
+ recencyScore
+ floorScore
+ buildYearScore
```

Initial scoring uses fields already loaded by the house search query:

- `priceFitScore`: if both min and max amount are provided, homes closer to the midpoint rank higher; otherwise lower price ranks higher.
- `areaScore`: larger latest-deal area ranks higher.
- `recencyScore`: newer latest-deal dates rank higher.
- `floorScore`: non-basement and ordinary residential floors rank higher than unusually low floors.
- `buildYearScore`: newer buildings rank higher.

The score is used only for ordering. It does not filter out results beyond the existing user search conditions.

## Backend Design

Add `recommend` to the allowed `sortBy` values in `HouseService`.

Extend `HouseMapper.xml` so `sortBy == 'recommend'` orders by a deterministic weighted expression and then `h.id DESC`. Keep the existing paging and count flow unchanged.

The algorithm should stay in the search SQL for now because all required fields are already present in the query and database ordering must happen before `LIMIT/OFFSET`.

## Frontend Design

Add a `추천순` sort control to the existing search result table header area. It should reuse the current `sortKey` and `sortDir` behavior, with `sortKey = 'recommend'`.

The first implementation does not need a new visual score column. The visible proof is that users can select recommendation sorting from the same search results screen.

## Error Handling

Invalid `sortBy` values should continue to return `COMMON_INVALID_INPUT`.

If data fields are missing, the SQL expression should use neutral fallback values through `COALESCE` so recommendation sorting remains stable.

## Testing

Backend tests:

- `HouseServiceTest` verifies that `sortBy = recommend` is accepted and passed to the mapper.
- Mapper-level coverage verifies that recommendation sorting returns the expected first result for controlled fixture data, if existing test fixtures make this practical.

Frontend tests are not available in the current project scripts. Verification should include `npm run build` from `frontend/`.

End-to-end verification:

- `./gradlew test` from `backend/`.
- `npm run build` from `frontend/`.

## Documentation

Add or update an algorithm document explaining the weighted house recommendation algorithm and connecting it to REQ-ROUTE-002.
