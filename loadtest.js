import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50,          // 50 virtual users (concurrent simulated clients)
    duration: '30s',  // run for 30 seconds
};

export default function () {
    // Each virtual user gets a unique-ish userId based on their VU number,
    // so we can observe per-user rate limiting under real concurrency.
    const userId = `loadtest-user-${__VU}`;
    const res = http.get(`http://localhost:8080/api/resource?userId=${userId}`);

    check(res, {
        'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
    });

    sleep(0.1); // small pause between requests per virtual user
}