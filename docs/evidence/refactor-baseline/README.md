# Refactor Baseline Evidence

이 디렉터리는 리팩터링 전 정적 분석 기준선을 보관한다.

- 프로덕션 소스 기준 SHA: `d813dd5bb46919451df504399f0a57792b7f8467`
- 측정 환경 기준 develop SHA: `0734e6c8a40bfcdc9f429b15a2c965dd022e4736`
- 측정 범위: 루트 프로젝트의 `src/main` Java 소스
- 외부 AI 평가 테스트: 기본 `test`에서 제외

두 SHA 사이에는 `src/main` 프로덕션 소스 변경이 없으며, `0734e6c8a40bfcdc9f429b15a2c965dd022e4736`은 테스트 재현성 안정화 변경만 포함한다.

## 재현 명령

```bash
docker run -d --rm --name bobfull-sonarqube-baseline \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  sonarqube:25.6.0.109173-community

./gradlew --stop
env -u OPENAI_API_KEY ./gradlew test jacocoTestReport --no-daemon --no-parallel --console=plain
./gradlew sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token="$SONAR_TOKEN"
```

`SONAR_TOKEN`은 로컬 SonarQube에서 생성해 셸 환경변수로만 전달한다. 이 저장소와 Evidence 문서에는 토큰이나 비밀번호를 기록하지 않는다.

## 산출물

- JaCoCo XML: `build/reports/jacoco/test/jacocoTestReport.xml`
- JaCoCo HTML: `build/reports/jacoco/test/html/index.html`
- 테스트 결과: `build/reports/tests/test/index.html`
- SonarQube 수치: [static-analysis-before.md](static-analysis-before.md)
- SonarQube 원본 metrics: [sonar-metrics-before.json](sonar-metrics-before.json)

로컬 SonarQube 컨테이너는 측정 후 `docker stop bobfull-sonarqube-baseline`으로 종료한다. `--rm` 옵션 때문에 컨테이너 종료 시 내부 측정 데이터는 유지되지 않으며, 이 문서의 수치가 재현 Evidence다.
