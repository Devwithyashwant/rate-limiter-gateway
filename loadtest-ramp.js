import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '15s', target: 50 },   // ramp to 50 VUs
        { duration: '15s', target: 200 },  // ramp to 200 VUs
        { duration: '15s', target: 500 },  // ramp to 500 VUs
        { duration: '15s', target: 0 },    // ramp down
    ],
};

export default function () {
    const userId = `ramptest-user-${__VU}`;
    const res = http.get(`http://localhost:8080/api/resource?userId=${userId}`);

    check(res, {
        'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
        'no server errors': (r) => r.status < 500,
    });

    sleep(0.1);
}