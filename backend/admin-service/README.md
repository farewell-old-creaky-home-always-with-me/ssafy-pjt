# Admin Service

관리자 기능과 공공 데이터 수집 배치를 담당하는 서비스입니다. 공지/QnA 관리 API와 주택, 지역, 상권, 환경, 인구, CCTV, 뉴스 데이터를 수집하는 배치 작업을 포함합니다.

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring MVC
- Spring Security
- Spring Batch
- MyBatis
- MySQL 8
- PDFBox
- Gradle

## 주요 기능

- 관리자 배치 실행 API
- 공공 데이터 수집 및 저장
- 배치 실행 로그 관리
- 배치 리포트 생성/조회 연계
- 관리자 공지사항 등록, 수정, 삭제
- 관리자 QnA 답변/상태 관리

## 실행

백엔드 루트에서 실행합니다.

```bash
cd backend
./gradlew :admin-service:bootRun
```

기본 포트: `8081`

Gateway를 함께 실행하면 관리자 API는 `http://localhost:8080/api/admin/**` 경로로 접근합니다.

## 테스트

```bash
cd backend
./gradlew :admin-service:test
```

## 주요 구조

```text
src/main/java/com/ssafy/home/
├── admin/     # 관리자 배치 실행 API
├── batch/     # Spring Batch Job, Step, Reader/Processor/Writer
├── external/  # 외부 공공 API 클라이언트
├── notice/    # 관리자 공지 API
├── qna/       # 관리자 QnA API
└── global/    # 공통 응답, 예외, 설정
```

## 리소스

- 설정: `src/main/resources/application.yml`
- 로컬 Docker 설정: `src/main/resources/application-local.yml`
- MyBatis Mapper: `src/main/resources/mapper/`
- 테스트 스키마/데이터: `src/test/resources/`
