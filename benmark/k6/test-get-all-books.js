import http from 'k6/http';
import { check } from 'k6';

export const options = {
    // Keep the TCP connection open to avoid port exhaustion on Windows
    noConnectionReuse: false,

    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 600,             // Desired rate: 600 requests/second
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,   // Pre-initialize 50 VUs
            maxVUs: 300,           // Maximum of 300 VUs (to avoid RAM/CPU overflow)
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],   // Error rate < 1%
        http_req_duration: ['p(95)<200'], // Expectation: 95% of requests < 200ms
    },
};

export default function () {
    const url = 'http://localhost:9001/api/v1/books?page=1&size=7&sort=name&direction=asc';

    const res = http.get(url, {
        headers: {
            'Content-Type': 'application/json',
        },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => r.body.includes('data'),
    });
}