# signalServer

이 레포는 WebRTC P2P 연결을 위한 시그널링 서버(Signaling Server)를 Spring Boot 기반 WebSocket으로 구현한 프로젝트입니다.<br>
클라이언트 간 직접 미디어를 주고받기 위해 필요한 SDP(offer/answer)와 ICE Candidate를 교환하는 중간 역할을 담당합니다.<br>
<br>
본 프로젝트는 순수 WebRTC + Custom Signaling으로 구현되며, 데이터베이스 없이 “로컬/메모리 기반” 운영을 목표로 합니다.<br>

## 역할
- WebSocket 연결 관리 및 사용자(UserId) 발급
- 방(roomId) 단위 참가자 관리
- WebRTC Signal 메시지 전달 (join, offer, answer, ice)
- 화면 공유 스트림 ID 발급 및 참여자 중계
- 채팅/손들기 이벤트 브로드캐스트
- 마이크, 비디오 전원 이벤트 브로드캐스트
- 연결 종료 시 자동 정리 (disconnect / refresh / close)

## 전체 구조
클라이언트가 WebSocket 연결 후 STOMP 프로토콜로 메시지를 publish/subscribe하며,
서버는 이를 SimpleBroker 또는 1:1 직접 전송 방식으로 전달합니다.

```scss
Client
 └── STOMP (WebSocket)
        ↓
SignalController
 ├── JoinOrchestrationService
 ├── LeaveOrchestrationService
 ├── ScreenOrchestrationService
 └── SignalMessagingService
        ↓
MessageBroker (SimpleBroker)
        ↓
Client (Single / Broadcast)
```

## 주요 기능
### 사용자 등록
- 클라이언트는 특정 roomId로 join 요청
- 서버는 uuid 기반 userId 생성 및 반환
- 반환 경로: /user/queue/userId

### 방 관리
- 사용자는 특정 roomId로 입장(join)
- 서버는 해당 방의 참가자 목록을 관리

### Offer / Answer 중계
- 참여자가 생성한 offer를 서버는 대상 사용자에게 전달
- 대상 사용자는 peerConnection을 생성하고 answer를 다시 서버로 전송
- 대상 사용자가 생성한 peerConnection에 대한 Answer를 서버는 보낸 사용자에게 전달
- offer 전달 시 참가자의 정보도 포함해 전달

### ICE Candidate 중계
- PeerConnection에서 생성된 ICE 후보를 실시간 전달
- 상대 PeerConnection에 candidate 즉시 추가 가능

### 화면 공유 관리
- 기존 join이 아닌 /app/signal/screen 으로 처리
- 화면 공유용 새로운 "screenId" 발급
- 본인을 제외한 모든 참여자에게 화면 공유 offer 발송

### 채팅, 리액션 관리
- 사용자는 같은 방에 있는 모든 사용자에게 전송 가능
- 서버는 chat,handUp 이벤트를 브로드캐스트하여 모든 참여자에게 전달

### 연결 종료 처리
- 탭 닫힘, 새로고침, 네트워크 끊김 등으로 WebSocket이 종료
  - 서버는 자동으로 방에서 해당 사용자 제거
  - 남아있는 사용자에게 leave 이벤트 알림
 
## 실행 방법
이 프로젝트는 Spring Boot 기반 프로그램입니다.<br>
로컬 환경에서 실행하려면 JDK 21버전이 추천되며, Maven 또는 Gradle이 필요합니다.<br>
클라이언트와 연결해서 사용할 경우, 8080포트 localhost로 시작하면 됩니다.

```bash
# Signal 서버 디렉토리 이동
cd signalServer

# 빌드 및 실행 (Maven)
./mvnw clean package
./mvnw spring-boot:run

# 빌드 및 실행(Gradle)
./gradlew build
./gradlew bootRun
```

## 구현 기능 목록
- [x] 참가자 등록 api 구현
- [x] 웹 소켓 연결 구현
  - [x] 웹 소켓 CORS 설정
  - [x] 사용자 id 등록
  - [x] 연결 해제 handler 등록
- [x] 시그널(join, offer, answer, ice) 구현
  - [x] 참가자 등록 및 참가자 정보 반환
  - [x] offer/answer에 대한 screenSender 여부 조회 및 전달
  - [x] ice 후보 전달
  - [x] 화면 공유 등록 및 참가자 정보 반환
- [x] 리액션 구현
  - [x] 참가자 채팅 구현
  - [x] 참가자 장치 on/off 상태 전달 구현
  - [x] 이모지 구현
  - [x] 손들기 구현
- [x] 강제 종료용 api 구현
- [x] 테스트 코드 작성 
