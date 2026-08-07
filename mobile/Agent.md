# Turip Mobile Agent Guide

이 프로젝트에서 Android 코드를 KMP로 옮기거나 화면을 복구할 때는 아래 원칙을 따른다.

## Core Rule

컴파일 에러나 import 누락이 발생했을 때 임의로 비슷한 컴포넌트, 모델, 유틸을 새로 만들지 않는다.

반드시 먼저 기존 Android 프로젝트(`../android`)에서 실제 원본 구현을 찾아 가져온 뒤, KMP에서 동작하지 않는 부분만 최소한으로 치환한다.

## Migration Flow

1. 누락된 import, 타입, 함수, 컴포넌트 이름을 확인한다.
2. `../android`에서 같은 이름의 원본 파일을 `rg`로 찾는다.
3. 원본 파일을 현재 KMP 모듈의 적절한 package로 가져온다.
4. package/import를 현재 KMP 구조에 맞춘다.
5. Android 전용 API만 최소 치환한다.
6. 화면 구조, spacing, typography, component composition은 원본을 유지한다.
7. 모듈 단위로 iOS/Android compile을 돌려 확인한다.
8. 다시 스캔해서 Android 전용 import가 남았는지 확인한다.

## What Not To Do

- import가 안 된다는 이유로 임의 UI를 만들지 않는다.
- 비슷해 보이는 placeholder screen으로 대체하지 않는다.
- 원본 Android 화면 구조를 확인하지 않고 화면을 재구성하지 않는다.
- feature impl 간 직접 의존을 새로 만들지 않는다.
- unrelated staged/user changes를 되돌리지 않는다.

## Common KMP Replacements

Android 원본을 가져온 뒤 필요한 경우에만 아래처럼 바꾼다.

- `com.on.turip.R` -> `com.on.turip.core.designsystem.generated.resources.Res`
- `androidx.compose.ui.res.stringResource` -> `org.jetbrains.compose.resources.stringResource`
- `androidx.compose.ui.res.painterResource` -> `org.jetbrains.compose.resources.painterResource`
- `@DrawableRes Int` -> `DrawableResource`
- `@StringRes Int` -> `StringResource`
- `LocalContext + ImageRequest.Builder(...)` -> `AsyncImage(model = value, ...)`
- `Timber` -> `Napier`
- `Hilt/@Inject` -> Koin module/provider
- Android domain package -> 현재 KMP `core.model` 또는 `core.domain` 위치

## Import Recovery Rule

import가 깨졌다면 아래 순서로 처리한다.

1. 같은 feature 안에 이미 포팅된 파일이 있는지 확인한다.
2. 없으면 `../android/app/src/main/java`에서 원본을 찾는다.
3. 원본이 다른 Android common component에 의존하면 그 component도 같이 추적한다.
4. 원본 의존 그래프를 필요한 만큼 가져온다.
5. KMP에서 컴파일되지 않는 Android API만 바꾼다.

이 과정 없이 새 컴포넌트나 새 모델을 만드는 것은 금지한다.

## Verification

화면 또는 component를 옮긴 뒤에는 최소한 아래를 확인한다.

```bash
rg -n "com\\.on\\.turip\\.ui\\.common|com\\.on\\.turip\\.ui\\.compose|com\\.on\\.turip\\.R|androidx\\.compose\\.ui\\.res|androidx\\.annotation|hiltViewModel|Timber|dagger|javax\\.inject|LocalContext|ImageRequest" <changed-module> -g '*.kt'
```

관련 모듈은 iOS와 Android 양쪽을 컴파일한다.

```bash
./gradlew :feature:<name>:impl:compileKotlinIosSimulatorArm64 :feature:<name>:impl:compileDebugKotlinAndroid
```

여러 feature를 건드렸다면 마지막에 `composeApp`까지 확인한다.

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64 :composeApp:compileDebugKotlinAndroid
```

## Current Project Direction

Android 프로젝트가 기준 구현이다. KMP 쪽은 Android의 화면, 컴포넌트, 모델, 유틸을 가능한 한 그대로 가져오고, 플랫폼 차이 때문에 필요한 부분만 작게 바꾼다.

화면이 다르게 보인다면 먼저 “내가 임의로 만든 부분이 있는지” 확인하고, 원본 Android 구현과 다시 비교한다.
