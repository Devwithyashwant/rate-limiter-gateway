import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '15s', target: 500 },
        { duration: '20s', target: 1000 },
        { duration: '20s', target: 2000 },
        { duration: '15s', target: 0 },
    ],
};

export default function () {
    const userId = `ramptest2-user-${__VU}`;
    const res = http.get(`http://localhost:8080/api/resource?userId=${userId}`);

    check(res, {
        'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
        'no server errors': (r) => r.status < 500,
        'fast response': (r) => r.timings.duration < 500,
    });

    sleep(0.1);
}