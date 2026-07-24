---
topic: lint-rules
last_compiled: 2026-04-13
source_count: 3
status: active
---

# Lint Rules

## 목적 [coverage: high — 3 sources]

`:lint-rules` 모듈은 Turip 프로젝트에 특화된 커스텀 Android Lint 규칙을 정의한다. `:app` 모듈에 `lintChecks(project(":lint-rules"))`로 적용되어 빌드 시 코드 스타일 위반을 자동으로 감지한다. 현재 함수 타입 파라미터 명명 규칙 하나를 강제한다.

## 아키텍처 [coverage: high — 3 sources]

```
:lint-rules (java-library + Kotlin JVM, Java 17)
  ├─ TuripIssueRegistry (IssueRegistry 구현체)
  │    └─ issues: listOf(NamedFunctionTypeParameterDetector.ISSUE)
  ├─ NamedFunctionTypeParameterDetector (Detector 구현체)
  │    ├─ ISSUE: Issue (id="UnnamedFunctionTypeArgument")
  │    └─ visitMethod(): UAST 기반 파라미터 분석
  └─ META-INF/services/com.android.tools.lint.client.api.IssueRegistry
       └─ com.on.turip.lint.TuripIssueRegistry (서비스 등록)
```

## 의존 관계 [coverage: medium — 2 sources]

```
:lint-rules
  └─ lint-api:31.9.1 (com.android.tools.lint)

:app
  └─ lintChecks(project(":lint-rules"))  → 빌드 시 자동 적용
```

Lint 모듈은 Android 런타임 의존성이 없는 순수 JVM 라이브러리다.

## API 표면 [coverage: high — 3 sources]

### `NamedFunctionTypeParameterDetector.ISSUE`

```
id:          "UnnamedFunctionTypeArgument"
category:    Category.CORRECTNESS
priority:    6
severity:    Severity.WARNING
scope:       JAVA_FILE + TEST_SOURCES
```

### 감지 로직

`visitMethod()`에서 모든 `UMethod`의 파라미터를 순회한다. 파라미터 타입이 함수 타입(lambda 타입)인 경우, 타입 시그니처를 깊이 추적하는 브라켓 파싱으로 분석하여 파라미터 이름이 없는(unnamed) 인자가 있으면 경고를 보고한다.

**예시 (위반)**:
```kotlin
fun foo(action: (Int, String) -> Unit)  // ← Int, String이 unnamed → WARNING
```

**예시 (준수)**:
```kotlin
fun foo(action: (count: Int, name: String) -> Unit)  // ← named → OK
```

## 데이터 [coverage: low — 1 source]

현재 등록된 이슈: 1개 (`UnnamedFunctionTypeArgument`)

`TuripIssueRegistry.vendor`: `"Turip"`

## 주요 결정사항 [coverage: medium — 2 sources]

1. **빌드 타임 강제**: 런타임 에러가 아닌 경고(WARNING)로 설정하여 빌드를 막지 않고 개발자에게 안내한다. CI에서 `lint --check` 실행 시 위반을 보고할 수 있다.
2. **UAST 기반**: JVM/Kotlin 소스 모두에 적용되는 UAST(Unified AST)를 사용하여 Kotlin 함수 타입을 정확하게 분석한다.
3. **테스트 소스 포함**: `scope`에 `TEST_SOURCES`를 포함하여 테스트 코드에서도 동일한 규칙을 적용한다.

## 주의사항 [coverage: low — 1 source]

- `lint-api` 버전(`31.9.1`)은 Android Gradle Plugin 버전과 맞춰야 한다. 버전 불일치 시 Lint 실행이 실패할 수 있다.
- 현재 이슈가 1개뿐이므로 규칙 추가 시 `TuripIssueRegistry.issues` 목록에 함께 등록해야 한다.

## 출처 [coverage: high — 3 sources]

- [NamedFunctionTypeParameterDetector.kt](../../lint-rules/src/main/java/com/on/turip/lint/NamedFunctionTypeParameterDetector.kt)
- [TuripIssueRegistry.kt](../../lint-rules/src/main/java/com/on/turip/lint/TuripIssueRegistry.kt)
- [META-INF services](../../lint-rules/src/main/resources/META-INF/services/com.android.tools.lint.client.api.IssueRegistry)
