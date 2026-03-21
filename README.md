# signalServer
본 레포지토리는 **Mediasoup SFU** 아키텍처 기반의 실시간 화상 회의를 지원하기 위한 **Signaling Server**입니다.<br>
Spring Boot와 STOMP(WebSocket)를 활용하여 클라이언트의 세션 관리, 미디어 서버(Node.js)와의 파라미터 중계, 그리고 비미디어 데이터(채팅, 상태)의 브로드캐스트를 담당합니다.

## 역할
- Mediasoup 세션 중계
  - 클라이언트-미디어 서버 간 RtpCapabilities, Transport 파라미터 교환 중계
  - 클라이언트의 Producer/Consumer 연결 요청 및 스트림 식별자 관리

- 룸(Room) 관리
  - 룸 생성, 참여자 입장/퇴장 세션 관리 및 동기화
  - 퇴장 시 미디어 서버에 해당 유저의 자원 해제(Cleanup) 명령 하달

- 미디어 제어 시그널링
  - Pause / Resume 요청을 미디어 서버로 전달하여 클라이언트 네트워크 대역폭 조절
  - 장치(카메라/마이크) 상태 변경 시 실시간 상태 동기화

- 인터랙션 브로드캐스트
  - 채팅, 이모지, 손들기 이벤트 및 실시간 상태값 전체 참여자에게 전파

## 전체 구조
클라이언트가 WebSocket 연결 후 STOMP 프로토콜로 메시지를 publish/subscribe하며,
서버는 이를 SimpleBroker 또는 1:1 직접 전송 방식으로 전달합니다.

```scss
Client (Next.js)
    │
    ├── [HTTP] ──────────────────────┐
    │   ▼                            ▼
    │ RoomController            UserController
    │ (방 생성/검증/퇴장)        (유저 등록/UUID 발급)
    │                                │
    └── [WebSocket / STOMP] ─────────┘
                ↓
    ┌──────────────────────────────┐
    │       Signaling Server       │
    ├──────────────────────────────┤
    │  Orchestration Services      │
    │  - JoinOrchestrationService  │──▶ [참여/기존 참여자 목록 전송]
    │  - LeaveOrchestrationService │──▶ [자원 정리/퇴장 브로드캐스트]
    └───────────────┬──────────────┘
                    ↓
    ┌───────────────┴──────────────┐
    │      Socket Controllers      │
    ├──────────────────────────────┤
    │ 1. MediaSessionController    │──▶ [Mediasoup 자원 중계]
    │ 2. RoomInteractionController │──▶ [채팅/이모지/상태 전파]
    └───────────────┬──────────────┘
                    ↓
          ┌──────────────────┐
          │ Messaging Layer  │
          └────────┬─────────┘
          ↙                ↘
  Media Server (Node.js)    MessageBroker (Simple)
   [SFU 리소스 제어]         [유저간 실시간 브로드캐스트]
```

## 주요 기능
### 룸 및 사용자 세션 관리 (REST & Socket)
- 방 생성 및 검증: REST API를 통해 고유 roomId를 생성하고, 입장 전 방의 존재 여부와 최대 인원(RoomRule)을 검증합니다.
- UUID 기반 식별자 발급: 유저 등록 시 고유한 userId를 생성하여 세션 전체에서 식별자로 활용하며, UserService를 통해 중앙 관리합니다.
- 지능형 참여(Join) 로직: 신규 참여자 입장 시, 기존 방에 참여 중인 **참여자 리스트(List<Participant>)**를 즉시 응답하여 초기 화면 렌더링을 지원합니다.

### Mediasoup 미디어 세션 중계 (Media Path)
- SFU 파라미터 핸들링: 클라이언트와 미디어 서버 사이에서 Capabilities, DTLS, ConsumerParams 등 WebRTC 연결에 필수적인 파라미터를 중계합니다.
- RTLS (Producer 등록): 사용자가 스트림을 송출하기 시작하면 서버는 해당 producerId를 룸 세션에 등록하고, 다른 모든 참여자에게 새로운 미디어의 등장을 실시간으로 알립니다.
- 동적 스트림 제어: 클라이언트의 요청에 따라 특정 Consumer의 미디어를 Pause하거나 Resume 하도록 미디어 서버에 명령을 전달합니다.

### 미디어 자원 관리 및 최적화
- Producer 추적: RoomsService를 통해 각 유저가 생성한 비디오/오디오 트랙(producerId)을 매핑하여 관리합니다.
- 자원 회수(Remove Track): 특정 트랙(예: 화면 공유 종료)만 제거할 경우, 룸 상태에서 해당 ID를 삭제하고 미디어 서버에도 자원 해제를 요청합니다.

### 실시간 인터랙션 및 상태 공유 (Interaction Path)
- 채팅 및 리액션: STOMP 브로커를 활용하여 텍스트 메시지와 이모지(Emoji) 효과를 룸 내 전체 유저에게 저지연으로 전파합니다.
- 장치 상태 동기화: 참여자의 마이크/카메라 On-Off 상태 변화를 추적하고 브로드캐스트하여 모든 유저의 UI가 동일한 상태를 유지하도록 합니다.
- 손들기(HandUp) 관리: 발표 의사 표현인 손들기 이벤트를 관리하여 화상 회의의 원활한 진행을 돕습니다.

### 연결 종료 및 자동 정리 (Cleanup)
- 오케스트레이션 기반 퇴장: 사용자의 명시적 퇴장(leave) 요청 시, LeaveOrchestrationService를 통해 룸 세션 정리와 참여자 알림을 일괄 처리합니다.
- 비정상 종료 감지: 네트워크 단절이나 탭 닫힘 등으로 웹소켓 연결이 끊길 경우, 서버는 자동으로 방에서 사용자를 제거하고 미디어 서버에 할당된 자원을 해제하여 메모리 누수를 방지합니다.
 
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
