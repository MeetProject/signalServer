# signalServer

이 레포는 WebRTC P2P 연결을 위한 시그널링 서버(Signaling Server)를 Spring Boot 기반 WebSocket으로 구현한 프로젝트입니다.<br>
클라이언트 간 직접 미디어를 주고받기 위해 필요한 SDP(offer/answer)와 ICE Candidate를 교환하는 중간 역할을 담당합니다.<br>
<br>
본 프로젝트에서는 별도의 데이터베이스를 연결하지 않고 로컬 단위에서 실행가능을 목표로 진행합니다.<br>

## 기능 개요
### 방 관리
- 사용자는 특정 roomId로 입장(join)
- 서버는 해당 방의 참가자 목록을 관리

### Offer / Answer 교환
- 참여자가 생성한 offer를 서버는 대상 사용자에게 전달
- 대상 사용자는 peerConnection을 생성하고 answer를 다시 서버로 전송
- 대상 사용자가 생성한 peerConnection에 대한 Answer를 서버는 보낸 사용자에게 전달

### ICE Candidate 중계
- 양쪽 PeerConnection에서 생성된 ICE 후보를 상대에게 전달

### 채팅, 리액션 관리
- 사용자는 같은 방에 있는 모든 사용자에게 전송 가능
- 서버는 chat/reaction 이벤트를 브로드캐스트하여 모든 참여자에게 전달

### 연결 종료 처리
- 탭 닫힘, 새로고침, 네트워크 끊김 등으로 WebSocket이 종료
  - 서버는 자동으로 방에서 해당 사용자 제거
  - 남아있는 사용자에게 leave 이벤트 알림
 
