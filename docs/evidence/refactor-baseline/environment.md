# Environment

- OS: macOS 26.6.2, Apple Silicon
- Java: OpenJDK 17.0.18
- Gradle Wrapper: 9.5.1
- Docker Desktop: 29.4.3
- SonarScanner for Gradle: 7.5.0.8588
- SonarQube image: `sonarqube:25.6.0.109173-community`
- SonarQube image digest: `sonarqube@sha256:4de9333cd53b3d66b0cdfc6c84c0be1e1e7a15565c66f76e30859575b0d5415a`

SonarScanner 공식 요구사항은 Gradle 7.6.4 또는 8.4 이상, Java 17 이상이다. Java 17은 scanner 런타임 지원이 deprecated 상태이므로 scanner의 기본 JRE auto-provisioning을 비활성화하지 않는다.
