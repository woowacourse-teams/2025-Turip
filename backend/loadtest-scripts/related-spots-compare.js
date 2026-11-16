// 연관 관광지 조회 API - RestClient vs WebClient vs 가상 스레드 비교 부하테스트
//
// ── 이 스크립트가 이전 버전과 달라진 점 ─────────────────────────────────────
//
// 1) 폴백 응답을 성공으로 집계하지 않는다
//    서버는 외부 API가 전부 실패해도 폴백 데이터를 담아 HTTP 200을 반환한다.
//    이전 스크립트는 `status is 200`만 확인했기 때문에, 외부 API가 호출량 제한으로
//    차단된 응답까지 전부 성공으로 집계됐다.
//    더 문제가 되는 것은 latency다. 폴백은 외부 API 호출이 실패하거나 타임아웃이
//    난 뒤에야 떨어지는 경로라, 그 실패를 기다린 시간이 응답 시간에 그대로 쌓인다.
//    실측에서 외부 API 응답은 p95 1.8초인데 폴백 응답은 p95 9.2초로 5배 느렸다.
//    둘을 섞어서 집계하면 "서버가 외부 API를 얼마나 잘 처리하는가"가 아니라
//    "외부 API 실패를 얼마나 오래 기다렸는가"를 함께 재게 된다.
//    이를 막기 위해 서버가 내려주는 X-Data-Source 헤더로 응답을 분류하고,
//    외부 API를 실제로 호출한 응답(api)만 latency 집계에 넣는다.
//
// 2) VU 상한을 50에서 20으로 낮춘다
//    이전 테스트는 VU 50까지 올렸는데, 그 구간에서는 외부 API가 호출량 제한으로
//    차단되어 대부분 폴백으로 떨어졌다. 즉 "서버의 처리 구조"가 아니라
//    "외부 API의 차단 정책"을 측정하고 있었다.
//    VU 20이면 요구 동시 외부 호출이 60건으로, WebClient 커넥션풀(10)의 6배이고
//    톰캣 스레드 제한(10)도 확실히 넘어서 구조적 차이가 드러난다.
//    단 이 구간에서도 폴백이 늘기 시작하면 다시 외부 API의 차단을 측정하는 것이므로,
//    요약에 찍히는 폴백 비율을 반드시 확인해야 한다.
//
// ── 비교 대상 세 가지 ───────────────────────────────────────────────────────
//   restclient : RestClient + 전용 스레드풀 10 (외부 호출 블로킹, 톰캣 블로킹)
//   webclient  : WebClient + Mono.zip + 컨트롤러 Mono 반환 (완전 논블로킹)
//   vthread    : RestClient + 가상 스레드 병렬 호출 (외부 호출 블로킹, 톰캣 블로킹)
//                spring.threads.virtual.enabled는 켜지 않는다. 그 플래그를 켜면
//                톰캣 워커까지 가상 스레드가 되어 바뀌는 변수가 둘이 된다.
//                여기서는 "병렬 실행 수단"만 바꿔 효과를 분리해 측정한다.
//
// ── 요청 1건당 외부 API 호출 수 ─────────────────────────────────────────────
//   서울/인천/부산은 각각 시군구 코드 3개를 가지므로 요청 1건 = 외부 API 3회 호출.
//   따라서 "요구 동시 외부 호출 수" = VU × 3 이다.
//
// ── VU 단계를 고른 이유 ────────────────────────────────────────────────────
//   think time 2초가 있으므로 VU 수가 곧 동시 요청 수는 아니다. VU는 "응답을 기다리거나
//   대기 중인 가상 사용자 수"이고, 실제 동시 요청 수는 응답 시간에 비례해 그보다 작다.
//   예컨대 응답이 500ms면 VU 1개가 2.5초 주기로 1건을 보내므로 VU 30에서 초당 약 12요청,
//   동시 요청은 그보다 훨씬 적다. 따라서 아래 단계는 "부하의 상대적 크기"로 읽어야 한다.
//
//   VU=2, 4, 6, 8   : 저부하 ~ 중부하. 세 조건 모두 여유 있는 구간이 어디까지인지 본다.
//   VU=10, 15       : 블로킹 조건(전용 풀 10)의 대기가 나타나기 시작하는 구간.
//   VU=20, 30       : 고부하. 세 조건의 차이와 한계 지점을 관찰한다.
//
//   VU 30 구간에서는 외부 API가 429(Too Many Requests)를 반환하기 시작한다.
//   조건 1 측정에서 전체 호출 15,939건 중 853건(5.4%)이 429였고, 요청 단위
//   폴백 비율은 10.68%였다. 폴백도 애플리케이션이 수행하는 정상 경로이므로
//   이를 포함한 전체 응답 시간을 비교 기본 지표로 삼되, 조건 간 폴백 비율 차이가
//   크면 해석에 반영한다.
//   참고로 폴백의 원인은 전용 스레드풀 포화가 아니었다. 측정 후 큐는 비어 있었고
//   (queue_remaining 50/50), 거부 예외도 없었다.
//
//   각 단계는 5초 램프 + 25초 유지 = 30초. 8단계 + 종료 램프 5초 = 총 4분 5초.
//   25초 유지는 VU당 수십 회 반복을 확보해 단계별 p95가 노이즈 없이 계산되도록 한 값이다.
//
// ── 실행 방법 ───────────────────────────────────────────────────────────────
//   각 조건을 배포한 뒤 VARIANT만 바꿔 한 번씩 실행한다.
//
//   BASE_URL=http://turip.o-r.kr VARIANT=restclient \
//     k6 run --out json=result-restclient.json loadtest-scripts/related-spots-compare.js
//
//   조건 사이에는 T 계열 인스턴스의 CPU 크레딧이 회복될 시간을 둔다.
//   크레딧이 바닥난 상태에서 측정하면 뒤에 돌린 조건이 불리해져 비교가 불공정해진다.

import http from "k6/http";
import { check, sleep } from "k6";
import { Trend, Counter, Rate } from "k6/metrics";

const BASE_URL = __ENV.BASE_URL || "http://turip.o-r.kr";
const VARIANT = __ENV.VARIANT || "unknown"; // restclient | webclient | vthread
const REGIONS = ["서울", "인천", "부산"];

const DATA_SOURCE_HEADER = "X-Data-Source";

// 반복 사이의 대기 시간(초).
// think time이 없으면 응답이 빠른 조건일수록 다음 요청을 더 빨리 보내게 되어,
// 같은 VU에서도 조건마다 총 요청 수와 외부 API 호출량이 크게 달라진다.
// 그러면 처리량이 높은 조건이 외부 API의 429에 더 많이 걸려 불리해지므로 비교가 불공정해진다.
// 2초는 조건 1의 응답 시간(avg 676ms, p95 1.8s)을 덮는 값이라, 반복 주기가 응답 시간보다
// 대기 시간에 지배되어 조건 간 요청 수가 비슷해진다.
const THINK_TIME = Number(__ENV.THINK_TIME || 2);

const STAGES = [
    { vus: 2, label: "VU=2  (저부하 기준점)" },
    { vus: 4, label: "VU=4" },
    { vus: 6, label: "VU=6" },
    { vus: 8, label: "VU=8  (중부하)" },
    { vus: 10, label: "VU=10 (블로킹 조건의 대기 시작 예상)" },
    { vus: 15, label: "VU=15" },
    { vus: 20, label: "VU=20 (고부하)" },
    { vus: 30, label: "VU=30 (한계 관찰)" },
];

const RAMP_TIME = __ENV.RAMP_TIME || "5s";
const HOLD_TIME = __ENV.HOLD_TIME || "25s";

function buildStages() {
    const stages = [];
    for (const stage of STAGES) {
        stages.push({ target: stage.vus, duration: RAMP_TIME });
        stages.push({ target: stage.vus, duration: HOLD_TIME });
    }
    stages.push({ target: 0, duration: RAMP_TIME });
    return stages;
}

// 폴백을 포함한 전체 응답 시간. 사용자가 실제로 체감하는 값이다.
// 폴백도 애플리케이션이 수행하는 정상 경로이므로, 세 조건을 비교할 때의 기본 지표로 쓴다.
const totalDuration = new Trend("spots_duration_total", true);

// 외부 API를 실제로 호출해 응답한 요청만 담는 latency.
// 전체 지표가 폴백 비율에 따라 흔들리므로, 순수한 처리 성능을 보려면 이쪽을 함께 본다.
const apiDuration = new Trend("spots_duration_api", true);

// 폴백으로 즉시 반환된 요청의 latency. 비교 대상은 아니지만,
// 폴백이 실제로 얼마나 빨리 반환되는지 확인해 왜곡 크기를 가늠하기 위해 따로 남긴다.
const fallbackDuration = new Trend("spots_duration_fallback", true);

// 폴백 비율. 이 값이 높은 구간의 latency 수치는 신뢰할 수 없다는 판단 근거가 된다.
const fallbackRate = new Rate("spots_fallback_rate");

// 폴백 사유별 건수. 부하로 인한 차단(failed)과 원래 데이터 없음(empty)을 나눠 센다.
const fallbackFailedCount = new Counter("spots_fallback_failed");
const fallbackEmptyCount = new Counter("spots_fallback_empty");
const httpErrorCount = new Counter("spots_http_error");
const missingHeaderCount = new Counter("spots_missing_header");

export const options = {
    scenarios: {
        related_spots_compare: {
            executor: "ramping-vus",
            startVUs: 0,
            stages: buildStages(),
            gracefulRampDown: "5s",
        },
    },
    thresholds: {
        // 폴백은 애플리케이션의 정상 경로이므로 임계값을 걸지 않는다.
        // 비율 자체는 요약에서 확인하고, 조건 간 차이가 크면 해석에 반영한다.
        http_req_failed: [{ threshold: "rate<0.50", abortOnFail: false }],
    },
};

export default function () {
    const region = REGIONS[__ITER % REGIONS.length];
    const url = `${BASE_URL}/api/v1/related-spots?regionCategory=${encodeURIComponent(region)}`;

    const res = http.get(url, {
        tags: { name: "related-spots", region, variant: VARIANT },
    });

    const vuTag = { vus: `${__VU}`, variant: VARIANT };

    if (res.status !== 200) {
        httpErrorCount.add(1);
        fallbackRate.add(false);
        check(res, { "status is 200": () => false });
        sleep(THINK_TIME);
        return;
    }

    // 서버가 내려준 데이터 출처로 응답을 분류한다.
    // 헤더 이름은 대소문자를 구분하지 않으므로 양쪽을 모두 확인한다.
    const dataSource = res.headers[DATA_SOURCE_HEADER] || res.headers["x-data-source"];

    if (!dataSource) {
        // 헤더가 없다는 것은 X-Data-Source를 추가하기 전 버전이 배포돼 있다는 뜻이다.
        // 이 경우 폴백 구분이 불가능하므로 측정값을 신뢰할 수 없다. 즉시 드러나도록 센다.
        missingHeaderCount.add(1);
        check(res, { "X-Data-Source 헤더 존재": () => false });
        sleep(THINK_TIME);
        return;
    }

    const isFallback = dataSource.startsWith("fallback");
    fallbackRate.add(isFallback);

    // 폴백 여부와 무관하게 모든 정상 응답을 담는다.
    totalDuration.add(res.timings.duration, vuTag);

    if (isFallback) {
        fallbackDuration.add(res.timings.duration, vuTag);
        if (dataSource === "fallback-failed") {
            fallbackFailedCount.add(1);
        } else if (dataSource === "fallback-empty") {
            fallbackEmptyCount.add(1);
        }
    } else {
        apiDuration.add(res.timings.duration, vuTag);
    }

    check(res, {
        "status is 200": (r) => r.status === 200,
        "외부 API 데이터로 응답": () => !isFallback,
    });

    sleep(THINK_TIME);
}

export function handleSummary(data) {
    const m = data.metrics;

    function fmt(trend) {
        // k6 Trend에는 count가 없다. avg 유무로 데이터 존재 여부를 판단한다.
        if (!trend || !trend.values || typeof trend.values.avg !== "number") {
            return "해당 응답 없음";
        }
        const v = trend.values;
        return [
            `avg ${v.avg.toFixed(1)}ms`,
            `p90 ${v["p(90)"].toFixed(1)}ms`,
            `p95 ${v["p(95)"].toFixed(1)}ms`,
            `max ${v.max.toFixed(1)}ms`,
        ].join(" / ");
    }

    function count(counter) {
        return counter && counter.values ? counter.values.count : 0;
    }

    const totalReqs = m.http_reqs ? m.http_reqs.values.count : 0;
    const fbRate = m.spots_fallback_rate ? m.spots_fallback_rate.values.rate : 0;
    const missing = count(m.spots_missing_header);

    const lines = [
        "",
        `=== 연관 관광지 조회 부하테스트 결과 [${VARIANT}] ===`,
        `BASE_URL : ${BASE_URL}`,
        `VU 단계  : ${STAGES.map((s) => s.vus).join(" -> ")}`,
        `think time : ${THINK_TIME}초 (조건 간 요청 수 편차를 줄이기 위함)`,
        `총 요청  : ${totalReqs} (외부 API 호출 약 ${totalReqs * 3}회)`,
        `평균 처리량 : ${m.http_reqs ? m.http_reqs.values.rate.toFixed(2) : "N/A"} req/s`,
        "",
        "── 응답 시간 ──────────────────────────────────────────",
        `전체 응답     : ${fmt(m.spots_duration_total)}   ← 조건 비교의 기본 지표`,
        `  외부 API분  : ${fmt(m.spots_duration_api)}`,
        `  폴백분      : ${fmt(m.spots_duration_fallback)}`,
        "  ※ 폴백도 애플리케이션이 수행하는 정상 경로이므로 '전체 응답'으로 비교한다.",
        "     다만 폴백은 외부 호출이 실패/타임아웃된 뒤에 떨어지는 경로라 대기 시간이",
        "     그대로 쌓인다. 폴백 비율이 조건마다 크게 다르면 그 영향을 함께 봐야 한다.",
        "",
        "── 폴백 현황 ──────────────────────────────────────────",
        `폴백 비율        : ${(fbRate * 100).toFixed(2)}%`,
        `  외부 API 실패  : ${count(m.spots_fallback_failed)}건 (부하로 인한 차단 포함)`,
        `  데이터 비어있음: ${count(m.spots_fallback_empty)}건`,
        `HTTP 에러        : ${count(m.spots_http_error)}건`,
        "",
    ];

    if (missing > 0) {
        lines.push(
            "!! 경고: X-Data-Source 헤더가 없는 응답이 " + missing + "건 있다.",
            "   헤더를 추가하기 전 버전이 배포된 것으로 보인다.",
            "   폴백 구분이 불가능하므로 이 측정 결과는 사용하면 안 된다.",
            ""
        );
    }

    if (fbRate > 0.1) {
        lines.push(
            "※ 폴백 비율이 10%를 넘는다. 외부 API가 429로 호출을 거부하고 있을 가능성이 높다.",
            "   폴백 자체는 정상 경로지만, 조건 간 폴백 비율 차이가 크면 전체 응답 시간을",
            "   비교할 때 그 차이를 함께 고려해야 한다.",
            ""
        );
    }

    lines.push(
        "단계별 상세는 --out json 으로 내보낸 결과를 vus 태그 기준으로 분리 집계할 것.",
        ""
    );

    const result = { stdout: lines.join("\n") };
    if (__ENV.JSON_SUMMARY_PATH) {
        result[__ENV.JSON_SUMMARY_PATH] = JSON.stringify(data, null, 2);
    }
    return result;
}
