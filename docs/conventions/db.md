# Database Conventions

## Naming (MySQL)

- Tables: `snake_case`, singular (`member`, `house_deal`)
- Columns: `snake_case` (`created_at`, `member_id`)

## MyBatis Mapping

`map-underscore-to-camel-case: true` is enabled.

## Flyway

- Keep production schema changes in `backend/src/main/resources/db/migration`.
- Name migrations `V<version>__<description>.sql` and increase versions monotonically.
- Never edit an applied migration. Add a new migration instead.
- Migrations must contain schema changes only. The sole data exception is structural framework state, currently the three Spring Batch MySQL sequence rows.
- Keep application and mock data in `backend/src/main/resources/data.sql`, outside Flyway history.
- When upgrading Spring Batch, review and apply the official schema migration scripts.
- This setup assumes a new, empty database. Baselining or adopting an existing database requires a separate procedure and is outside this setup.
