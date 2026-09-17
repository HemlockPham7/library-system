import http from 'k6/http';
import { check } from 'k6';

export const options = {
    // Giữ lại kết nối TCP để tránh ngốn Port trên Windows
    noConnectionReuse: false,

    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 600,             // Tốc độ mong muốn: 600 requests / giây
            timeUnit: '1s',
            duration: '30s',
            preAllocatedVUs: 50,   // Khởi tạo trước 50 VUs
            maxVUs: 300,           // Tối đa 300 VUs (tránh tràn RAM/CPU)
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],   // Tỷ lệ lỗi < 1%
        http_req_duration: ['p(95)<200'], // Kỳ vọng 95% request < 200ms
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