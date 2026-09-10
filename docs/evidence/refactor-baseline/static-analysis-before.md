# Static Analysis Before

## Measurement Condition

- SonarQube Community Build: `25.6.0.109173`
- Scanner: SonarScanner for Gradle `7.5.0.8588`
- Project key: `bobfull-backend-refactor-baseline`
- 분석 명령: `./gradlew sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token="$SONAR_TOKEN"`
- Compute Engine: SUCCESS, warning 0
- 프로덕션 소스 기준 SHA: `d813dd5bb46919451df504399f0a57792b7f8467`
- 측정 환경 기준 develop SHA: `0734e6c8a40bfcdc9f429b15a2c965dd022e4736`

## Before Metrics

| Metric | Value |
|---|---:|
| Bugs | 14 |
| Vulnerabilities | 0 |
| Security Hotspots | 4 |
| Code Smells | 243 |
| Duplicated lines | 236 |
| Duplicated lines density | 1.3% |
| Coverage | 84.6% |
| Line coverage | 88.1% |
| Branch coverage | 72.5% |
| Complexity | 2,158 |
| Cognitive complexity | 867 |
| NCLOC | 14,537 |

SonarQube Coverage는 SonarQube가 분석한 프로젝트 범위로 계산되며, JaCoCo report-level coverage와 집계 범위가 달라 수치가 일치하지 않을 수 있다.

## 주요 Complexity 경고

- `java:S3776`, CRITICAL, `RestaurantFeedbackInsightService.java:46`
  - `process()`의 Cognitive Complexity가 18이며 규칙 허용치 15를 초과한다.
  - 이번 Issue는 기준선 확보만 수행하므로 수정하지 않는다.
