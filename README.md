## Playback Progress: Project Details

This repository contains an implementation of a low-latency Playback Progress service (hot cache + async durability).

- **LLD location**: [docs/LLD-playback-progress.md](docs/LLD-playback-progress.md)
- **Tasks / requirements**: [docs/TASKS-playback-progress.md](docs/TASKS-playback-progress.md)

### General flows (from LLD)
- Ingest (save): Client -> `POST /v1/playback/save` -> idempotency & rate-limit -> write to Redis (HSET) -> publish event to Kafka -> return 202. Worker consumes Kafka, batch-merges and conditionally upserts to DB.
- Read (history): Client -> `GET /v1/playback/history` -> try Redis HGETALL for user -> if hit return sorted slice; if miss query DB, populate cache, return results.

### What's covered in this project
- Hot-path cache: Redis-based `CacheClient` implementation and HSET/HGET flows.
- Event publishing: Kafka producer client and `EventPublisher` implementation.
- Repository: `MySqlPlaybackRepository` bridging cache, events and JPA store.
- Worker: `DBWorker` Kafka consumer with batching, dedup/merge and conditional persistence.
- API: Controller and service layers for save and history endpoints, including idempotency and rate-limiting filter.
- Tests: Unit tests for major components (repository, worker, filter, controller).
- Docs: TASKS and LLD documents in `/docs`.
- Observability: metrics exposed via Spring Actuator and Micrometer (Prometheus-friendly).

### SQL tables and migrations
- SQL DDL files are in: [docs/sql/playback_progress.sql](docs/sql/playback_progress.sql)
- The file contains `CREATE TABLE` statements for:
  - `playback_progress` (primary playback store)
  - `playback_idempotency` (idempotency keys)
  - `playback_events_dlq` (dead-letter events)
  - `playback_audit_log` (append-only audit)
  - `playback_recent` (materialized recent playback view)

If you want Flyway or Liquibase migrations generated from these DDLs, I can scaffold them next.

### Running locally
Ensure Docker, Java 21, and Maven (or the bundled `./mvnw`) are available. Build the app with `./mvnw clean package` and start local infra with `./up.sh` or `docker-compose up` (the compose file includes MySQL and Redis; add Kafka for full end-to-end). Alternatively run the app directly with `./mvnw spring-boot:run` pointed at a running MySQL and Redis. Run unit tests with `./mvnw test`.

```bash
# Build, start infra and run tests (examples)
./mvnw clean package
./up.sh
./mvnw test
```
