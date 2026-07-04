# signalServer

WebRTC 화상회의를 위한 시그널링 서버입니다. Spring Boot의 STOMP over WebSocket(SockJS) 기반으로, 클라이언트와 미디어 서버(mediasoup SFU) 사이의 시그널링 중계와 방·참가자 상태 관리를 담당합니다.

미디어 트래픽은 별도의 [미디어 서버](../mediaServer)가 처리하며, 이 서버는 다음을 책임집니다.

- 사용자·방 생성과 입장 검증 (REST)
- 클라이언트 ↔ 미디어 서버 간 시그널링 메시지 중계 (STOMP)
- 방 단위 상태(참가자, 손들기, 미디어 옵션, 프로듀서) 관리와 브로드캐스트
- 채팅·이모지 등 방 상호작용 중계
- 연결 종료(탭 닫힘·새로고침·순단) 시 유예 기반 퇴장 처리

## 기술 스택

- Java 21, Spring Boot 3.5 (Web, WebSocket, JDBC, Validation, Retry)
- H2 인메모리 DB (사용자·방 영속), `ConcurrentHashMap` 기반 세션 저장소
- JUnit 5, AssertJ, Mockito

## 구조

```
controller/   REST(rest)·STOMP(socket) 진입점 — 와이어 DTO 조립과 전송
service/      유스케이스 — 도메인 결과 반환, 전송 방식을 모름
domain/       Room, RoomSession(애그리거트), Participant, User + 값객체(UserName, ProfileColor, MediaOption)
repository/   JDBC(room, member) + 인메모리 세션 저장소
infrastructure/ StompMessageSender — sendToUser / broadcast / sendToMediaServer 프리미티브
config/       WebSocket·CORS 설정, 핸드셰이크 인증, 목적지 가드
exception/    REST·STOMP 예외 → 에러 응답 매핑
```

- 서비스 계층은 전송(REST/STOMP)을 모르고 도메인 결과만 반환합니다. 와이어 DTO 조립과 발송은 경계 계층(컨트롤러·리스너)이 담당합니다.
- 값객체(`UserName`, `ProfileColor`, `MediaOption`)는 생성 시점에 불변식을 검증합니다 — 유효하지 않은 값은 존재할 수 없습니다.

## API 개요

### REST

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/api/users` | 사용자 등록 → `userId` |
| POST | `/api/rooms` | 방 생성 → 10자리 `roomId` |
| GET | `/api/rooms/{roomId}/validate` | 존재·정원 검증 |
| POST | `/api/rooms/leave` | 퇴장 (beforeunload beacon용) |

에러는 `{code, message}` 형식으로, `ErrorCode`에 따라 400/404/409/500으로 매핑됩니다.

### WebSocket (STOMP)

- 엔드포인트: `/ws` (SockJS), 쿼리 파라미터 `userId` 필수 — 없으면 403
- 클라이언트 발행: `/app/**` (그 외 목적지 SEND는 차단)
- 개인 응답: `/user/queue/replies` — 요청의 `correlationId`로 짝을 맞추는 RPC 스타일
- 방 브로드캐스트: `/topic/room/{roomId}/{participant|rtls|producer/remove|leave|device|handup|emoji|chat}`

| 목적지 | 설명 |
|---|---|
| `/app/signal/join`, `/app/signal/resync` | 방 입장·재동기화 |
| `/app/signal/capabilities`, `/app/signal/dtls(/connect)`, `/app/signal/rtls`, `/app/signal/consumerParams` | 미디어 서버로 릴레이되는 WebRTC 시그널링 |
| `/app/signal/producer/pause·resume`, `/app/consumer/pause·resume`, `/app/producer/remove` | 트랙 제어 |
| `/app/chat/send`, `/app/emoji`, `/app/device`, `/app/handUp`, `/app/leave` | 방 상호작용 |
| `/app/media/**` | 미디어 서버 → 서버 응답 채널 (미디어 서버 principal 전용) |

### 연결 종료 처리

WebSocket이 끊기면 10초 유예 후 재접속이 없을 때 방에서 제거하고(`leave` 브로드캐스트 + 미디어 서버 통지) 사용자 레코드를 삭제합니다. 새로고침·순단은 유예 내 재접속 후 `/app/signal/resync`로 복구합니다.

## 미디어 서버 인증

미디어 서버는 `/ws?userId={id}&token={secret}`으로 접속하며, 핸드셰이크에서 토큰을 검증합니다. `/app/media/**` 발행도 미디어 서버 principal만 허용됩니다.

```bash
cp .env.example .env.local   # 값 채우기 (미디어 서버의 .env.local과 동일해야 함)
```

| 변수 | 설명 |
|---|---|
| `MEDIA_SERVER_ID` | 미디어 서버 접속 id (기본 `mediaServer`) |
| `MEDIA_SERVER_TOKEN` | 공유 시크릿 — `openssl rand -hex 32` 권장 |

## 실행

```bash
./gradlew bootRun    # http://localhost:8080
./gradlew test       # 전체 테스트 (단위 + 통합)
```

로컬 개발은 `.env.local` 없이도 기본값으로 동작합니다. 프론트엔드는 `localhost:3000` 기준으로 CORS가 허용되어 있습니다.

## 관련 저장소

- **front** — Next.js 클라이언트 (SockJS + @stomp/stompjs, mediasoup-client)
- **mediaServer** — Node.js mediasoup SFU
