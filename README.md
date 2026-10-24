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

## Design Decisions

- Used an interface (`RateLimiterStrategy`) so algorithms are interchangeable without
  changing calling code — a simple application of the Strategy design pattern.
- Both algorithms use `synchronized` methods to prevent race conditions when multiple
  concurrent requests from the same user are evaluated simultaneously.
- Chose to implement two algorithms specifically to compare trade-offs: Token Bucket
  trades precision for memory efficiency and natural burst support; Sliding Window
  trades memory for stricter, more precise enforcement.

## Planned Next

- Redis-backed distributed state, so rate limits are enforced correctly across
  multiple running instances of this service (current in-memory version only works
  correctly on a single instance).
- Kafka event publishing when requests are rate-limited, consumed by a separate
  service for logging/analytics.
- Load testing with k6, metrics endpoint, and containerized deployment.

## Tech Stack

Java 21+, Spring Boot, Maven, JUnit 5