# CardDemo cloud-native target

The CardDemo runtime is opt-in. The normal `naca-cloud-native` profile remains
database-free; activate the `carddemo` profile to enable PostgreSQL, Flyway and
the NacaRT connection bridge.

## Start PostgreSQL

From the repository root:

```bash
cp .env.carddemo.example .env.carddemo
docker compose --env-file .env.carddemo up -d postgres
```

Load the `NACA_DATABASE_*` values from that file into the shell, then run:

```bash
SPRING_PROFILES_ACTIVE=carddemo ./gradlew :naca-cloud-native:bootRun
curl --fail http://localhost:8000/actuator/health/readiness
```

Flyway owns all CardDemo objects. It migrates an empty database through V005
and creates the `carddemo_runtime`, `carddemo_vsam`, `carddemo` and
`carddemo_compat` schemas. Docker initialization must not create business
tables or seed records.

## Acceptance gates

```bash
./gradlew :naca-cloud-native:cardDemoOnlineBaseline
./gradlew :naca-cloud-native:cardDemoPostgresAcceptance
```

The PostgreSQL gate uses a real PostgreSQL 16 Testcontainer and skips only when
Docker is unavailable. It validates empty-database migration, readiness,
Spring-to-NacaRT connection binding, transaction rollback, no-data SQLCODE
`+100`, and duplicate-key SQLCODE `-803`.

The online translation gate currently records `COSGN00C` as blocked. It uses
the upstream symbolic BMS copybook so COBOL/CICS translation can be measured
separately from full 3270 BMS generation. The later JSON Map gateway will read
the real BMS source without exposing the legacy XML protocol.
