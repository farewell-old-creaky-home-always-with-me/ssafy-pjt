# Architecture

## Package Structure

Domain-per-package layout under `com.ssafy.home`.

```
[domain]/
├── controller/
├── service/
├── mapper/
│   ├── XxxMapper.java
│   └── dto/          # XxxParam (input), XxxResult (output)
└── dto/              # XxxRequest (client in), XxxResponse (client out)
```

Global utilities live in `global/` (`exception`, `config`, `interceptor`).

## Layer Rules

Dependency direction: `Controller → Service → Mapper`. Reverse references are forbidden.
