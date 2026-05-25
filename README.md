# Team5 Web IDE

Team5 Web IDE 서비스의 백엔드 레포지토리입니다.  
사용자가 프로젝트를 만들고, 파일을 편집하고, 팀원과 실시간으로 협업할 수 있는 서비스를 목표로 합니다.

## 프로젝트 개요

### 서비스 이름

Team5 Web IDE

### 핵심 기능

- 회원가입, 로그인, 구글 로그인, 내 정보/프로필 관리
- 프로젝트 생성, 조회, 수정, 삭제
- 프로젝트 멤버 초대와 권한 관리
- IDE 파일 트리, 파일/폴더 생성, 코드 조회 및 저장
- 실시간 채팅
- 프로젝트 접속자 표시
- 코드 라인 댓글
- 관리자 대시보드, 사용자/프로젝트/보안 로그 관리

### 관련 문서

| 구분 | 링크 |
| --- | --- |
| 메인 Notion / API 1 | https://www.notion.so/API-1-bd5d3570df81836f81fc81d60d05dca5 |
| 도메인 설계 / ERD | https://www.notion.so/32bd3570df81824b94cf81a3d7f9d6d0 |
| API 설계서 | https://www.notion.so/36b044633ac18134afe7fcf477b75562 |
| Figma | https://www.figma.com/design/uJerc6vqBIaH3ZIxUoa5l2/WebIDE?node-id=136-1084&p=f&t=YFJNCiRACj1VGtIH-0 |
| 기획서 | https://www.notion.so/WEB-IDE-35b8294ef2a88063b7e0e4b226d94c2f |

## 팀 구성과 역할

총 4명이 도메인별로 나누어 개발합니다.

| 담당자 | 담당 도메인 | 주요 기능 |
| --- | --- | --- |
| 김다은 | Auth / User / Profile | 회원가입, 로그인, 구글 로그인, 내 정보, 프로필, 회원탈퇴 |
| 정윤서 | Project / ProjectMember | 프로젝트 CRUD, 멤버 초대, 권한 관리 |
| 최희원 | IDE / File | 파일 트리, 파일/폴더 생성, 코드 조회/저장, 파일 잠금 |
| 조아영 | Chat / Presence / Log / Comment | 채팅, 접속자, 활동 로그, 코드 라인 댓글 |

## 개발 일정표

작성일: 2026-05-25  
아래 일정은 팀 상황에 따라 조정할 수 있습니다.

| 기간 | 작업 | 담당 |
| --- | --- | --- |
| 1주차 | 도메인 설계, ERD, API 명세 정리 | 전체 |
| 1주차 | 백엔드 레포 세팅 완료, 브랜치 전략, 공통 응답/예외 규칙 정리 | 정윤서 |
| 2주차 | Auth / User / Profile 구현 | 김다은 |
| 2주차 | Project / ProjectMember 구현 | 정윤서 |
| 2~3주차 | IDE / File 구현 | 최희원 |
| 3주차 | Chat / Presence / Comment / Log 구현 | 조아영 |
| 4주차 | 통합 테스트, API 연결, 버그 수정 | 전체 |
| 4주차 | 발표/시연 준비, README와 문서 정리 | 전체 |

## MVP 범위

### 1차 MVP

- Auth / User
- Project / ProjectMember
- IDE / File
- 기본 Chat
- 기본 Presence
- 기본 Comment
- 관리자 페이지 기본 조회 API

### 2차 구현 또는 보류

- 프로젝트 나가기
- OWNER 양도
- 초대 수락/거절 기반 invitation 기능
- ProfileHistory 전체 이력
- 상세 관리자 로그 필터
- 정교한 의심 로그인 탐지

## 백엔드 기술 스택

현재 기준으로 예상하는 기술 스택입니다.

| 구분 | 기술 |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Build | Gradle |
| DB | PostgreSQL H2 |
| ORM | Spring Data JPA |
| Auth | Spring Security, JWT |
| Realtime | WebSocket, STOMP |
| Cache / Presence | Redis 또는 서버 메모리 |
| API Docs | Swagger / Springdoc |

## 추천 패키지 구조

```plain text
src/main/java/com/team5/webide
├── WebIdeApplication.java
├── global
│   ├── config
│   ├── security
│   ├── exception
│   ├── response
│   └── common
└── domain
    ├── auth
    ├── user
    ├── project
    │   ├── controller
    │   ├── service
    │   ├── repository
    │   ├── entity
    │   └── dto
    ├── file
    ├── chat
    ├── presence
    ├── comment
    ├── activity
    └── admin
```

## 공통 API 응답 규칙

팀 합의 후 전체 API에 동일하게 적용합니다.

### 성공 응답

```json
{
  "success": true,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

### 실패 응답

```json
{
  "success": false,
  "code": "PROJECT_NOT_FOUND",
  "message": "존재하지 않는 프로젝트입니다.",
  "data": null
}
```

## Git 브랜치 규칙

### 기본 브랜치

| 브랜치 | 설명 |
| --- | --- |
| `main` | 배포 가능한 안정 버전 |
| `dev` | 개발 통합 브랜치 |
| `feature/*` | 기능 개발 브랜치 |
| `fix/*` | 버그 수정 브랜치 |
| `docs/*` | 문서 작업 브랜치 |
| `refactor/*` | 구조 개선 브랜치 |

### 브랜치 이름 규칙

```plain text
feature/{도메인}-{작업내용}
fix/{도메인}-{작업내용}
docs/{문서내용}
refactor/{도메인}-{작업내용}
```

예시:

```plain text
feature/auth-login
feature/project-create
feature/file-tree
feature/chat-websocket
fix/project-member-duplicate
docs/readme
refactor/project-permission
```

## 커밋 메시지 규칙

### 형식

```plain text
type: 작업 내용
```

### type 목록

| type | 의미 |
| --- | --- |
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 |
| `style` | 코드 포맷팅, 세미콜론 등 기능 변경 없는 수정 |
| `refactor` | 리팩토링 |
| `test` | 테스트 코드 추가/수정 |
| `chore` | 빌드, 설정, 패키지 등 기타 작업 |

### 예시

```plain text
feat: 프로젝트 생성 API 구현
fix: 프로젝트 멤버 중복 추가 예외 처리
docs: README 협업 규칙 추가
refactor: 프로젝트 권한 검증 로직 분리
test: 프로젝트 생성 서비스 테스트 추가
chore: 초기 Gradle 설정 추가
```

## Push 규칙

### 절대 직접 push하지 않는 브랜치

- `main`
- `dev`

`main`, `dev` 브랜치에는 직접 push하지 않고 Pull Request로만 병합합니다.

### 작업 순서

```plain text
1. dev 최신 상태 받기
2. 기능 브랜치 생성
3. 작업 및 커밋
4. 원격 브랜치로 push
5. Pull Request 생성
6. 코드 리뷰 후 dev로 merge
```

### 명령어 예시

```bash
git checkout dev
git pull origin dev
git checkout -b feature/project-create

git add .
git commit -m "feat: 프로젝트 생성 API 구현"
git push origin feature/project-create
```

## Pull Request 규칙

PR은 `dev` 브랜치를 대상으로 생성합니다.

### PR 제목

```plain text
[도메인] 작업 내용
```

예시:

```plain text
[Project] 프로젝트 생성 API 구현
[Auth] 로그인 API 구현
[File] 파일 트리 조회 API 구현
```

### PR 본문에 포함할 내용

- 작업한 기능
- 변경한 파일 또는 구조
- 테스트 결과
- 리뷰어가 확인해야 할 부분
- 관련 이슈 또는 Notion 문서 링크

### Merge 기준

- 최소 1명 이상 코드 리뷰
- 충돌 없음
- 로컬 테스트 통과
- API 명세와 구현 내용 일치

## 코드 리뷰 체크리스트

- API URL, Method가 명세와 일치하는가
- Request / Response 형식이 명세와 일치하는가
- 권한 검사가 빠지지 않았는가
- 예외 상황을 처리했는가
- Entity 관계가 ERD와 맞는가
- 테스트 또는 실행 확인을 했는가
- 민감 정보가 커밋되지 않았는가

## 환경변수와 보안 규칙

- `application.yml`, `.env` 등 민감 정보가 들어간 파일은 커밋하지 않습니다.
- 예시 파일은 `application-example.yml` 형태로 공유합니다.
- JWT Secret, DB 비밀번호, OAuth Client Secret은 GitHub에 올리지 않습니다.
- 개인 로컬 설정은 각자 관리합니다.

## 로컬 실행 방법

프로젝트 세팅 후 아래 명령어로 실행합니다.

```bash
./gradlew bootRun
```

테스트 실행:

```bash
./gradlew test
```

## 팀 합의가 필요한 부분

- 공통 API 응답 래퍼 사용 여부
- DELETE 성공 응답을 `200 + message`로 할지 `204 No Content`로 할지
- Project owner를 `Project.ownerId`로도 저장할지 여부
- OWNER 1명 제한을 DB 제약으로 강제할지, 서비스 로직으로 처리할지
- Soft Delete 적용 범위
- Comment lineNumber 밀림 문제를 MVP에서 감수할지 여부
- Presence 비정상 퇴장 heartbeat 기준
