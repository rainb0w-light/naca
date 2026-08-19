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

`bootRun` first translates and compiles `COSGN00C` into the cloud-native target
workspace. Open `http://localhost:8000/carddemo/` to exercise the JSON terminal adapter,
or call it directly:

```bash
curl --fail -H 'Content-Type: application/json' \
  --data '{"requestId":"00000000-0000-0000-0000-000000000001","conversationId":"00000000-0000-0000-0000-000000000002","transactionId":"CC00","mapSet":"COSGN00","map":"COSGN0A","aid":"ENTER","cursorField":"USERID","fields":{"USERID":{"value":"MISSING1","modified":true,"cleared":false},"PASSWD":{"value":"PASSWORD","modified":true,"cleared":false}}}' \
  http://localhost:8000/api/carddemo/bms/adapter
```

The endpoint accepts JSON only. It constructs the internal NacaRT DOM itself,
so untrusted clients cannot submit XML. For `CC00` it executes the translated
`COSGN00C`, maps implicit BMS `*I/*O` symbolic copybook fields, and performs the
keyed `USRSEC` read through PostgreSQL. Password fields are cleared at the
outbound JSON boundary. The accepted end-to-end scenario is the initial screen
plus the unknown-user authentication response; a valid login still transfers
with `XCTL` to `COADM01C` or `COMEN01C`, which are not yet in this executable slice.

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
`LENGERR`. It also runs the real translated `COSGN00C` through
`JSON -> implicit RECEIVE MAP -> CICS READ -> PostgreSQL -> SEND MAP -> JSON`
and requires the original COBOL "User not found" response with a masked password.

The minimal BMS gate drives a controlled source through the unmodified
`naca-trans` pipeline, generates the COBOL program plus physical/symbolic BMS
classes and DFHAID, applies the target-only symbolic Form alias adapter, compiles
everything into `build/generated-carddemo/minimal`, and executes
`JSON -> RECEIVE MAP -> COBOL IF/MOVE -> SEND MAP -> JSON`. The expected `PING`
request must return `PONG`; checking generated text alone is not accepted.

The online translation gate records `COSGN00C` as translated and requires its
generated Java plus all copybooks to compile. It uses the upstream symbolic BMS
copybook so COBOL/CICS translation and JSON execution remain independent of a
full 3270 renderer. The generated source/classes are placed under
`build/generated-carddemo/signon`; set `CARDDEMO_GENERATED_CLASSES_DIR` to
override the runtime class directory.
