# Rate Limiter Gateway

A distributed rate limiting system built in Java/Spring Boot, designed to demonstrate
real-world backend concepts: concurrency control, pluggable algorithm design, and
(upcoming) distributed state management with Redis and async event processing with Kafka.

## Current Features

- **Two pluggable rate-limiting algorithms**, selectable via a strategy pattern:
    - **Token Bucket**: allows bursts up to a configurable bucket size, with tokens
      refilling at a steady rate over time. Memory-efficient (O(1) per user).
    - **Sliding Window Counter**: tracks individual request timestamps within a
      rolling time window for precise rate enforcement, avoiding the boundary-burst
      exploit of naive fixed-window approaches. Higher memory cost (O(n) per user,
      where n = requests within the window).
- Per-user rate limiting via in-memory, thread-safe storage (`ConcurrentHashMap`).
- Returns proper HTTP 429 (Too Many Requests) on rate limit violations.
- Unit tested, including concurrency-sensitive refill/window-slide behavior.

## Verified Distributed Behavior

Manually tested by running two separate instances of this service on different ports
(8081, 8082), both connected to the same Redis instance. Confirmed that a rate limit
is correctly enforced as a single shared count across both instances — a request
hitting the limit on one instance is rejected even when the next request arrives at
a completely different, independent instance. This validates that state is correctly
centralized in Redis rather than held per-instance in memory.
## Design Decisions

- Used an interface (`RateLimiterStrategy`) so algorithms are interchangeable without
  changing calling code — a simple application of the Strategy design pattern.
- Both algorithms use `synchronized` methods to prevent race conditions when multiple
  concurrent requests from the same user are evaluated simultaneously.
- Chose to implement two algorithms specifically to compare trade-offs: Token Bucket
  trades precision for memory efficiency and natural burst support; Sliding Window
  trades memory for stricter, more precise enforcement.

## Kafka Integration

Whenever a request is rate-limited, an event is published to the `rate-limit-exceeded`
Kafka topic. A separate consumer processes these events independently, tracking blocked
attempts per user — demonstrating decoupling between request handling and event processing.

Note: this Spring Boot version's Kafka auto-configuration did not activate correctly
(diagnosed via Spring's condition evaluation report), so producer and consumer beans
are defined explicitly in `KafkaProducerConfig` and `KafkaConsumerConfig`.

## Planned Next

- Redis-backed distributed state, so rate limits are enforced correctly across
  multiple running instances of this service (current in-memory version only works
  correctly on a single instance).
- Kafka event publishing when requests are rate-limited, consumed by a separate
  service for logging/analytics.
- Load testing with k6, metrics endpoint, and containerized deployment.
## Deployment

The entire system (app, Redis, Kafka, Zookeeper) runs via a single command:

    docker compose up -d --build

This builds the app using a multi-stage Dockerfile and starts all services on a shared
Docker network. Note: when containerized, services communicate using Docker's internal
service names (e.g., `kafka:9092`, `redis:6379`) rather than `localhost` — hardcoding
`localhost` is a common mistake when moving from local development to a containerized
environment, which I hit and fixed during this build.

## Metrics

A basic `/api/metrics` endpoint tracks total allowed vs. blocked requests.
## Tech Stack

Java 21+, Spring Boot, Maven, JUnit 5

## Load Testing & Bottleneck Analysis

Tested with k6 across three scenarios of increasing concurrency:

| Peak VUs | Throughput | p95 Latency | Correctness |
|----------|-----------|-------------|-------------|
| 50       | ~470 req/s   | 11.7ms   | 100% |
| 500      | ~1,724 req/s | 21.6ms   | 100% |
| 2000     | ~4,703 req/s | 231.6ms  | 99.84% |

**Findings:**
- The system remained functionally correct even under extreme load (2000 concurrent
  users) — zero 500 errors, zero incorrect rate-limit decisions, across nearly 1
  million total checks.
- Latency degrades gracefully rather than failing outright: p95 rose from ~12ms to
  ~232ms as load scaled 40x, with 99% of requests still completing under 500ms.
- `docker stats` during peak load showed the application container (Spring Boot/Tomcat)
  as the primary bottleneck at 600%+ CPU, while Redis stayed under 15% CPU — meaning
  the data layer has significant headroom and the app's request-handling layer is the
  limiting factor, not the distributed state store.

**What I'd do to address this at scale:**
- Horizontally scale the app behind a load balancer — this is exactly what the
  Redis-backed design enables, since multiple instances already share correct rate-limit
  state (verified earlier in this project)
- Profile and tune the request-handling path (JSON serialization, Tomcat thread pool
  sizing) since that's the confirmed bottleneck, not the data layer
- Consider a reactive stack (Spring WebFlux) if CPU-bound request dispatch remains
  a hard ceiling after tuning