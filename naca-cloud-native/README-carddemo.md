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

Open `http://localhost:8000/carddemo/` to exercise the JSON terminal adapter,
or call it directly:

```bash
curl --fail -H 'Content-Type: application/json' \
  --data '{"requestId":"00000000-0000-0000-0000-000000000001","conversationId":"00000000-0000-0000-0000-000000000002","transactionId":"CC00","mapSet":"COSGN00","map":"COSGN0A","aid":"ENTER","cursorField":"USERID","fields":{"USERID":{"value":"DEMO0001","modified":true,"cleared":false}}}' \
  http://localhost:8000/api/carddemo/bms/adapter
```

The endpoint accepts JSON only. It constructs the internal NacaRT DOM itself,
so untrusted clients cannot submit XML. Password fields are cleared at the
outbound JSON boundary. Until `COSGN00C` can lower successfully, the endpoint
is an adapter diagnostic rather than the real CardDemo sign-on transaction.

`GET /api/carddemo/capabilities` reports all 25 pinned online transactions and
their current translation/compilation/acceptance state. The build packages this
report from the corpus inventory rather than maintaining a second handwritten
transaction list. PostgreSQL also stores terminal and COMMAREA snapshots with
row locking, optimistic versions, expiry and caller-controlled rollback.

Flyway owns all CardDemo objects. It migrates an empty database through V005
and creates the `carddemo_runtime`, `carddemo_vsam`, `carddemo` and
`carddemo_compat` schemas. Docker initialization must not create business
tables or seed records.

## Acceptance gates

```bash
./gradlew :naca-cloud-native:cardDemoOnlineBaseline
./gradlew :naca-cloud-native:cardDemoPostgresAcceptance
./gradlew :naca-cloud-native:cardDemoMinimalBmsAcceptance
```

The PostgreSQL gate uses a real PostgreSQL 16 Testcontainer and skips only when
Docker is unavailable. It validates empty-database migration, readiness,
Spring-to-NacaRT connection binding, transaction rollback, no-data SQLCODE
`+100`, duplicate-key SQLCODE `-803`, cloud-provided CICS `APPLID`/`SYSID`,
and PostgreSQL keyed-record `READ` responses for `NORMAL`, `NOTFND` and
`LENGERR`.

The minimal BMS gate drives a controlled source through the unmodified
`naca-trans` pipeline, generates the COBOL program plus physical/symbolic BMS
classes and DFHAID, applies the target-only symbolic Form alias adapter, compiles
everything into `build/generated-carddemo/minimal`, and executes
`JSON -> RECEIVE MAP -> COBOL IF/MOVE -> SEND MAP -> JSON`. The expected `PING`
request must return `PONG`; checking generated text alone is not accepted.

The online translation gate currently records `COSGN00C` as blocked. It uses
the upstream symbolic BMS copybook so COBOL/CICS translation can be measured
separately from full 3270 BMS generation. The JSON Map gateway exposes the
existing RECEIVE/SEND MAP data boundary as a REST contract without exposing
the legacy XML protocol.
