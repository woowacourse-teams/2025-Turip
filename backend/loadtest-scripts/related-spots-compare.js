// 연관 관광지 조회 API - RestClient vs WebClient vs 가상 스레드 비교 부하테스트
//
// ── 이 스크립트가 이전 버전과 달라진 점 ─────────────────────────────────────
//
// 1) 폴백 응답을 성공으로 집계하지 않는다
//    서버는 외부 API가 전부 실패해도 폴백 데이터를 담아 HTTP 200을 반환한다.
//    이전 스크립트는 `status is 200`만 확인했기 때문에, 외부 API가 호출량 제한으로
//    차단된 응답까지 전부 성공으로 집계됐다.
//    더 문제가 되는 것은 latency다. 폴백 경로는 외부 API 응답을 기다리지 않고
//    즉시 반환되므로 응답이 매우 빠르다. 고부하 구간에서 폴백 비율이 올라가면
//    평균과 p95가 오히려 좋아지는 방향으로 왜곡된다.
//    이를 막기 위해 서버가 내려주는 X-Data-Source 헤더로 응답을 분류하고,
//    외부 API를 실제로 호출한 응답(api)만 latency 집계에 넣는다.
//
// 2) VU 상한을 50에서 10으로 낮춘다
//    이전 테스트는 VU 50까지 올렸는데, 그 구간에서는 외부 API가 호출량 제한으로
//    차단되어 대부분 폴백으로 떨어졌다. 즉 "서버의 처리 구조"가 아니라
//    "외부 API의 차단 정책"을 측정하고 있었다.
//    VU 10이면 요구 동시 외부 호출이 30건으로, WebClient 커넥션풀(10)의 3배이고
//    톰캣 스레드 제한(10)과도 맞물려 구조적 차이가 드러나는 구간이다.
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
//   VU=2  : 외부 호출 6건.  저부하 기준점. 세 조건 모두 여유 있는 구간.
//   VU=3  : 외부 호출 9건.  WebClient 커넥션풀(10) 포화 직전.
//   VU=4  : 외부 호출 12건. 커넥션풀 초과 시작 (대기/큐잉 발생 예상 지점).
//   VU=6  : 외부 호출 18건. 커넥션 대기 누적. 톰캣 스레드(10)도 압박받기 시작.
//   VU=8  : 외부 호출 24건. 블로킹 조건(restclient/vthread)의 톰캣 스레드 포화 근처.
//   VU=10 : 외부 호출 30건. 커넥션풀 3배 과포화. 세 조건의 차이가 가장 뚜렷할 구간.
//
//   각 단계는 5초 램프 + 25초 유지 = 30초. 6단계 + 종료 램프 5초 = 총 3분 5초.
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
import { check } from "k6";
import { Trend, Counter, Rate } from "k6/metrics";

const BASE_URL = __ENV.BASE_URL || "http://turip.o-r.kr";
const VARIANT = __ENV.VARIANT || "unknown"; // restclient | webclient | vthread
const REGIONS = ["서울", "인천", "부산"];

const DATA_SOURCE_HEADER = "X-Data-Source";

const STAGES = [
    { vus: 2, label: "VU=2  (저부하 기준점, 외부 호출 6건)" },
    { vus: 3, label: "VU=3  (커넥션풀 포화 직전, 9건)" },
    { vus: 4, label: "VU=4  (커넥션풀 초과 시작, 12건)" },
    { vus: 6, label: "VU=6  (커넥션 대기 누적, 18건)" },
    { vus: 8, label: "VU=8  (톰캣 스레드 포화 근처, 24건)" },
    { vus: 10, label: "VU=10 (커넥션풀 3배 과포화, 30건)" },
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

// 외부 API를 실제로 호출해 응답한 요청만 담는 latency.
// 이 테스트에서 비교해야 할 "진짜" 응답 시간은 이 지표다.
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
        // 폴백 비율이 높아지는 것 자체가 관찰 대상이므로 빌드를 실패시키지는 않는다.
        // 다만 집계에서 눈에 띄도록 임계값은 걸어둔다.
        spots_fallback_rate: [{ threshold: "rate<0.10", abortOnFail: false }],
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
        return;
    }

    const isFallback = dataSource.startsWith("fallback");
    fallbackRate.add(isFallback);

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
}

export function handleSummary(data) {
    const m = data.metrics;

    function fmt(trend) {
        if (!trend || !trend.values || trend.values.count === 0) {
            return "해당 응답 없음";
        }
        const v = trend.values;
        return [
            `건수 ${v.count}`,
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
        `총 요청  : ${totalReqs} (외부 API 호출 약 ${totalReqs * 3}회)`,
        "",
        "── 응답 시간 ──────────────────────────────────────────",
        `외부 API 응답 : ${fmt(m.spots_duration_api)}`,
        `폴백 응답     : ${fmt(m.spots_duration_fallback)}`,
        "  ※ 비교해야 할 지표는 '외부 API 응답'이다.",
        "     폴백은 외부 호출을 기다리지 않아 빠르므로 섞어서 보면 안 된다.",
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
            "!! 주의: 폴백 비율이 10%를 넘는다.",
            "   외부 API가 차단되고 있을 가능성이 높으므로, 부하를 더 낮추거나",
            "   호출 간격을 두고 다시 측정하는 것을 권한다.",
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
