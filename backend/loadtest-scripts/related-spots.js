// 연관 관광지 조회 API 응답시간 측정용 소규모 요청 스크립트 (부하테스트 아님)
//
// 목적: RestClient(블로킹) vs WebClient(논블로킹) 구조 변경 전후의 "응답 시간"만 비교한다.
// 서울/인천/부산은 모두 TourApiAreaCode 기준 시군구 코드 3개를 가지고 있어서,
// 요청 1건당 한국관광공사 API를 정확히 3회 병렬 호출한다. 즉 "외부 API 3회 병렬 호출" 시나리오를 그대로 재현한다.
// REGION을 매 iteration마다 순환시키는 이유: 동일 지역만 반복 요청하면 캐싱 등으로 인해
// 뒤로 갈수록 응답이 빨라지는 왜곡이 발생하기 때문 (지역을 다양화해 이를 줄인다).
//
// 외부 API(apis.data.go.kr) 호출 한도 보호를 위해, 이 스크립트는 전체 실행에서
// TOTAL_REQUESTS(기본 30) × 3 = 90회 정도만 실제 한국관광공사 API를 호출하도록 총 요청 수를 제한한다.
// (VU/기간 기반이 아니라 "총 요청 수 기반"으로 실행되는 이유가 이것이다.)
//
// 실행 예시:
//   BASE_URL=http://localhost:8080 k6 run loadtest-scripts/related-spots.js
//   BASE_URL=http://localhost:8080 TOTAL_REQUESTS=30 CONCURRENCY=4 k6 run loadtest-scripts/related-spots.js

import http from "k6/http";
import { check } from "k6";
import { Trend } from "k6/metrics";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
// 시군구 코드 3개씩 -> 외부 API 3회 병렬 호출인 지역만 사용, 매 요청마다 순환
const REGIONS = ["서울", "인천", "부산"];
const TOTAL_REQUESTS = parseInt(__ENV.TOTAL_REQUESTS || "30", 10); // 총 호출할 our-API 요청 수 (외부 API 호출 수 = 이 값 x 3)
const CONCURRENCY = parseInt(__ENV.CONCURRENCY || "4", 10); // 동시에 날아가는 요청 수

const relatedSpotsDuration = new Trend("related_spots_duration", true);
const regionDurations = {
    "서울": new Trend("related_spots_duration_seoul", true),
    "인천": new Trend("related_spots_duration_incheon", true),
    "부산": new Trend("related_spots_duration_busan", true),
};

export const options = {
    scenarios: {
        related_spots_probe: {
            executor: "shared-iterations",
            vus: CONCURRENCY,
            iterations: TOTAL_REQUESTS,
            maxDuration: "5m",
        },
    },
    // 소규모 프로브이므로 실패율에 대한 pass/fail 기준만 최소로 둔다.
    thresholds: {
        http_req_failed: ["rate<0.1"],
    },
};

export default function () {
    const region = REGIONS[__ITER % REGIONS.length];
    const url = `${BASE_URL}/api/v1/related-spots?regionCategory=${encodeURIComponent(region)}`;
    const res = http.get(url, {
        tags: { name: "related-spots", region },
    });

    relatedSpotsDuration.add(res.timings.duration);
    regionDurations[region].add(res.timings.duration);

    check(res, {
        "status is 200": (r) => r.status === 200,
    });
}

function formatTrend(label, trend) {
    if (!trend) {
        return `${label} : duration metric 없음`;
    }
    const v = trend.values;
    return [
        `${label}`,
        `  avg  : ${v.avg.toFixed(1)} ms`,
        `  min  : ${v.min.toFixed(1)} ms`,
        `  med  : ${v.med.toFixed(1)} ms`,
        `  p90  : ${v["p(90)"].toFixed(1)} ms`,
        `  p95  : ${v["p(95)"].toFixed(1)} ms`,
        `  max  : ${v.max.toFixed(1)} ms`,
    ].join("\n");
}

export function handleSummary(data) {
    const overall = data.metrics.related_spots_duration;
    const failed = data.metrics.http_req_failed;

    const summaryLines = [
        "",
        "=== 연관 관광지 조회 응답시간 요약 ===",
        `BASE_URL        : ${BASE_URL}`,
        `REGIONS         : ${REGIONS.join(", ")} (요청마다 순환, 지역별 외부 API 병렬 호출 수: 3)`,
        `총 요청 수       : ${TOTAL_REQUESTS} (외부 API 총 호출 수 약 ${TOTAL_REQUESTS * 3}회)`,
        `동시 실행 수(VU) : ${CONCURRENCY}`,
        "",
        formatTrend("[전체]", overall),
        "",
        formatTrend("[서울]", data.metrics.related_spots_duration_seoul),
        "",
        formatTrend("[인천]", data.metrics.related_spots_duration_incheon),
        "",
        formatTrend("[부산]", data.metrics.related_spots_duration_busan),
        "",
        failed ? `실패율 : ${(failed.values.rate * 100).toFixed(2)}%` : "",
        "",
    ].join("\n");

    return {
        stdout: summaryLines,
    };
}
