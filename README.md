# 🥚 Egg-flow: Team5 Web IDE Backend 🚀

> **"Code anywhere, build anything."**
> 개발 초보자들이 알을 깨고 나오듯, 성장의 흐름(Workflow)을 매끄럽게 탈 수 있도록 돕는 B2B형 협업 Web IDE **'Egg-flow'**의 백엔드 레포지토리입니다.

---

## 🔥 Our Differentiator: 왜 'Egg-flow'인가요?
단순히 브라우저에서 코드만 치는 IDE? 놉! 🙅‍♂️ 저희의 진짜 차별점은 **"완벽한 통제와 관리가 가능한 관리자(Admin) 페이지"**에 있습니다. 

* **👨‍🏫 리더와 멘토를 위한 대시보드:** 부트캠프 운영자나 팀 리더가 코호트별 프로젝트 현황과 사용자 계정을 한눈에 관리할 수 있습니다.
* **🛡️ 빈틈없는 보안 로그:** 누가 권한 없이 남의 프로젝트를 기웃거렸는지, 로그인 실패는 몇 번 했는지 싹 다 기록하고 추적합니다.
* **👑 세분화된 권한 시스템:** Owner, Editor, Viewer로 역할을 나누어 안전하게 협업하고, 관리자는 이 모든 것을 모니터링합니다.

---

## ✨ 핵심 기능 (Core Features)
* **초스피드 셋업:** 이메일 & 구글 소셜 로그인으로 가입하고 바로 프로젝트 생성!
* **실시간 협업 끝판왕:** 코드 라인에 직접 남기는 댓글과 WebSocket 기반 실시간 채팅으로 피드백 속도 UP ⚡
* **동시 수정 대참사 방어 (Lock):** 내가 파일 수정 중일 땐 철벽 방어! 덮어쓰기 충돌을 막아주는 똑똑한 잠금(Lock) 기반 동시성 제어.
* **IDE 본질에 충실:** 파일 트리, 폴더/파일 CRUD, Monaco Editor 기반의 깔끔한 코드 조회 및 저장.

---

## 👨‍💻 Team 5 (도메인별 역할 분담)
우리는 4명이 각자의 도메인을 확실하게 책임지는 구조로 빡세게 달립니다! 🏃‍♂️💨

| 담당자 | 담당 도메인 (Domain) | 내가 책임지는 기능들 🛠️ |
| :--- | :--- | :--- |
| **김다은** | Auth / User / Profile | 회원가입, 로그인(JWT), 구글 소셜 로그인, 내 정보/프로필 관리 |
| **정윤서** | Project / ProjectMember | 프로젝트 CRUD, 멤버 초대, 권한(Owner/Editor/Viewer) 매트릭스 관리 |
| **최희원** | IDE / File | Monaco Editor 연결, 파일 트리, 파일/폴더 CRUD, 코드 저장 및 파일 잠금(Lock) |
| **조아영** | Chat / Log / Comment | 실시간 채팅(WebSocket), 접속자 상태 표시, 활동 로그, 코드 라인 댓글 |

---

## 🛠️ 기술 스택 (Tech Stack)
현업에서 가장 사랑받는 스택들로 꽉 채웠습니다.

* **Language:** Java
* **Framework:** Spring Boot, Spring Security
* **Database:** PostgreSQL (개발용 H2), Spring Data JPA
* **Realtime & Cache:** WebSocket, STOMP, Redis (예정)
* **Auth:** JWT, OAuth2 Client
* **Docs:** Swagger / Springdoc

---

## 📅 타임라인 (3 Weeks Sprint)
3주 안에 코어 기능 완성을 목표로 달리는 미친 스케줄 ⏱️

* **1주차:** 기반 다지기 및 핵심 API (설계, Auth, Project CRUD)
* **2주차:** IDE 본질 구현 (File CRUD, Monaco Editor 연동, 코드 저장)
* **3주차:** 실시간의 마법 (WebSocket 채팅, 접속자, Lock 기반 동시 수정 방지) + 예외 처리 & 버그 사냥 🐛

---

## 🤙 절대 지켜! 우리들의 규칙 (Conventions)

### 📌 Git 브랜치 전략
> `main`과 `dev` 브랜치에 직접 Push하는 자, 살아남지 못하리라... ☠️ 무조건 **Pull Request**로만 병합합니다.

* `main`: 배포 가능한 갓벽한 상태
* `dev`: 우리들의 피땀눈물이 모이는 개발 통합 브랜치
* **작업 브랜치 명명법:** `{타입}/{도메인}-{작업내용}`
  * 예시: `feature/auth-login`, `fix/project-member-duplicate`, `refactor/project-permission`

### 💬 커밋 메시지 폼 미쳤다
`type: 작업 내용` 형식으로 깔끔하게 작성합니다.

* `feat`: 새로운 기능 추가 (짜릿함)
* `fix`: 버그 수정 (다행임)
* `docs`: 문서 수정 (README 등)
* `style`: 코드 포맷팅, 세미콜론 등 (기능 변경 X)
* `refactor`: 코드 리팩토링
* `test`: 테스트 코드 추가/수정
* `chore`: 빌드, 설정, 패키지 매니저 수정

### 🤝 PR (Pull Request) 통과 조건
1. **최소 1명의 코드 리뷰 (Approve) 필수!** 👀
2. 충돌(Conflict)은 알아서 싹 풀고 올릴 것.
3. API 명세서랑 찰떡같이 맞는지 확인 완.
4. (중요) `application.yml` 같은 민감한 시크릿 키값 절대 올리지 않기! 🚨

---

## 📡 공통 API 응답 포맷
프론트와 편안하게 파싱할 수 있도록 응답을 통일했습니다.

**✅ 성공했을 때**
```json
{
  "success": true,
  "message": "요청이 성공했습니다.",
  "data": { ... }
}
