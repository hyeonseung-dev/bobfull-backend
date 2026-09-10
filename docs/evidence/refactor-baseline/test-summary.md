# Test Summary

## Measurement

- 측정일: 2026-09-09
- 명령: `env -u OPENAI_API_KEY ./gradlew test jacocoTestReport --no-daemon --no-parallel --console=plain`
- OpenAI 실측 평가: 기본 `test` task의 `openai-evaluation` 태그 제외로 미실행
- 결과: 930 tests, 0 failures, 50 skipped, 100% successful
- 실행 시간: 2m 55.20s

## JaCoCo

| 지표 | Covered | Missed | Coverage |
|---|---:|---:|---:|
| Instruction | 27,090 | 5,973 | 81.93% |
| Branch | 1,154 | 441 | 72.35% |
| Line | 4,976 | 680 | 87.98% |
| Complexity Coverage | 1,897 | 608 | 75.73% |
| Method | 1,459 | 226 | 86.59% |
| Class | 407 | 16 | 96.22% |

- XML: `build/reports/jacoco/test/jacocoTestReport.xml`
- HTML: `build/reports/jacoco/test/html/index.html`

이 수치는 JaCoCo XML의 report-level counter를 기준으로 계산했다.
